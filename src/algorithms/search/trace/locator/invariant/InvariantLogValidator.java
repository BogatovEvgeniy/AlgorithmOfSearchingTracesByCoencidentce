package algorithms.search.trace.locator.invariant;

import algorithms.search.trace.ITraceSearchingAlgorithm;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.*;
import java.util.stream.Collectors;

public class InvariantLogValidator implements ITraceSearchingAlgorithm.TraceLocator.ILogValidator {

    private TraceInvariantList invariantTree;

    public InvariantLogValidator(TraceInvariantList invariantTree) {
        this.invariantTree = invariantTree;
    }

    @Override
    public boolean isValid(XLog xLog) throws IllegalArgumentException {
        List<String> skippedKeys = new LinkedList();
        Set<String> attributesKeySet = xLog.get(0).get(0).getAttributes().keySet();
        for (XTrace trace : xLog) {
            for (XEvent xEvent : trace) {
                attributesKeySet = xEvent.getAttributes().keySet()
                        .stream()
                        .distinct()
                        .filter(attributesKeySet::contains)
                        .collect(Collectors.toSet());
            }
        }

        for (String key : attributesKeySet) {
            List<IRule> ruleSetPerKey = invariantTree.getRuleSetPerKey(key);
            if (ruleSetPerKey != null && !ruleSetPerKey.isEmpty()) {
                continue;
            } else {
                skippedKeys.add(key);
            }
        }

        if (skippedKeys.size() == attributesKeySet.size()) {
            throw new IllegalArgumentException("The log doesn't contain all Attributes mentioned in InvariantTree. Skipped keys:"
                    + Arrays.toString(skippedKeys.toArray()));
        } else {
            return true;
        }
    }
}
