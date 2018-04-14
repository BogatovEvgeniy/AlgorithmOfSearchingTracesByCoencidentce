package algorithms;

import algorithms.validators.MaximalCoincidenceValidator;
import org.deckfour.xes.model.XEvent;

public class ValidationStrategy {

    public static int maxAttributesCoincidences(XEvent baseEvent, XEvent compEvent) {
        return new MaximalCoincidenceValidator().validateCoincidence(baseEvent, compEvent);
    }
}
