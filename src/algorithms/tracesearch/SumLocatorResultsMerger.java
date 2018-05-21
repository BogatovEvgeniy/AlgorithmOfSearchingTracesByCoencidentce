package algorithms.tracesearch;

public abstract class SumLocatorResultsMerger implements ILocatorResultMerger {

    public int[] mergeResults;

    @Override
    public int[] merge(int[] suitableTraces) {
        throw new UnsupportedOperationException();
    }
}
