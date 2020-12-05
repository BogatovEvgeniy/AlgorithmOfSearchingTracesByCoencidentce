package algorithms.search.trace.locator.invariant;

import algorithms.Utils;
import algorithms.search.trace.ITraceSearchingAlgorithm;
import algorithms.search.trace.locator.invariant.rule.log.Final;
import algorithms.search.trace.locator.invariant.rule.log.Initial;
import com.google.common.annotations.VisibleForTesting;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

import javax.annotation.Nonnull;
import java.util.*;

import static algorithms.search.trace.TraceSearchingAlgorithm.ADD_NEW_TRACE;
import static algorithms.search.trace.TraceSearchingAlgorithm.TRACE_UNDEFINED;


/**
 * While you are using this locator under the hood there is an assumption that log is prepeared
 * and that atleast one even of traces in log has attribute equalt to one of invariant
 * <p>
 */
public class ByFirstTraceCoincidenceInvariantsTraceLocator implements ITraceSearchingAlgorithm.TraceLocator {

    private static ILogValidator LOG_VALIDATOR_INSTANCE;
    private float minimalCoincidenceVal;
    private TraceInvariantList tree;

    public ByFirstTraceCoincidenceInvariantsTraceLocator(float minimalCoincidenceVal, TraceInvariantList tree) {
        this.minimalCoincidenceVal = minimalCoincidenceVal;
        this.tree = tree;
    }

    @VisibleForTesting
    @Override
    public int[] defineSuitableTracesList(XLog resultLog, XEvent event) {
        if (resultLog.isEmpty()) {
            return TRACE_UNDEFINED;
        } else {
            return getMaxCoincidenceTraceIndexByAttr(resultLog, event);
        }
    }

    @VisibleForTesting
    private int[] getMaxCoincidenceTraceIndexByAttr(XLog resultLog, XEvent event) {
        XAttributeMap getEventAttributes = event.getAttributes();
        Map<Integer, Float> traceAttributesCoincidenceValues = new HashMap<>();

        if (isEventInitial(event, tree)){
            return ADD_NEW_TRACE;
        }

        for (String key : getEventAttributes.keySet()) {
            String eventVal = event.getAttributes().get(key).toString();
            List<IRule> ruleList = tree.getRuleSetPerKey(key);

            if (ruleList == null || ruleList.isEmpty()) {
                continue;
            }


            List<Integer> suitableTraceIndexes;
            @Nonnull List<String> preValues = definePossiblePreviousValues(resultLog, ruleList, eventVal);

            if (preValues == null || preValues.isEmpty()) {
                return TRACE_UNDEFINED;
            }

            suitableTraceIndexes = defineSuitableTraceIndex(resultLog, preValues, key);
            calculateCoincidencePerTraceIndex(traceAttributesCoincidenceValues, suitableTraceIndexes);
        }

        traceAttributesCoincidenceValues = removeFinalizedTraces(resultLog, traceAttributesCoincidenceValues, event);

        if (traceAttributesCoincidenceValues.isEmpty()) {
            return ADD_NEW_TRACE;
        } else {
            return returnSortedResults(traceAttributesCoincidenceValues);
        }
    }

    public boolean isEventInitial(XEvent event, TraceInvariantList invariantList) {
        int[] result = TRACE_UNDEFINED;
        for (String key : event.getAttributes().keySet()) {

            List<Initial> initialRules = invariantList.getInitialEvents(key);
            if (initialRules == null || initialRules.isEmpty()) {
                continue;
            }

            for (Initial rule : initialRules) {
                if (rule.isInitialState(event)) {
                    result = ADD_NEW_TRACE;
                    break;
                }
            }
        }
        return result == ADD_NEW_TRACE;
    }

    private Map<Integer, Float> removeFinalizedTraces(XLog resultLog, Map<Integer, Float> traceAttributesCoincidenceValues, XEvent event) {
        Map<Integer, Float> result = new HashMap<>();
        Set<Integer> nonFinalizedTraces = new HashSet<>();
        for (String attrKey : event.getAttributes().keySet()) {
            List<Final> finalRules = tree.getFinalEvents(attrKey);

            if (finalRules == null) {
                continue;
            }
            nonFinalizedTraces = defineNonFinalizedTraceIndexesPerAttribute(finalRules, resultLog, traceAttributesCoincidenceValues, nonFinalizedTraces);
        }

        // Copy nonfinalized traces and their coincidence values to result
        for (Integer integer : traceAttributesCoincidenceValues.keySet()) {
            if (nonFinalizedTraces.contains(integer)) {
                result.put(integer, traceAttributesCoincidenceValues.get(integer));
            }
        }
        return result;
    }

