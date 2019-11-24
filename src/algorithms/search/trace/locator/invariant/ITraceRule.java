package algorithms.search.trace.locator.invariant;

import org.deckfour.xes.model.XLog;

import java.util.List;

public interface ITraceRule extends IRule {
    List<String> getPossiblePreValues(XLog resultLog, String eventVal);
}
