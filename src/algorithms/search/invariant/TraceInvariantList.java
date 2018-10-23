package algorithms.search.invariant;

import java.util.*;

/**
 * The tree structure where:
 * root is a collection of nodes which represent a set of attributes of a log.
 * So there is cant be a case where two nodes are representing the same attribute
 * Each attribute represents a set of invariants
 * Invariant is representing a list of values in order of their appearance in a real process
 */
public class TraceInvariantList {
    private Map<String, Node> root;

    public TraceInvariantList() {
        root = new HashMap<>();
    }

    public void addInvariantNode(Node node) {
        root.put(node.getKey(), node);
    }

    public void insertOrReplaceInvariant(String key, List values) {
        root.remove(key);
        Node node = new Node(key);
        node.addInvariant(values);
        root.put(key, node);
    }

    public Node getInvariantNodeForKey(String key) {
        return root.get(key);
    }

    public void clear() {
        root.clear();
    }

    public int size() {
        return root.size();
    }

    public static class Node {
        private String key;
        private List<String> attributeInvariant;
        private List<List<String>> traceAttributeValues;

        public Node(String key) {
            this.key = key;
            attributeInvariant = new LinkedList<>();
            traceAttributeValues = new LinkedList<>();
        }

        void addInvariant(List<String> invariantValues) {
            attributeInvariant.clear();
            attributeInvariant.addAll(invariantValues);
        }

        void addTrace(List<String> traceValues) {
            List<List<String>> lists = traceAttributeValues;
            lists.add(traceValues);
        }

        void removeTrace(int traceIndex) {
            traceAttributeValues.remove(traceIndex);
        }

        void addValue(int traceIndex, String value) {

            if (traceAttributeValues != null) {
                traceAttributeValues = new LinkedList<>();
            }

            List<String> traceValues = traceAttributeValues.get(traceIndex);
            traceValues.add(value);
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
            traceAttributeValues = null;
            attributeInvariant = null;
        }

        public void size() {
            traceAttributeValues.size();
        }
    }
}