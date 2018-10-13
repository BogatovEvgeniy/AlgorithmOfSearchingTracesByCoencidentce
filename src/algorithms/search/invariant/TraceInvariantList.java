package algorithms.search.invariant;

import algorithms.search.base.ITraceSearchingAlgorithm;
import com.google.common.annotations.VisibleForTesting;
import exceptions.InvariantAlreadyExistsException;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XAttributeImpl;

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
    ITraceSearchingAlgorithm.TraceLocator invariantsTraceLocator;

    public TraceInvariantList() {
        root = new HashMap<>();
        invariantsTraceLocator = new InvariantsTraceLocator(this);
    }

    public void addInvariantNode(String key, Node node) {
        root.put(key, node);
    }

    public void addValue(String key, XLog xLog, XEvent xEvent) {
        Node node = root.get(key);
        if (node == null) {
            root.put(key, new Node(key));
        }

        root.get(key).addValue(xLog, xEvent);
    }

    public void addInvariant(String key, List<String> values) throws InvariantAlreadyExistsException {
        Node node = root.get(key);
        if (node == null) {
            node.addInvariant(values);
        } else {
            throw new InvariantAlreadyExistsException(key);
        }
    }

    public void insertOrReplaceInvariant(String key, List values) throws InvariantAlreadyExistsException {
        root.remove(key);
        addInvariant(key,values);
    }

    public int nodesPerKey(XAttribute key) {
        return root.get(key).traceAttributeValues.size();
    }

    public Node getInvariantNodeForKey(String key) {
        return root.get(key);
    }

    public void clear() {
        root.clear();
    }

    public void size() {
        root.size();
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

        void addValue(XLog xLog, XEvent xEvent) {
            int traceIndex = getMostSuitableTraceIndex(key);

            if (traceAttributeValues != null) {
                traceAttributeValues = new LinkedList<>();
            }

            LinkedList<String> traceValues = new LinkedList<>();
            String newVal = ((XAttributeImpl)xEvent.getAttributes().get(key)).toString();
            traceValues.add(newVal);
            traceAttributeValues.add(traceValues);

            List<String> vs = traceAttributeValues.get(traceIndex);
            vs.add(newVal);
        }

       
        int getMostSuitableTraceIndex(String key) {
            throw new IllegalStateException("Not implemented yet");
        }

        public void addValuesList(String key, List<String> valuesList) {
            // TODO should be implemented search method and call to find right place
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
