package algorithms.search.trace.locator.invariant;

import algorithms.search.trace.locator.invariant.rule.log.Final;
import algorithms.search.trace.locator.invariant.rule.log.Initial;

import java.util.*;

/**
 * This a tree structure where:
 * root is a collection of Rules
 */
public class TraceInvariantList {
    private List<IEventRule> eventRules = new LinkedList<>();
    private List<ITraceRule> traceRules = new LinkedList<>();
    private Map<String, List<Initial>> initialEvents = new HashMap<>();
    private Map<String, List<Final>> finalEvents = new HashMap<>();


    public void addInvariantEventRule(IEventRule rule) {
        eventRules.add(rule);
    }

    public void addInvariantBatchEventRule(List<? extends IEventRule> rule) {
        eventRules.addAll(rule);
    }

    public <ITrRule extends ITraceRule> void addInvariantTraceRule(ITrRule rule) {
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
            if (rulesPerAttr.contains(attrKey)) {
                continue;
            } else {
                rulesPerAttr.add(attrKey);
            }
        }

        return rulesPerAttr.size();
    }

    public void addInitialEvents(Initial initialEvent) {
        List<Initial> initialsPerKey = this.initialEvents.get(initialEvent.getAttrKey());
        if (initialsPerKey == null){
            initialsPerKey = new LinkedList<>();
        }
        initialsPerKey.add(initialEvent);
        initialEvents.put(initialEvent.getAttrKey(), initialsPerKey);
    }

    public void addFinalEvents(Final finalEvent) {
        List<Final> finalsPerKey = this.finalEvents.get(finalEvent.getAttrKey());
        if (finalsPerKey == null){
            finalsPerKey = new LinkedList<>();
        }
        finalsPerKey.add(finalEvent);
        finalEvents.put(finalEvent.getAttrKey(), finalsPerKey);
    }

    public List<Initial> getInitialEvents(String attrKey) {
        return initialEvents.get(attrKey);
    }

    public List<Final> getFinalEvents(String attrKey) {
        return finalEvents.get(attrKey);
    }
}