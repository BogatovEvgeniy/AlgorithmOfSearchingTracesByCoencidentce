package algorithms.search.trace.locator.invariant.rule.log;

import algorithms.search.trace.locator.invariant.rule.BaseRule;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Final extends BaseRule {

    private String previous;

    public Final(String attrKey, String previous) {
        super(attrKey);
        this.previous = previous;
    }

    @Override
    public boolean isApplicableFor(String key) {
        return attrKey.equals(key);
    }

    public Set<Integer> removeFinalizedTraces(XLog resultLog, Set<Integer> traceResults) {
        List<Integer> excludeList = new LinkedList<>();
        Set<Integer> perKeyResults = new HashSet<>(traceResults);
        for (int traceIndex = 0; traceIndex < resultLog.size(); traceIndex++) {
            int lastEventIndexInTrace = resultLog.get(traceIndex).size() - 1;
            XEvent lastEvent = resultLog.get(traceIndex).get(lastEventIndexInTrace);
            String eventVal = lastEvent.getAttributes().get(attrKey).toString();
            if (eventVal.equals(previous)) {
                if (!excludeList.contains(traceIndex)) {
                    excludeList.add(traceIndex);
                }
            }
        }

        perKeyResults.removeAll(excludeList);

        return perKeyResults;
    }
}
