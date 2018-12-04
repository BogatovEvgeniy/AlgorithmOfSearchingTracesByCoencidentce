package algorithms.search.trace;


import javafx.util.Pair;
import org.deckfour.xes.model.XLog;

import java.util.List;
import java.util.Set;

public class PredefibedAttributeWeightsSearchAlgorithm extends BaseWeightSearchAlgorithm {

    public PredefibedAttributeWeightsSearchAlgorithm(int stepSizeInRange,
                                                     int maxAllowedFails,
                                                     float minimalCoincidence,
                                                     List<List<String>> attributeSets) {

        super(stepSizeInRange, maxAllowedFails, minimalCoincidence);
        this.attributeSets = attributeSets;

        System.out.println("Config:" + this.toString());
    }

    @Override
    List<List<String>> getAttributeSet(XLog log, int windowIndex, int windowSize) {
        return attributeSets;
    }
}

