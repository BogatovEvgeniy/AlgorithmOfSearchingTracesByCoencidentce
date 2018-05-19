package algorithms.validators;

import org.deckfour.xes.model.XEvent;

import java.util.Map;

public interface ICoincidenceValidator {
    int validateCoincidenceByMap(XEvent base, XEvent comp);

    float validateCoincidenceByMap(XEvent lastEventOfTrace, XEvent xEvent, Map<String, Float> attributeCoefficientMap);
}
