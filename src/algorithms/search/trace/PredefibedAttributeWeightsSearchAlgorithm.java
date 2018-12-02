package algorithms.search.trace;


import javafx.util.Pair;

import java.util.List;
import java.util.Set;

public class PredefibedAttributeWeightsSearchAlgorithm extends BaseWeightSearchAlgorithm {

    public PredefibedAttributeWeightsSearchAlgorithm(int stepSizeInRange,
                                                     int maxAllowedFails,
                                                     float minimalCoincidence,
                                                     Set<Pair<Integer, Integer>> rangeSet,
                                                     List<List<String>> attributeSets) {

        super(stepSizeInRange, maxAllowedFails, minimalCoincidence);
        this.rangeSet = rangeSet;
        this.attributeSets = attributeSets;

        System.out.println("Config:" + this.toString());
    }

    Set<Pair<Integer, Integer>> getRangeSet() {
        return rangeSet;
    }

    List<List<String>> getAttributeSet(Pair<Integer, Integer> firstLastIndexOfRange) {
        return attributeSets;
    }
}

