package algorithms.search.trace.locator.invariant.rule;

import algorithms.search.trace.locator.invariant.IRule;

public abstract class BaseRule implements IRule {

    protected String attrKey;

    public BaseRule(String attrKey) {
        this.attrKey = attrKey;
    }

    @Override
    public String getAttrKey() {
        return attrKey;
    }
}
