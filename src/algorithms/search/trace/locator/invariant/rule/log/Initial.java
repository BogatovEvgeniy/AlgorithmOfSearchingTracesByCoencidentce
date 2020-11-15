package algorithms.search.trace.locator.invariant.rule.log;

import algorithms.search.trace.locator.invariant.rule.BaseRule;
import org.deckfour.xes.model.XEvent;

public class Initial  extends BaseRule {

    private String previous;

    public Initial(String attrKey, String previous) {
        super(attrKey);
        this.previous = previous;
    }

    @Override
    public boolean isApplicableFor(String key) {
        return attrKey.equals(key);
    }

    public boolean isInitialState(XEvent event) {
       return event.getAttributes().get(attrKey).toString().equals(previous);
    }
}