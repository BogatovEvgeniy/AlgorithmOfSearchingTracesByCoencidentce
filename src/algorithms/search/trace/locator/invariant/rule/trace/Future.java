package algorithms.search.trace.locator.invariant.rule.trace;

import algorithms.search.trace.locator.invariant.ITraceRule;
import algorithms.search.trace.locator.invariant.rule.BaseRule;
import org.deckfour.xes.model.XLog;

import java.util.List;

public class Future extends BaseRule implements ITraceRule {

    public Future(String attrKey) {
        super(attrKey);
    }

    @Override
    public boolean isApplicableFor(String key) {
        return false;
    }

    @Override
    public List<String> getPossiblePreValues(XLog resultLog, String eventVal) {
        return null;
    }
}
