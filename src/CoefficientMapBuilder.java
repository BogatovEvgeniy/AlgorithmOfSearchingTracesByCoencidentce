import algorithms.search.TraceValidator;
import javafx.util.Pair;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.io.File;
import java.util.*;

public class CoefficientMapBuilder {
    private final TraceValidator traceValidator;
    private File srcFile;
    private Map<String, Float> correctionMap;

    public CoefficientMapBuilder(File srcFile, Map<String, Float> correctionMap) {
        this.srcFile = srcFile;
        this.correctionMap = correctionMap;
        this.traceValidator = new TraceValidator();
    }


    public Map<String, Float> build() {
        Map<String, Float> attributeCoefficientMap = new HashMap<>();
        try {
            XesXmlParser xUniversalParser = new XesXmlParser();
            if (xUniversalParser.canParse(srcFile)) {
                List<XLog> parsedLog = xUniversalParser.parse(srcFile);
                if (traceValidator.validateIsEmpty(parsedLog)) return new HashMap<>();
                attributeCoefficientMap = prepareCoefficientMap(parsedLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return attributeCoefficientMap;
    }

    private Map<String, Float> prepareCoefficientMap(List<XLog> parsedLog) {
        Map<String, Float> attributeCoefficientMap = buildCoefficientMapForAttributes(parsedLog);
        attributeCoefficientMap = coefficientsCorrectionBaseOnIncomeData(correctionMap, attributeCoefficientMap);
        attributeCoefficientMap = balanceCoefficientsToValue(1, attributeCoefficientMap);
        System.out.println("Coefficient map: " + attributeCoefficientMap.toString());
        return attributeCoefficientMap;
    }

    private Map<String, Float> balanceCoefficientsToValue(float targetValue, Map<String, Float> attributeCoefficientMap) {
        Iterator<String> iterator = attributeCoefficientMap.keySet().iterator();
        float coefficientSum = 0;
        while (iterator.hasNext()) {
            coefficientSum += attributeCoefficientMap.get(iterator.next());
        }

        if (targetValue == coefficientSum) return attributeCoefficientMap;


        /**
         * The correction value calculates basing on expression below
         * t - target value usually equals to 1
         * n1...ni - the sum of coefficients in the map
         * x - the correction value used to make values in map that sum of them was equals target val
         *
         * t = x + (n1...ni)
         *
         * x = t - (ni...ni)
         *
         * t = (n1...ni)(x/(n1...ni) + 1)
         *
         */

        Map<String, Float> correctedMap = new HashMap<>();
        // calculate (x/(n1...ni) + 1)
        float correctionValue = ((targetValue - coefficientSum) / coefficientSum) + 1;

        iterator = attributeCoefficientMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            correctedMap.put(key, attributeCoefficientMap.get(key) * correctionValue);
        }

        return correctedMap;
    }

    private Map<String, Float> coefficientsCorrectionBaseOnIncomeData(Map<String, Float> correctionAtributesMap, Map<String, Float> attributeCoefficientMap) {
        Map<String, Float> resultMap = new HashMap<>();
        Iterator<String> iterator = attributeCoefficientMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            float value = attributeCoefficientMap.get(key);
            if (correctionAtributesMap.containsKey(key)) {
                value = correctionAtributesMap.get(key) * attributeCoefficientMap.get(key);
            }
            resultMap.put(key, value);
        }

        return resultMap.size() > 0 ? resultMap : attributeCoefficientMap;
    }

    private Map<String, Float> buildCoefficientMapForAttributes(List<XLog> parsedLog) {
        XTrace trace = parsedLog.get(0).get(0);
        XAttributeMap attributes = trace.get(0).getAttributes();
        Map<String, List<Pair<String, Integer>>> valuesMap = fillAttributeValuesMap(parsedLog, attributes);
        return calculateCoefficientMapForEachAttribute(valuesMap, trace.size(), attributes.size());
    }


    private Map<String, Float> calculateCoefficientMapForEachAttribute(Map<String, List<Pair<String, Integer>>> valuesMap, int eventsInLog, int attributesPerEvent) {
        Map<String, Float> resultMap = new HashMap<>();
        Map<String, Float> varietyPerAttrMap = new HashMap<>();

        float sumOfVarieties = 0;
        for (String attributeName : valuesMap.keySet()) {
            List<Pair<String, Integer>> pairs = valuesMap.get(attributeName);
            float varietyOfEventsPerAttribute = (float) pairs.size() / eventsInLog;
            varietyPerAttrMap.put(attributeName, varietyOfEventsPerAttribute);
            sumOfVarieties += varietyOfEventsPerAttribute;
        }

        float variatePercentMultilayer = 1 / sumOfVarieties;
        for (String attributeName : valuesMap.keySet()) {
            resultMap.put(attributeName, (varietyPerAttrMap.get(attributeName) * variatePercentMultilayer));
        }

        return resultMap;
    }

    /**
     * Build map where war calculated frequency of each value of each attribute in a list
     * <p>
     * Attribute1: Value11: 10
     * : Value12: 12
     * Attribute2: Value21: 3
     * : Value22: 5
     * ..............
     * AttributeN: ValueN1: 1..K
     * : ValueNM: 1..K
     * <p>
     * where K is value  restricted by  by count of events in log. That can be in case of
     * value of an attribute is constant for all events
     *
     * @param parsedLog
     * @param attributes
     * @return
     */
    private Map<String, List<Pair<String, Integer>>> fillAttributeValuesMap(List<XLog> parsedLog, XAttributeMap attributes) {
        Map<String, List<Pair<String, Integer>>> valuesMap = new HashMap<>();
        for (String key : attributes.keySet()) {
            if (key.contains("timestamp")) break;
            for (XLog log : parsedLog) {
                for (XTrace trace : log) {
                    for (XEvent xEvent : trace) {
                        String attrValue = String.valueOf(xEvent.getAttributes().get(key));
                        if (!valuesMap.containsKey(key)) {
                            // A value of an attribute was met first time
                            ArrayList<Pair<String, Integer>> value = new ArrayList<>();
                            valuesMap.put(key, value);
                            value.add(new Pair<String, Integer>(attrValue, 1));
                        } else {
                            // Increase frequency value for value of current attribute
                            List<Pair<String, Integer>> attributeValueFrequencyList = valuesMap.get(key);
                            int positionToInsert = -1;
                            for (int i = 0; i < attributeValueFrequencyList.size(); i++) {
                                if (attributeValueFrequencyList.get(i).getKey().equals(attrValue)) {
                                    positionToInsert = i;
                                    break;
                                }
                            }

                            /** If value for some attribute was met before than increase value of frequencies this value
                             *for an attribute
                             * OR just add for an attribute new value with frequency equals to 1
                             */
                            if (positionToInsert >= 0) {
                                Integer currentAttrFrequency = attributeValueFrequencyList.get(positionToInsert).getValue().intValue();
                                attributeValueFrequencyList.remove(positionToInsert);
                                attributeValueFrequencyList.add(positionToInsert, new Pair<>(attrValue, currentAttrFrequency + 1));
                            } else {
                                attributeValueFrequencyList.add(new Pair<>(attrValue, 1));
                            }
                        }
                    }
                }
            }
        }
        return valuesMap;
    }
}