    private Set<Integer> defineNonFinalizedTraceIndexesPerAttribute(List<Final> finalRules, XLog resultLog, Map<Integer, Float> traceAttributesCoincidenceValues, Set<Integer> nonFinalizedTraces) {

        Set<Integer> perAttrNonFinalizedTraceIndexes = null;
        for (Final finalRule : finalRules) {
            perAttrNonFinalizedTraceIndexes = defineAllowedTracesPerRule(resultLog, traceAttributesCoincidenceValues, perAttrNonFinalizedTraceIndexes, finalRule);
        }


        if (nonFinalizedTraces.isEmpty()) {
            nonFinalizedTraces = perAttrNonFinalizedTraceIndexes;
        } else {
            nonFinalizedTraces.retainAll(perAttrNonFinalizedTraceIndexes);
        }
        return nonFinalizedTraces;
    }

    private Set<Integer> defineAllowedTracesPerRule(XLog resultLog, Map<Integer, Float> traceAttributesCoincidenceValues, Set<Integer> perAttrNonFinalizedTraceIndexes, Final finalRule) {
        Set<Integer> perRuleAllowedTraces = finalRule.removeFinalizedTraces(resultLog, traceAttributesCoincidenceValues.keySet());
        if (perAttrNonFinalizedTraceIndexes == null) {
            perAttrNonFinalizedTraceIndexes = perRuleAllowedTraces;
        } else {
            perAttrNonFinalizedTraceIndexes.retainAll(perRuleAllowedTraces);
        }
        return perAttrNonFinalizedTraceIndexes;
    }

    private List<Integer> defineSuitableTraceIndex(XLog resultLog, List<String> preValues, String key) {
        List<Integer> result = new LinkedList<>();
        for (int traceIndex = 0; traceIndex < resultLog.size(); traceIndex++) {
            int lastEventIndex = resultLog.get(traceIndex).size() - 1;

            String attrValue = resultLog.get(traceIndex).get(lastEventIndex).getAttributes().get(key).toString();

            if (preValues.contains(attrValue)) {
                result.add(traceIndex);
            }
        }
        return result;
    }

    private void calculateCoincidencePerTraceIndex(Map<Integer, Float> traceAttributesCoincidenceValues, List<Integer> suitableTraceIndex) {
        if (suitableTraceIndex != null && !suitableTraceIndex.isEmpty()) {
            for (Integer index : suitableTraceIndex) {
                Float value = traceAttributesCoincidenceValues.get(index);
                if (value != null) {
                    value++;
                } else {
                    value = 1F;
                }
                traceAttributesCoincidenceValues.put(index, value);
            }
        }
    }

    private int[] returnSortedResults(Map<Integer, Float> traceAttributesCoincidenceValues) {
        if (traceAttributesCoincidenceValues.isEmpty()) {
            return TRACE_UNDEFINED;
        } else {
            return removeTracesBelowCoincidence(traceAttributesCoincidenceValues, minimalCoincidenceVal);
        }
    }

    private int[] removeTracesBelowCoincidence(Map<Integer, Float> traceIndexes, float minimalCoincidenceVal) {
        List<Integer> result = new LinkedList<>();
        int attrRulesCount = tree.countOfAttributesUnderRule();
        for (Integer key : traceIndexes.keySet()) {
            if ((traceIndexes.get(key) / attrRulesCount) > minimalCoincidenceVal) {
                result.add(key);
            }
        }

        if (result.size() > 0) {
            return Utils.toPrimitives(result);
        } else {
            return TRACE_UNDEFINED;
        }
    }

    @VisibleForTesting
    private List<String> definePossiblePreviousValues(XLog resultLog, List<IRule> ruleList, String eventVal) {
        List<String> nextValues = new LinkedList<>();
        if (ruleList == null && ruleList.isEmpty()) {
            return null;
        }

        for (IRule iRule : ruleList) {
            if (iRule instanceof IEventRule) {
                nextValues.addAll(((IEventRule) iRule).getPossiblePreValues(eventVal));
            } else if (iRule instanceof ITraceRule) {
                nextValues.addAll(((ITraceRule) iRule).getPossiblePreValues(resultLog, eventVal));
            }
        }

        return removeDuplicates(nextValues);
    }

    private List<String> removeDuplicates(List<String> nextValues) {
        List<String> result = new LinkedList<>();
        for (String nextValue : nextValues) {
            if (!result.contains(nextValue)) {
                result.add(nextValue);
            }
        }
        return result;
    }

    @Override
    public ILogValidator getLogValidator() {
        if (LOG_VALIDATOR_INSTANCE == null) {
            LOG_VALIDATOR_INSTANCE = new InvariantLogValidator(tree);
        }
        return LOG_VALIDATOR_INSTANCE;
    }
}
