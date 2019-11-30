package algorithms.search.trace.locator.invariant.rule.trace;

import algorithms.search.trace.locator.invariant.ITraceRule;
import algorithms.search.trace.locator.invariant.rule.BaseRule;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.LinkedList;
import java.util.List;

public class Any extends BaseRule implements ITraceRule {

    private String previous;

    public Any(String attrKey, String previous) {
        super(attrKey);
        this.previous = previous;
    }

    @Override
    public List<String> getPossiblePreValues(XLog resultLog, String eventVal) {
        List<String> result = new LinkedList<>();
        for (XTrace xTrace : resultLog) {
            int lastEventIndexInTrace = xTrace.size() - 1;
            XEvent lastEvent = xTrace.get(lastEventIndexInTrace);
            if (lastEvent.getAttributes().get(attrKey).toString().equals(previous)) {
                if (!result.contains(eventVal)) {
                    result.add(lastEvent.getAttributes().get(attrKey).toString());
                }
            }
        }
        return result;
    }

    @Override
    public boolean isApplicableFor(String key) {
        return attrKey.equals(key);
    }
}
