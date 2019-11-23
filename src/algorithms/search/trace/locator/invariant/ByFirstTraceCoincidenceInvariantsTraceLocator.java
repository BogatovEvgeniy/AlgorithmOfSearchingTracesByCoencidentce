package algorithms.search.trace.locator.invariant;

import algorithms.Utils;
import algorithms.preprocess.InvariantInitialEventSearchAlgorithm;
import algorithms.search.trace.ITraceSearchingAlgorithm;
import com.google.common.annotations.VisibleForTesting;
import org.deckfour.xes.model.*;

import javax.annotation.Nonnull;
import java.util.*;


/**
 * While you are using this locator under the hood there is an assumption that log is prepeared
 * and that atleast one even of traces in log has attribute equalt to one of invariant
 * <p>
 * To prepare log for using this locator use {@link InvariantInitialEventSearchAlgorithm}
 */
public class ByFirstTraceCoincidenceInvariantsTraceLocator implements ITraceSearchingAlgorithm.TraceLocator {

    public static final int UNDEFINED_INDEX = -1;
    private static ILogValidator LOG_VALIDATOR_INSTANCE;
    private float minimalCoincidenceVal;
    private Map<String, Float> traiceCoinsidences = new HashMap<>();
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
            List<TraceInvariantList.IRule> ruleList = tree.getRuleSetPerKey(key);
            @Nonnull List<String> preValues = definePossiblePreviousValues(ruleList, eventVal);

            int suitableTraceIndex = UNDEFINED_INDEX;
            if (!preValues.isEmpty()) {
                suitableTraceIndex = defineSuitableTraceIndex(resultLog, preValues, key);
            }

            calculateCoincidencePerTraceIndex(traceAttributesCoincidenceValues, suitableTraceIndex);
        }
        return returnSortedResults(traceAttributesCoincidenceValues);
    }

    private int defineSuitableTraceIndex(XLog resultLog, List<String> preValues, String key) {
        int result = UNDEFINED_INDEX;
        for (int traceIndex = 0; traceIndex < resultLog.size(); traceIndex++) {
            int lastEventIndex = resultLog.get(traceIndex).size();

            String attrValue = resultLog.get(traceIndex).get(lastEventIndex).getAttributes().get(key).toString();
            if (preValues.contains(attrValue)) {
                result = traceIndex;
                break;
            }
        }
        return result;
    }

    private void calculateCoincidencePerTraceIndex(Map<Integer, Float> traceAttributesCoincidenceValues, int suitableTraceIndex) {
        if (suitableTraceIndex != UNDEFINED_INDEX) {
            Float value = traceAttributesCoincidenceValues.get(suitableTraceIndex);
            if (value != null) {
                value++;
            } else {
                value = 1F;
            }
            traceAttributesCoincidenceValues.put(suitableTraceIndex, value);
        }
    }

    private int[] returnSortedResults(Map<Integer, Float> traceAttributesCoincidenceValues) {
        if (traceAttributesCoincidenceValues.isEmpty()) {
            return new int[]{0};
        } else {
            return sortIndexesByValues(traceAttributesCoincidenceValues);
        }
    }

    @VisibleForTesting
    private List<String> definePossiblePreviousValues(List<TraceInvariantList.IRule> ruleList, String eventVal) {
        List<String> preValues = new LinkedList<>();
        if (ruleList == null && ruleList.isEmpty()) {
            return null;
        }


        for (TraceInvariantList.IRule iRule : ruleList) {
            preValues.addAll(iRule.getPossiblePreValues(eventVal));
        }

        return preValues;
    }

    @VisibleForTesting
    private int[] sortIndexesByValues(Map<Integer, Float> traceCoincidenceMap) {
        Map<Integer, Float> sortedMap = Utils.sortMap(traceCoincidenceMap);
        return Utils.toPrimitives(sortedMap.keySet());
    }

    @Override
    public ILogValidator getLogValidator() {
        if (LOG_VALIDATOR_INSTANCE == null) {
            LOG_VALIDATOR_INSTANCE = new InvariantLogValidator(tree);
        }
        return LOG_VALIDATOR_INSTANCE;
    }
}
