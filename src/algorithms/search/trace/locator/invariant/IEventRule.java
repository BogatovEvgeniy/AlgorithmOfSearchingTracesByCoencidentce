package algorithms.search.trace.locator.invariant;

import java.util.List;

public interface IEventRule extends IRule {
    List<String> getPossiblePreValues(String eventVal);
}