package algorithms.search.trace.locator.invariant.rule.log;

import algorithms.search.trace.locator.invariant.rule.BaseRule;
import algorithms.search.trace.locator.invariant.rule.ILogRule;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Final extends BaseRule implements ILogRule {

    private String previous;

    public Final(String attrKey, String previous) {
        super(attrKey);
        this.previous = previous;
    }

    @Override
    public boolean isApplicableFor(String key) {
        return attrKey.equals(key);
    }

    @Override
    public Set<Integer> preProcessResults(XLog resultLog, Set<Integer> traceResults) {
        List<Integer> excludeList = new LinkedList<>();
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

        boolean result = traceResults.removeAll(excludeList);

        return traceResults;
    }
}
