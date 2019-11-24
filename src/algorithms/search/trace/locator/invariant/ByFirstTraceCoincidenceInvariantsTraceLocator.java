package algorithms.search.trace.locator.invariant;

import algorithms.Utils;
import algorithms.search.trace.ITraceSearchingAlgorithm;
import algorithms.search.trace.locator.invariant.rule.event.Any;
import com.google.common.annotations.VisibleForTesting;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

import javax.annotation.Nonnull;
import java.util.*;

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

    @Override
    public String getId() {
        return getClass().getSimpleName();
    }

    @VisibleForTesting
    @Override
    public int[] defineSuitableTracesList(XLog resultLog, XEvent event) {
        if (resultLog.isEmpty()) {
            return new int[]{0};
        } else {
            return getMaxCoincidenceTraceIndexByAttr(resultLog, event);
        }
    }

    @VisibleForTesting
    private int[] getMaxCoincidenceTraceIndexByAttr(XLog resultLog, XEvent event) {
        XAttributeMap getEventAttributes = event.getAttributes();
        Map<Integer, Float> traceAttributesCoincidenceValues = new HashMap<>();
        for (String key : getEventAttributes.keySet()) {
            String eventVal = event.getAttributes().get(key).toString();
            List<IRule> ruleList = tree.getRuleSetPerKey(key);

            if (ruleList == null || ruleList.isEmpty()) {
                continue;
            }

            List<Integer> suitableTraceIndex;
            if (ruleListContainsAny(ruleList)) {
                suitableTraceIndex = Collections.nCopies(resultLog.size() - 1, 1);
            } else {

                @Nonnull List<String> preValues = definePossiblePreviousValues(resultLog, ruleList, eventVal);

                if (preValues == null || preValues.isEmpty()) {
                    return TRACE_UNDEFINED;
                }

                suitableTraceIndex = defineSuitableTraceIndex(resultLog, preValues, key);
            }

            calculateCoincidencePerTraceIndex(traceAttributesCoincidenceValues, suitableTraceIndex);
        }
        return returnSortedResults(traceAttributesCoincidenceValues);
    }

    private boolean ruleListContainsAny(List<IRule> ruleList) {
        for (IRule iRule : ruleList) {
            if (iRule instanceof Any) {
                return true;
            }
        }
        return false;
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
            return new int[]{0};
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

        return Utils.toPrimitives(result);
    }

    @VisibleForTesting
    private List<String> definePossiblePreviousValues(XLog resultLog, List<IRule> ruleList, String eventVal) {
        List<String> preValues = new LinkedList<>();
        if (ruleList == null && ruleList.isEmpty()) {
            return null;
        }


        for (IRule iRule : ruleList) {
            if (iRule instanceof IEventRule) {
                preValues.addAll(((IEventRule) iRule).getPossiblePreValues(eventVal));
            } else {
                preValues.addAll(((ITraceRule) iRule).getPossiblePreValues(resultLog, eventVal));
            }
        }

        return preValues;
    }

    @Override
    public ILogValidator getLogValidator() {
        if (LOG_VALIDATOR_INSTANCE == null) {
            LOG_VALIDATOR_INSTANCE = new InvariantLogValidator(tree);
        }
        return LOG_VALIDATOR_INSTANCE;
    }
}
