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

}