package algorithms.validators;

import org.deckfour.xes.model.XEvent;

public interface ICoincidenceValidator {
    int validateCoincidence(XEvent base, XEvent comp);
}
