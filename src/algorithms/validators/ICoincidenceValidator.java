package algorithms.validators;

import org.deckfour.xes.model.XEvent;

import java.util.Map;

public interface ICoincidenceValidator {
    int validateCoincidence(XEvent base, XEvent comp);

    float validateCoincidence(XEvent lastEventOfTrace, XEvent xEvent, Map<String, Float> attributeCoefficientMap);
}
