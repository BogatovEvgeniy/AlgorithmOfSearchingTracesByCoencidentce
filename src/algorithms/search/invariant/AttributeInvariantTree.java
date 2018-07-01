package algorithms.search.invariant;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.deckfour.xes.model.XAttribute;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * The tree structure where:
 * root is a collection of nodes which represent a set of attributes of a log.
 * So there is cant be a case where two nodes are representing the same attribute
 * Each attribute represents a set of invariants
 * Invariant is representing a list of values in order of their appearance in a real process
 * 
 * @param <V> - a key type
 */
public class AttributeInvariantTree<V> {
    TreeNode<XAttribute, V> root = new TreeNode<>();

    public void addNodes(List<TreeNode<XAttribute, V>> nodeList) {
        for (TreeNode<XAttribute, V> XAttributeVTreeNode : nodeList) {
            root.addNode(XAttributeVTreeNode);
        }
    }

    public void addValues(List<V> values) {
        for (V value : values) {
            root.addValue(value);
        }
    }

    public void addInvariant(XAttribute attribute, List<V> values) {
        TreeNode<XAttribute, V> node = root.searchNodeByKey(attribute);
        for (V value : values) {
            node.addValue(value);
        }
    }

    public void addNodesToNode(List<V> values) {
        TreeNode<XAttribute, V> node = root.searchEqualsNode();
        for (V value : values) {
            root.addValue(value);
        }
    }

    public int nodesPerKey(XAttribute key) {
        return root.searchNodeByKey(key).childs.size();
    }

    public Set<TreeNode<XAttribute, V>> getInvariantsForKey(XAttribute attribute) {
        return root.searchNodeByKey(attribute).childs;
    }

    public static class TreeNode<K, V> {
        TreeNode<K, V> prev;
        K attribute;
        List<V> values;
        Set<TreeNode<K, V>> childs;


        private TreeNode() {
        }

        public TreeNode(K attribute) {
            this.attribute = attribute;
        }

        public TreeNode initWithValues(K attribute, List<V> nodeValues) {
            TreeNode<K, V> instance = new TreeNode<K, V>(attribute);
            for (V val : nodeValues) {
                instance.addValue(val);
            }
            return instance;
        }

        public TreeNode initWithNodes(K attribute, List<TreeNode<K, V>> childNodes) {
            TreeNode<K, V> instance = new TreeNode<K, V>(attribute);
            for (TreeNode<K, V> childNode : childNodes) {
                instance.addNode(childNode);
            }
            return instance;
        }

        void addNode(TreeNode<K, V> node) {
            childs.remove(node);
            childs.add(node);
        }

        void addValue(V nodeVal) {
            values.remove(nodeVal);
            values.add(nodeVal);
        }

        public List<V> getValues() {
            return values;
        }

        public Set<TreeNode<K, V>> getChilds() {
            return childs;
        }

        @Nullable
        <P> TreeNode<K, V> searchNode(P compareValue, Predicate<P> predicate) {
            if (isLeafContainer(this)) {
                return predicate.test(compareValue) ? this : null;
            } else {
                for (TreeNode<K, V> child : childs) {
                    TreeNode<K, V> kvTreeNode = searchNode(compareValue, predicate);
                    if (kvTreeNode != null) {
                        return kvTreeNode;
                    }
                }
            }
            return null;
        }

        /**
         * Search in N-node is it node or leaf
         * if count of child nodes equals NULL then it's
         * the last level of nodes so it could be only leaf container
         */
        private boolean isLeafContainer(TreeNode<K, V> searchArea) {
            for (TreeNode<K, V> childNode : searchArea.childs) {
                if (childNode.childs == null) {
                    return true;
                }
            }
            return false;
        }

        TreeNode<K, V> searchEqualsNode() {
            Predicate<TreeNode<K, V>> predicate = new Predicate<TreeNode<K, V>>() {
                @Override
                public boolean test(TreeNode<K, V> kvTreeNode) {
                    return this.equals(kvTreeNode);
                }
            };
            return searchNode(this, predicate);
        }


        TreeNode<K, V> searchNodeByKey(K key) {
            Predicate<K> predicate = new Predicate<K>() {
                @Override
                public boolean test(K searchVal) {
                    return key.equals(searchVal);
                }
            };
            return searchNode(key, predicate);
        }

        @NotNull
        public <P> List<TreeNode<K, V>> searchNodesWithValue(V searchVal) {
            List<TreeNode<K, V>> resultList = new LinkedList<>();
            Predicate<V> predicate = aVal -> {
                List<V> values = TreeNode.this.values;
                for (V value : values) {
                    if (aVal.equals(value)) {
                        return true;
                    }
                }
                return false;
            };
            fillListWithAppropriateNodes(resultList, searchVal, predicate);
            return resultList;
        }

        private <P> void fillListWithAppropriateNodes(List<TreeNode<K, V>> targetList, P compareValue, Predicate<P> predicate) {
            List<TreeNode<K, V>> resultsList = new LinkedList<>();
            if (isLeafContainer(this)) {
                if (predicate.test(compareValue)) {
                    resultsList.add(this);
                }
            } else {
                for (TreeNode<K, V> child : childs) {
                    searchNode(compareValue, predicate);
                }
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TreeNode<K, V> treeNode = (TreeNode<K, V>) o;
            return Objects.equals(prev, treeNode.prev) &&
                    Objects.equals(attribute, treeNode.attribute) &&
                    Objects.equals(values, treeNode.values) &&
                    Objects.equals(childs, treeNode.childs);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prev, attribute, values, childs);
        }
    }
}
