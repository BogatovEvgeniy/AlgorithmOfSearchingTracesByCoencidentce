package algorithms.search.invariant;

import algorithms.search.base.ITraceSearchingAlgorithm;
import exceptions.InvariantAlreadyExistsException;
import org.deckfour.xes.model.XAttribute;

import java.util.*;

/**
 * The tree structure where:
 * root is a collection of nodes which represent a set of attributes of a log.
 * So there is cant be a case where two nodes are representing the same attribute
 * Each attribute represents a set of invariants
 * Invariant is representing a list of values in order of their appearance in a real process
 *
 */
public class AttributeInvariantTree {
    private Map<XAttribute, Node> root;
    ITraceSearchingAlgorithm.TraceLocator invariantsTraceLocator;

    public AttributeInvariantTree() {
        root = new HashMap<>();
        invariantsTraceLocator = new InvariantsTraceLocator(this);
    }

    public void addInvariantNode(XAttribute attribute, Node invariant) {
        root.put(attribute, invariant);
    }

    public void addValue(XAttribute attribute, int traceIndex, Object value) {
        root.get(attribute).addValue(traceIndex, value);
    }

    public void addValue(String attributeKey, Object value) {
        root.get(attributeKey).addValue(value);
    }

    public <V>  void addInvariant(XAttribute attribute, List<V> values) throws InvariantAlreadyExistsException {
        Node<V> node = root.get(attribute);
        if (node == null) {
            node.addInvariant(values);
        } else {
            throw new InvariantAlreadyExistsException(attribute);
        }
    }

    public void insertOrReplaceInvariant(XAttribute attribute, List values) {
        Node node = root.get(attribute);
        root.remove(attribute);
        node.addInvariant(values);
    }

    public int nodesPerKey(XAttribute key) {
        return root.get(key).traceAttributeValues.size();
    }

    public Node getInvariantNodeForKey(XAttribute attribute) {
        return root.get(attribute);
    }

    public void clear() {
        root.clear();
    }

    public void size() {
        root.size();
    }

    public static class Node<V> {
        List<V> attributeInvariant;
        List<List<V>> traceAttributeValues;

        public Node() {
            attributeInvariant = new ArrayList<>();
            traceAttributeValues = new ArrayList<>();
        }

        public void addValuesList(List<V> valuesList) {
            traceAttributeValues.add(valuesList);
        }

        void addInvariant(List<V> invariantValues) {
            if (traceAttributeValues.contains(invariantValues)) {
                traceAttributeValues.remove(invariantValues);
            }
            traceAttributeValues.add(invariantValues);
        }

        boolean addValue(int traceIndex, V newVal) {
            List<V> vs = traceAttributeValues.get(traceIndex);

            if (vs != null) {
                traceAttributeValues.add(new LinkedList<>());
                vs  = traceAttributeValues.get(traceIndex);
            }

            return vs.add(newVal);
        }

        boolean addValue(V newVal) {
            traceAttributeValues.add(new LinkedList<>());
            List<V> vs = traceAttributeValues.get(traceAttributeValues.size() - 1);
            return vs.add(newVal);
        }

        public List<V> getInvariantValues() {
            return attributeInvariant;
        }

        public List<List<V>> getAllAvailiableSet() {
            return traceAttributeValues;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> that = (Node<?>) o;
            return Objects.equals(attributeInvariant, that.attributeInvariant) &&
                    Objects.equals(traceAttributeValues, that.traceAttributeValues);
        }

        @Override
        public int hashCode() {
            return Objects.hash(attributeInvariant, traceAttributeValues);
        }

        public void clear() {
            traceAttributeValues = null;
            attributeInvariant = null;
        }

        public void size() {
            traceAttributeValues.size();
        }
    }
}
