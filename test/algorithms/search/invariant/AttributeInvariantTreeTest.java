package algorithms.search.invariant;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AttributeInvariantTreeTest {

    private AttributeInvariantTree<String> invariantTree;
    private static XAttribute ATTR_TEST_VAL_1;
    private static XAttribute ATTR_TEST_VAL_2;
    private static XAttribute ATTR_TEST_VAL_3;
    private static XAttribute ATTR_TEST_VAL_4;

    @BeforeClass
    public static void initConstants(){
        ATTR_TEST_VAL_1 = new XAttributeLiteralImpl("key1", "val1");
        ATTR_TEST_VAL_2 = new XAttributeLiteralImpl("key2", "val2");
        ATTR_TEST_VAL_3 = new XAttributeLiteralImpl("key3", "val3");
        ATTR_TEST_VAL_4 = new XAttributeLiteralImpl("key4", "val4");
    }

    @Before
    public void initEnvironment(){
        invariantTree = new AttributeInvariantTree<String>();
    }

    @Test
    public void addNodes() {
        List<AttributeInvariantTree.Node<XAttribute, String>> listWithTwoAttributeNodes = getListWithTwoAttributeNodes();
        invariantTree.addNodes(listWithTwoAttributeNodes);

        assertTrue(listWithTwoAttributeNodes.size() > 0);
        assertTrue(listWithTwoAttributeNodes.get(0).attribute.getKey().equals(ATTR_TEST_VAL_1.getKey()));
        assertTrue(listWithTwoAttributeNodes.get(1).attribute.getKey().equals(ATTR_TEST_VAL_2.getKey()));
    }


    @Test
    public void addValues() {
        List<AttributeInvariantTree.Node<XAttribute, String>> listWithTwoAttributeNodes = getListWithTwoAttributeNodes();
        invariantTree.addNodes(listWithTwoAttributeNodes);

        invariantTree.size();
        invariantTree.getNodes();


    }

    @Test
    public void addInvariant() {
    }

    @Test
    public void addNodesToNode() {
    }

    @Test
    public void nodesPerKey() {
    }

    @Test
    public void getInvariantsForKey() {

    }

    @After
    public void clearEnvironment(){
        invariantTree = null;
    }

    List<AttributeInvariantTree.Node<XAttribute, String>> getListWithOneAttributeNodes() {
        List<AttributeInvariantTree.Node<XAttribute, String>> listOfNodes = new ArrayList<>();
        AttributeInvariantTree.Node<XAttribute, String> node1 = new AttributeInvariantTree.Node<>(ATTR_TEST_VAL_1);
        listOfNodes.add(node1);

        return listOfNodes;
    }

    List<AttributeInvariantTree.Node<XAttribute, String>> getListWithTwoAttributeNodes() {
        List<AttributeInvariantTree.Node<XAttribute, String>> listOfNodes = new ArrayList<>();
        AttributeInvariantTree.Node<XAttribute, String> node1 = new AttributeInvariantTree.Node<>(ATTR_TEST_VAL_1);
        listOfNodes.add(node1);
        AttributeInvariantTree.Node<XAttribute, String> node2 = new AttributeInvariantTree.Node<>(ATTR_TEST_VAL_2);
        listOfNodes.add(node2);

        return listOfNodes;
    }
}