package usecases;

import algorithms.search.trace.locator.invariant.ITraceRule;
import algorithms.search.trace.locator.invariant.TraceInvariantList;
import algorithms.search.trace.locator.invariant.rule.log.Final;
import algorithms.search.trace.locator.invariant.rule.log.Initial;
import algorithms.search.trace.locator.invariant.rule.trace.Same;

import java.util.*;

public class BPIChallenge2013Incidents implements IUseCase, ICoefficientMapCalculator {

    public static final String KEY_PRODUCT = "product";
    public static final String KEY_ORG_RESOURCE = "org:resource";
    public static final String KEY_ORG_ROLE = "org:role";
    public static final String KEY_ORG_GROUP = "org:group";

    @Override
    public String getLogName() {
        return "BPI_Challenge_2013_incidents_111";
    }

    @Override
    public List<List<String>> getAttributeSets() {
        List<List<String>> attributeSets = new LinkedList<>();
        attributeSets.add(Arrays.asList("concept:name"));
        attributeSets.add(Arrays.asList("product"));
        attributeSets.add(Arrays.asList("org:group"));
        attributeSets.add(Arrays.asList("org:resource"));
        attributeSets.add(Arrays.asList("organization involved"));
        attributeSets.add(Arrays.asList("org:role"));
        attributeSets.add(Arrays.asList("org:resource", "product"));
        attributeSets.add(Arrays.asList("org:group", "org:resource"));
        attributeSets.add(Arrays.asList("org:role", "org:resource"));
        attributeSets.add(Arrays.asList("concept:name", "org:group", "org:role", "product"));
        attributeSets.add(Arrays.asList("concept:name", "org:group", "org:resource", "product"));
        attributeSets.add(Arrays.asList("concept:name", "org:group", "org:resource", "org:role"));
        attributeSets.add(Arrays.asList("concept:name", "org:group", "org:resource", "organization involved"));
        attributeSets.add(Arrays.asList("concept:name", "org:group", "org:resource", "org:role", "product"));
        attributeSets.add(Arrays.asList("concept:name", "org:group", "org:resource", "organization involved", "org:role"));
        attributeSets.add(Arrays.asList("concept:name", "org:group", "org:resource", "organization involved", "org:role", "product"));
        return attributeSets;
    }

    @Override
    public TraceInvariantList getInvariants() {
        TraceInvariantList list = new TraceInvariantList();

        List<ITraceRule> traceRules = new LinkedList<>();
        traceRules.add(new Same(KEY_PRODUCT));
        list.addInvariantBatchTraceRule(traceRules);

        list.addInitialEvents(new Initial("concept:name", "Accepted"));
        list.addInitialEvents(new Initial("concept:name", "Queued"));
        list.addInitialEvents(new Initial("lifecycle:transition", "In Progress"));
        list.addFinalEvents(new Final("concept:name", "Completed"));
        list.addFinalEvents(new Final("lifecycle:transition", "Closed"));
        list.addFinalEvents(new Final("lifecycle:transition", "In Call"));
        return list;
    }

    public Map<String, Float> calculateCoefficientsMap() {
        Map<String, Float> correctionMap = new HashMap<>();
        correctionMap.put(KEY_PRODUCT, 0.520899941f);
        correctionMap.put(KEY_ORG_RESOURCE, 0.159700019f);
        correctionMap.put(KEY_ORG_GROUP, 0.319400038f);
//        Map<String, Float> stringFloatMap = new CoefficientMapBuilder(xLog, correctionMap, false).build();
        return correctionMap;
    }
}
