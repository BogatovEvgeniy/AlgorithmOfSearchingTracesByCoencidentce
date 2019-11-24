package algorithms.search.trace.locator.invariant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This a tree structure where:
 * root is a collection of Rules
 */
public class TraceInvariantList {
    private List<IEventRule> eventRules = new ArrayList<>();
    private List<ITraceRule> traceRules = new ArrayList<>();


    public void addInvariantEventRule(IEventRule rule) {
        eventRules.add(rule);
    }

    public void addInvariantBatchEventRule(List<? extends IEventRule> rule) {
        eventRules.addAll(rule);
    }

    public <ITrRule extends ITraceRule> void  addInvariantTraceRule(ITrRule rule) {
        traceRules.add(rule);
    }

    public void addInvariantBatchTraceRule(List<? extends ITraceRule> rule) {
        traceRules.addAll(rule);
    }


    public List<IRule> getRuleSetPerKey(String key) {
        List<IRule> resultSet = new ArrayList<>();
        for (IEventRule rule : eventRules) {
            if (rule.isApplicableFor(key)) resultSet.add(rule);
        }

        for (ITraceRule rule : traceRules) {
            if (rule.isApplicableFor(key)) resultSet.add(rule);
        }

        return resultSet;
    }

    public void clear() {
        eventRules.clear();
        traceRules.clear();
    }

    public int eventRulesCount() {
        return eventRules.size();
    }

    public int traceRulesCount() {
        return traceRules.size();
    }

    public int countOfAttributesUnderRule() {
        List<String> rulesPerAttr = new LinkedList<>();
        List<IRule> listOfRules = new LinkedList<>();
        listOfRules.addAll(eventRules);
        listOfRules.addAll(traceRules);
        for (IRule rule : listOfRules) {
            String attrKey = rule.getAttrKey();
            if (rulesPerAttr.contains(attrKey)){
                continue;
            } else {
                rulesPerAttr.add(attrKey);
            }
        }

        return rulesPerAttr.size();
    }
}