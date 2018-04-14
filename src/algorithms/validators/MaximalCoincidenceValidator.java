package algorithms.validators;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;

import java.util.Set;

public class MaximalCoincidenceValidator implements ICoincidenceValidator {

    @Override
    public int validateCoincidence(XEvent base, XEvent compare) {
        int coincidenceCounter = 0;
        for (String baseKey : base.getAttributes().keySet()) {
            for (String compareKey : compare.getAttributes().keySet()) {
                if (baseKey.equals(compareKey)
                        && base.getAttributes().get(baseKey).toString().equals(compare.getAttributes().get(compareKey).toString())) {
                    ++ coincidenceCounter;
                }
            }
        }
        return coincidenceCounter;
    }
}
