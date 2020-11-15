package algorithms.search.trace;


import org.deckfour.xes.model.XLog;

import java.util.LinkedList;
import java.util.List;

public class PredefinedAttributeWeightsSearchAlgorithm extends BaseWeightSearchAlgorithm {

    public PredefinedAttributeWeightsSearchAlgorithm(int stepSizeInRange,
                                                     int maxAllowedFails,
                                                     float minimalCoincidence,
                                                     List<List<String>> attributeSets) {

        super(stepSizeInRange, maxAllowedFails, minimalCoincidence);
        this.attributeSets = attributeSets;

        System.out.println("Config:" + this.toString());
    }

    public static PredefinedAttributeWeightsSearchAlgorithm init(List<List<String>> attributeSets){
        return new PredefinedAttributeWeightsSearchAlgorithm(3,
                PredefinedAttributeWeightsSearchAlgorithm.FAIL_COUNT_UNLIMITED,
                0.0f,
                attributeSets);
    }

    @Override
    List<List<String>> getAttributeSet(XLog log, int windowIndex, int windowSize) {
        return attributeSets;
    }
}

