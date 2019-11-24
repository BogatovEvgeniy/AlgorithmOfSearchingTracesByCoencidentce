package algorithms.search.trace.locator.invariant;

public interface IRule {
    boolean isApplicableFor(String key);

    String getAttrKey();
}
