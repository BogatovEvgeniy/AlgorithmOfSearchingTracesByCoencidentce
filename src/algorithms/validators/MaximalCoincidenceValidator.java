package algorithms.validators;

import org.deckfour.xes.model.XEvent;

import java.util.Map;

public class MaximalCoincidenceValidator implements ICoincidenceValidator {

    @Override
    public int validateCoincidenceByMap(XEvent base, XEvent compare) {
        int coincidenceCounter = 0;
        for (String baseKey : base.getAttributes().keySet()) {
            for (String compareKey : compare.getAttributes().keySet()) {
                if (baseKey.equals(compareKey)
                        && base.getAttributes().get(baseKey).toString().equals(compare.getAttributes().get(compareKey).toString())) {
                    ++coincidenceCounter;
                }
            }
        }
        return coincidenceCounter;
    }

    @Override
    public float validateCoincidenceByMap(XEvent base, XEvent compare, Map<String, Float> attributeCoefficientMap) {
        float coincidenceCounter = 0;
        for (String baseKey : base.getAttributes().keySet()) {
            for (String compareKey : compare.getAttributes().keySet()) {
                if (baseKey.equals(compareKey)
                        && base.getAttributes().get(baseKey).toString().equals(compare.getAttributes().get(compareKey).toString())) {
                    Float coincidence = attributeCoefficientMap.get(baseKey);
                    coincidenceCounter += coincidence == null ? 0 : coincidence;
                }
            }
        }
        return coincidenceCounter;
    }

}
