package algorithms.tracesearch;

public class FirstByIndexLocatorResultsMerger implements ILocatorResultMerger {

    public int[] mergeResults;

    @Override
    public int[] merge(int[] suitableTraces) {
        if (mergeResults == null) {
            mergeResults = suitableTraces;
            return mergeResults;
        }

        int[] mergedArr = new int[mergeResults.length];

        for (int i = 0; i < mergeResults.length; i++) {

            int currMergedValue = mergeResults[i];
            int currSuitableValue = suitableTraces[i];

            if (currMergedValue == currSuitableValue) {
                mergedArr[i] = currMergedValue;
            } else {
                int mergedValIndex = getValueIndexInArr(mergeResults, currSuitableValue);
                int suitableValIndex = getValueIndexInArr(suitableTraces, currMergedValue);

                if (mergedValIndex >= suitableValIndex) {
                    mergedArr[i] = currMergedValue;
                } else {
                    mergedArr[i] = currSuitableValue;
                }
            }
        }
        return mergedArr;
    }

    private int getValueIndexInArr(int[] mergeResults, int targetValue) {

        int result = -1;
        for (int i = 0; i < mergeResults.length; i++) {
            if (mergeResults[i] == targetValue) {
                result = i;
                break;
            }
        }

        assert (result != -1);
        return result;
    }
}
