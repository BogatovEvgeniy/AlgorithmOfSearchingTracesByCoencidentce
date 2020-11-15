package algorithms.search.trace.locator.invariant.rule.trace;

import algorithms.search.trace.locator.invariant.ITraceRule;
import algorithms.search.trace.locator.invariant.rule.BaseRule;
import com.google.common.collect.Lists;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.List;

public class Same extends BaseRule implements ITraceRule {

    private String attrKey;

    public Same(String attrKey) {
        super(attrKey);
        this.attrKey = attrKey;
    }

    @Override
    public boolean isApplicableFor(String key) {
        return attrKey.equals(key);
    }

    @Override
    public List<String> getPossiblePreValues(XLog resultLog, String eventVal) {
        for (XTrace xTrace : resultLog) {
            int lastEventIndexInTrace = xTrace.size() - 1;
            XEvent lastEvent = xTrace.get(lastEventIndexInTrace);
             if (lastEvent.getAttributes().get(attrKey).toString().equals(eventVal)) {
                 return Lists.newArrayList(eventVal);
             }
        }
        return Lists.newArrayList();
    }
}
