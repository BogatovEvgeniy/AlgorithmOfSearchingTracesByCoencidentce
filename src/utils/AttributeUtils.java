package utils;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XEventImpl;

import java.util.LinkedList;
import java.util.List;

public class AttributeUtils {
    public static boolean containsDuplicate(List<XEvent> eventsPerAttributeSet, XEventImpl currEvent) {

        boolean containsSameEvent = false;

        if (currEvent == null || eventsPerAttributeSet == null || eventsPerAttributeSet.size() == 0) {
            return true;
        }

        for (XEvent xEvent : eventsPerAttributeSet) {

            if (containsSameEvent){
                break;
            }

            XAttributeMap attributes = xEvent.getAttributes();
            boolean attributesComparision = false;
            for (String key : attributes.keySet()) {
                boolean isValuesEqual = attributes.get(key).equals(currEvent.getAttributes().get(key));
                // if difference was found stop search. Events are different
                if (isValuesEqual) {
                    attributesComparision = true;
                } else {
                    attributesComparision = false;
                    break;
                }
            }

            containsSameEvent = attributesComparision;
        }
        return containsSameEvent;
    }

    public static List<String> convertToValuesList(XAttributeMap valueSetPerAttr) {
        List<String> result = new LinkedList<>();

        for (String key : valueSetPerAttr.keySet()) {
            result.add(valueSetPerAttr.get(key).toString());
        }
        return result;
    }

    public static boolean eventListContainsEqualValues(List<XEvent> eventList, XAttributeMap valueSetPerAttr) {
        if (valueSetPerAttr == null || eventList == null || eventList.size() == 0) {
            return false;
        }

        boolean isExists = false;
        for (XEvent xEvent : eventList) {
            // Some equals event was found
            if (isExists) {
                return isExists;
            }

            for (String key : xEvent.getAttributes().keySet()) {
                boolean isValuesEqual = xEvent.getAttributes().get(key).equals(valueSetPerAttr.get(key));
                if (isValuesEqual) {
                    isExists = true;
                    break;
                } else {
                    isExists = false;
                    continue;
                }
            }
        }
        return isExists;
    }
}
