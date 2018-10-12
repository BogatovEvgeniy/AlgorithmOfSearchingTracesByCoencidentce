package algorithms.search.invariant;

import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AttributeInvariantTreeTest {

    private AttributeInvariantTree invariantTree;

    private static String ATTR_KEY_VAL_1 = "KEY_1";
    private static String ATTR_KEY_VAL_2 = "KEY_2";

    private static String ATTR_TEST_VAL_STRING_VAL_1 = "string val_1";
    private static String ATTR_TEST_VAL_STRING_VAL_2 = "string val_2";
    private static int ATTR_TEST_VAL_INT = 1;
    private static String ATTR_TEST_VAL_TIME = "1970-01-01T00:00:00.000+01:00";
    private static float ATTR_TEST_VAL_FLOAT = 1.1F;

    @Before
    public void initEnvironment(){
        invariantTree = new AttributeInvariantTree();
    }

    @Test
    public void addInvariantNode() {
        List<AttributeInvariantTree.Node<String>> listWithTwoAttributeNodes = getListWithTwoStringAttributeNodes();
        String value = listWithTwoAttributeNodes.get(0).getInvariantValues().get(0);
//        invariantTree.addValue(new XAttributeLiteralImpl(ATTR_KEY_VAL_1, value), value);
//        invariantTree.addValue(new XAttributeLiteralImpl(ATTR_KEY_VAL_2, listWithTwoAttributeNodes.get(1)));
//
//        assertTrue(listWithTwoAttributeNodes.size() > 0);
//        assertTrue(listWithTwoAttributeNodes.get(0).attribute.getKey().equals(ATTR_TEST_VAL_1.getKey()));
//        assertTrue(listWithTwoAttributeNodes.get(1).attribute.getKey().equals(ATTR_TEST_VAL_2.getKey()));
    }

    @Test
    public void addValue() {
    }

    @Test
    public void addInvariant() {
    }

    @Test
    public void insertOrReplaceInvariant() {
    }

    @Test
    public void nodesPerKey() {
    }

    @Test
    public void getInvariantNodeForKey() {
    }

    @Test
    public void clear() {
    }

    @Test
    public void size() {
    }

    @After
    public void clearEnvironment(){
        invariantTree = null;
    }

    List<AttributeInvariantTree.Node<String>> getListWithOneAttributeNodes() {
        List<AttributeInvariantTree.Node<String>> listOfNodes = new ArrayList<>();
        AttributeInvariantTree.Node<String> node1 = new AttributeInvariantTree.Node<String>();
        node1.addValue(ATTR_TEST_VAL_STRING_VAL_1);
        listOfNodes.add(node1);

        return listOfNodes;
    }

    List<AttributeInvariantTree.Node<String>> getListWithTwoStringAttributeNodes() {
        List<AttributeInvariantTree.Node<String>> listOfNodes = new ArrayList<>();
        AttributeInvariantTree.Node<String> node1 = new AttributeInvariantTree.Node<>();
        node1.addValue(ATTR_TEST_VAL_STRING_VAL_1);
        listOfNodes.add(node1);
        AttributeInvariantTree.Node<String> node2 = new AttributeInvariantTree.Node<>();
        node2.addValue(ATTR_TEST_VAL_STRING_VAL_2);
        listOfNodes.add(node2);

        return listOfNodes;
    }
}