package algorithms.search.trace;

import org.deckfour.xes.model.XAttributeMap;

import java.util.Map;

public class AttributeSetWeightPerRanges {

    Map<Integer, Float> rangesWeights;
    XAttributeMap keysValues;
    float weight;

    public AttributeSetWeightPerRanges() {
    }

    public AttributeSetWeightPerRanges(Map<Integer, Float> rangesWeights, XAttributeMap keysValues, float weight) {
        this.rangesWeights = rangesWeights;
        this.keysValues = keysValues;
        this.weight = weight;
    }

    public  Map<Integer, Float>getRangeIndexes() {
        return rangesWeights;
    }

    public void setRangeIndexes( Map<Integer, Float> rangesWeights) {
        this.rangesWeights = rangesWeights;
    }

    public XAttributeMap getValues() {
        return keysValues;
    }

    public void setValues(XAttributeMap values) {
        this.keysValues = values;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "AttributeSetWeightPerRanges{" +
                "rangeIndexes=" + rangesWeights.keySet().toString() +
                ", rangeWeights=" + rangesWeights.values().toString() +
                ", keys=" + keysValues.keySet().toString() +
                ", values=" + keysValues.values().toString() +
                ", weight=" + weight +
                '}';
    }
}
