package algorithms.search.trace.locator.invariant.rule.event;

import algorithms.search.trace.locator.invariant.IEventRule;
import algorithms.search.trace.locator.invariant.rule.BaseRule;

import javax.annotation.Nonnull;
import java.util.List;

public class And extends BaseRule implements IEventRule {

    private final String previous;
    protected List<String> next;

    public And(@Nonnull String attrKey, @Nonnull String previous, @Nonnull List<String> next) {
        super(attrKey);
        this.previous = previous;
        this.next = next;
    }

    @Override
    public boolean isApplicableFor(String key) {
        return attrKey.equals(key);
    }

    @Override
    public List<String> getPossiblePreValues(String eventVal) {
        List<String> result = next;
        if (next.contains(eventVal)) {
            result.remove(eventVal);
            result.add(previous);
        }
        return result;
    }
}