package algorithms.tracesearch;

public abstract class SumLocatorResultsMerger implements ILocatorResultMerger {

    public int[] mergeResults;

    @Override
    public int[] merge(int[] suitableTraces) {
        if (mergeResults == null || mergeResults.length == 0) {
            return mergeResults;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
