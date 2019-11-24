package algorithms.search.trace.locator.invariant.rule.event;

import algorithms.search.trace.locator.invariant.IEventRule;
import algorithms.search.trace.locator.invariant.rule.BaseRule;

import java.util.List;

public class Any extends BaseRule implements IEventRule {

    private String previous;

    public Any(String attrKey, String previous) {
        super(attrKey);
        this.previous = previous;
    }

    @Override
    public List<String> getPossiblePreValues(String eventVal) {
        return null;
    }

    @Override
    public boolean isApplicableFor(String key) {
        return attrKey.equals(key);
    }
}
