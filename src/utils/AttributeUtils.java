package utils;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XEventImpl;

import java.util.LinkedList;
import java.util.List;

public class AttributeUtils {
    public static boolean isKeyValSetDifferent(List<XEvent> eventsPerAttributeSet, XEventImpl currEvent) {

        if (currEvent == null || eventsPerAttributeSet == null || eventsPerAttributeSet.size() == 0) {
            return false;
        }

        boolean isAbsent = false;
        for (XEvent xEvent : eventsPerAttributeSet) {


            XAttributeMap attributes = xEvent.getAttributes();
            for (String key : attributes.keySet()) {
                boolean isValuesEqual = attributes.get(key).equals(currEvent.getAttributes().get(key));
                if (isValuesEqual) {
                    isAbsent = false;
                } else {
                    isAbsent = true;
                    break;
                }
            }

            // Some absent key-value attribute was found
            if (isAbsent) {
                break;
            }
        }
        return isAbsent;
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
