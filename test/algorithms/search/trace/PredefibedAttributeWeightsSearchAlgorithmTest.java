package algorithms.search.trace;

import base.LogTestBaseClass;
import com.google.common.collect.Lists;
import javafx.util.Pair;
import org.deckfour.xes.model.XLog;
import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;

public class PredefibedAttributeWeightsSearchAlgorithmTest extends LogTestBaseClass {

    private XLog log;
    private PredefibedAttributeWeightsSearchAlgorithm algorithm;

    private static String ATTR_KEY_1 = "attr1";
    private static String ATTR_KEY_2 = "attr2";
    private static String ATTR_KEY_3 = "attr3";
    private static String ATTR_KEY_4 = "attr4";

    @Before
    public void prepareAlgorithmInstance() {
        log = getLogInstance();

        Set<Pair<Integer, Integer>> rangeSet = new HashSet<>();
        rangeSet.add(new Pair<>(1, 3));
        rangeSet.add(new Pair<>(4, 6));
        rangeSet.add(new Pair<>(7, 9));
        rangeSet.add(new Pair<>(10, 12));

        List<List<String>> attributeSets = new LinkedList<>();
        attributeSets.add(Lists.newArrayList(ATTR_KEY_1));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_2));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_3));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_4));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_1, ATTR_KEY_2));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_1, ATTR_KEY_3));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_1, ATTR_KEY_4));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_2, ATTR_KEY_3));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_2, ATTR_KEY_4));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_3, ATTR_KEY_4));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_1, ATTR_KEY_2, ATTR_KEY_3));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_1, ATTR_KEY_2, ATTR_KEY_4));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_2, ATTR_KEY_3, ATTR_KEY_4));
        attributeSets.add(Lists.newArrayList(ATTR_KEY_1, ATTR_KEY_3, ATTR_KEY_4));

        algorithm = new PredefibedAttributeWeightsSearchAlgorithm(3, 3, 0.3f, rangeSet, attributeSets);
    }

    @Test
    public void coincidenceInRange() {

    }

    @Test
    public void calculateCoincidenceInStep() {

    }

    @Test
    public void calculateCoincidenceEventPair() {

    }

    @Test
    public void checkLog() {
        assertNotNull(log);
        assertNotEquals(log.size(), 0);
        assertNotNull(log.get(0));
        assertNotEquals(log.get(0).size(),0);
    }

    @After
    public void nullifyAlgorithmInstance() {
        algorithm = null;
    }

}