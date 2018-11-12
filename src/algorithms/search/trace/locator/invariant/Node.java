package algorithms.search.trace.locator.invariant;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Node {
    private String key;
    private List<String> attributeInvariant;
    private List<List<String>> traceAttributeValues;
    private int lastInsertionIndex;

    public Node(String key) {
        this.key = key;
        attributeInvariant = new LinkedList<>();
        traceAttributeValues = new LinkedList<>();
    }

    public void addInvariant(List<String> invariantValues) {
        attributeInvariant.clear();
        attributeInvariant.addAll(invariantValues);
    }

    void addTrace(List<String> traceValues) {
        traceAttributeValues.add(traceValues);
    }

    void addTrace(String traceValue) {
        LinkedList<String> traceEvents = new LinkedList<>();
        traceEvents.add(traceValue);
        traceAttributeValues.add(traceEvents);
    }

    void removeTrace(int traceIndex) {
        traceAttributeValues.remove(traceIndex);
    }

    void removeTrace(List<String> trace) {
        traceAttributeValues.remove(trace);
    }

    void addValue(int traceIndex, String value) {

        if (traceAttributeValues == null) {
            traceAttributeValues = new LinkedList<>();
        }

        if (traceAttributeValues.isEmpty()) {
            traceAttributeValues.add(new LinkedList<>());
        }

        if (traceIndex > traceAttributeValues.size() - 1){
            throw  new IllegalStateException("There is no trace in node with such index");
        }

        List<String> traceValues = traceAttributeValues.get(traceIndex);
        traceValues.add(value);
        lastInsertionIndex = traceIndex;
    }

    public String getKey() {
        return key;
    }

    public List<String> getAttributeInvariant() {
        return attributeInvariant;
    }

    public List<List<String>> getAllAvailableValues() {
        return traceAttributeValues;
    }

    public int getLastInsertionIndex() {
        return lastInsertionIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node that = (Node) o;
        return Objects.equals(attributeInvariant, that.attributeInvariant) &&
                Objects.equals(traceAttributeValues, that.traceAttributeValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attributeInvariant, traceAttributeValues);
    }

    public void clear() {
        traceAttributeValues = new LinkedList<>();
        attributeInvariant = new LinkedList<>();
    }

    @Override
    public String toString() {
        return "Node{" +
                "key='" + key + '\'' +
                ", attributeInvariant=" + attributeInvariant +
                ", traceAttributeValues=" + traceAttributeValues +
                ", lastInsertionIndex=" + lastInsertionIndex +
                '}';
    }
}
