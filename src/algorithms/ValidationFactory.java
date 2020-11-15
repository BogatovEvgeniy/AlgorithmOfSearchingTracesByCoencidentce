package algorithms;

import algorithms.validators.MaximalCoincidenceValidator;
import org.deckfour.xes.model.XEvent;

import java.util.Map;

public class ValidationFactory {

    public static int maxAttributesCoincidences(XEvent baseEvent, XEvent compEvent) {
        return new MaximalCoincidenceValidator().validateCoincidenceByMap(baseEvent, compEvent);
    }

    public static float maxAttributesCoincidenceWeightCriteria(XEvent lastEventOfTrace, XEvent xEvent, Map<String, Float> attributeCoefficientMap) {
        return new MaximalCoincidenceValidator().validateCoincidenceByMap(lastEventOfTrace, xEvent, attributeCoefficientMap);
    }
}
