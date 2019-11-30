package algorithms.search.trace.locator.invariant.rule;

import algorithms.search.trace.locator.invariant.IRule;
import org.deckfour.xes.model.XLog;

import java.util.Set;

public interface ILogRule extends IRule {
    Set<Integer> preProcessResults(XLog resultLog, Set<Integer> traceResults);
}
