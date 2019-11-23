package algorithms.search.trace.locator.invariant;

import java.util.*;

/**
 * This a tree structure where:
 * root is a collection of Rules
 */
public class TraceInvariantList {
    private List<IRule> rules;

    public TraceInvariantList() {
        rules = new ArrayList<IRule>();
    }

    public void addInvariantNode(IRule rule) {
        rules.add(rule);
    }

    public void insertOrReplaceInvariant(IRule rule) {
        rules.remove(rule);
        rules.add(rule);
    }

    public List<IRule> getRuleSetPerKey(String key) {
        List<IRule> resultSet = new ArrayList<>();
        for (IRule rule : rules) {
            if (rule.isApplicableFor(key)) resultSet.add(rule);
        }

        return resultSet;
    }

    public void clear() {
        rules.clear();
    }

    public int size() {
        return rules.size();
    }

    interface IRule {
        boolean isApplicableFor(String key);

        List<String> getPossiblePreValues(String eventVal);
    }
}