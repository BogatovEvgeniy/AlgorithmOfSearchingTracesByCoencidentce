package algorithms.search.trace.locator.invariant.rule.event;

import algorithms.search.trace.locator.invariant.IEventRule;
import algorithms.search.trace.locator.invariant.rule.BaseRule;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;

public class Then extends BaseRule implements IEventRule {

    private final String previous;
    protected String next;

    public Then(@Nonnull String attrKey, @Nonnull String previous, String next) {
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
        if (next == null) return new LinkedList<>();

        if (next.contains(eventVal)) {
            Lists.newArrayList(previous);
        }
        return Lists.newArrayList();
    }
}
