package usecases;

import algorithms.search.trace.locator.invariant.IEventRule;
import algorithms.search.trace.locator.invariant.ITraceRule;
import algorithms.search.trace.locator.invariant.TraceInvariantList;
import algorithms.search.trace.locator.invariant.rule.event.Then;
import algorithms.search.trace.locator.invariant.rule.log.Final;
import algorithms.search.trace.locator.invariant.rule.log.Initial;
import algorithms.search.trace.locator.invariant.rule.trace.Any;
import algorithms.search.trace.locator.invariant.rule.event.Or;
import algorithms.search.trace.locator.invariant.rule.trace.Same;
import com.google.common.collect.Lists;

import java.util.*;

public class Product_production implements IUseCase, ICoefficientMapCalculator {
    public static final String KEY_CASE_ID = "Case ID";
    public static final String KEY_ACTIVITY = "concept:name";
    public static final String KEY_RESOURCE = "Resource";
    public static final String KEY_SPAN = "Span";
    public static final String KEY_WORK_ORDER_QTY = "Work Order  Qty";
    public static final String KEY_PART_DESC = "Part Desc.";
    public static final String KEY_WORKER_ID = "Worker ID";
    public static final String KEY_REPORT_TYPE = "Report Type";

    @Override
    public String getLogName() {
        return "Ballnut_production_without_machine_numbers";
    }

    @Override
    public List<List<String>> getAttributeSets() {
        List<List<String>> attributeSets = new LinkedList<>();
        attributeSets.add(Arrays.asList(KEY_CASE_ID));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY));
        attributeSets.add(Arrays.asList(KEY_RESOURCE));
        attributeSets.add(Arrays.asList(KEY_SPAN));
        attributeSets.add(Arrays.asList(KEY_WORK_ORDER_QTY));
        attributeSets.add(Arrays.asList(KEY_PART_DESC));
        attributeSets.add(Arrays.asList(KEY_WORKER_ID));
        attributeSets.add(Arrays.asList(KEY_CASE_ID, KEY_ACTIVITY));
        attributeSets.add(Arrays.asList(KEY_CASE_ID, KEY_RESOURCE));
        attributeSets.add(Arrays.asList(KEY_CASE_ID, KEY_SPAN));
        attributeSets.add(Arrays.asList(KEY_CASE_ID, KEY_WORK_ORDER_QTY));
        attributeSets.add(Arrays.asList(KEY_CASE_ID, KEY_PART_DESC));
        attributeSets.add(Arrays.asList(KEY_CASE_ID, KEY_WORKER_ID));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY, KEY_RESOURCE));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY, KEY_SPAN));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY, KEY_WORK_ORDER_QTY));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY, KEY_PART_DESC));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY, KEY_WORKER_ID));
        attributeSets.add(Arrays.asList(KEY_RESOURCE, KEY_SPAN));
        attributeSets.add(Arrays.asList(KEY_RESOURCE, KEY_WORK_ORDER_QTY));
        attributeSets.add(Arrays.asList(KEY_RESOURCE, KEY_PART_DESC));
        attributeSets.add(Arrays.asList(KEY_RESOURCE, KEY_WORKER_ID));
        attributeSets.add(Arrays.asList(KEY_SPAN, KEY_WORK_ORDER_QTY));
        attributeSets.add(Arrays.asList(KEY_SPAN, KEY_PART_DESC));
        attributeSets.add(Arrays.asList(KEY_SPAN, KEY_WORKER_ID));
        attributeSets.add(Arrays.asList(KEY_PART_DESC, KEY_WORKER_ID));
        attributeSets.add(Arrays.asList(KEY_CASE_ID, KEY_ACTIVITY, KEY_WORK_ORDER_QTY, KEY_WORKER_ID));
        return attributeSets;
    }

    @Override
    public TraceInvariantList getInvariants() {
        TraceInvariantList list = new TraceInvariantList();

//        List<IEventRule> eventRules = new LinkedList<>();
//        eventRules.add(new Or(KEY_ACTIVITY, "Turning & Milling", Lists.newArrayList("Turning & Milling", "Turning & Milling Q.C.")));
//        eventRules.add(new Or(KEY_ACTIVITY, "Turning & Milling Q.C.", Lists.newArrayList("Turning & Milling Q.C.", "Laser Marking")));
//        eventRules.add(new Or(KEY_ACTIVITY, "Laser Marking", Lists.newArrayList("Laser Marking", "Lapping")));
//        eventRules.add(new Or(KEY_ACTIVITY, "Lapping", Lists.newArrayList("Lapping", "Round Grinding")));
//        eventRules.add(new Or(KEY_ACTIVITY, "Round Grinding", Lists.newArrayList("Round Grinding", "Round Grinding - Q.C.", "Final Inspection Q.C.")));
//        eventRules.add(new Or(KEY_ACTIVITY, "Round Grinding - Q.C.", Lists.newArrayList("Round Grinding - Q.C.", "Final Inspection Q.C.")));
//        eventRules.add(new Or(KEY_ACTIVITY, "Final Inspection Q.C.", Lists.newArrayList("Packing", "Final Inspection Q.C.")));
//        list.addInvariantBatchEventRule(eventRules);

        List<ITraceRule> traceRules = new LinkedList<>();
        traceRules.add(new Same(KEY_CASE_ID));
        traceRules.add(new Same(KEY_WORK_ORDER_QTY));
        list.addInvariantBatchTraceRule(traceRules);

        list.addInitialEvents(new Initial(KEY_ACTIVITY, "Turning & Milling"));
        list.addFinalEvents(new Final(KEY_ACTIVITY, "Packing"));
        return list;
    }

    private void allmostAllEventRulesCovered(TraceInvariantList list) {
        List<IEventRule> rules = new LinkedList<>();
        rules.add(new Or(KEY_ACTIVITY, "Turn & Mill. & Screw Assem", Lists.newArrayList("Turning & Milling", "Turn & Mill. & Screw Assem", "Turning & Milling Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Turning & Milling", Lists.newArrayList("Turning & Milling", "Turning Q.C.", "Turning & Milling Q.C.", "Flat Grinding", "Grinding Rework", "Lapping", "Nitration Q.C.",  "Laser Marking", "Packing", "Final Inspection Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Turning Q.C.", Lists.newArrayList("Turning & Milling", "Laser Marking")));
        rules.add(new Or(KEY_ACTIVITY, "Turning & Milling Q.C.", Lists.newArrayList("Turning & Milling", "Turning & Milling Q.C.", "Turn & Mill. & Screw Assem", "Flat Grinding", "Round Grinding", "Lapping", "Turning Rework", "Laser Marking", "Final Inspection Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Turning Rework", Lists.newArrayList("Turning & Milling", "Laser Marking")));
        rules.add(new Or(KEY_ACTIVITY, "Lapping", Lists.newArrayList("Turning & Milling", "Turning & Milling Q.C.", "Round Grinding", "Flat Grinding", "Grinding Rework", "Lapping", "Laser Marking", "Packing",  "Final Inspection Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Laser Marking", Lists.newArrayList("Turning & Milling", "Turning & Milling Q.C.", "Flat Grinding", "Laser Marking", "Lapping", "Round Grinding", "Final Inspection Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Flat Grinding", Lists.newArrayList("Turn & Mill. & Screw Assem", "Flat Grinding", "Laser Marking", "Lapping", "Turning & Milling Q.C.", "Flat Grinding", "Lapping","Final Inspection Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Round Grinding", Lists.newArrayList("Flat Grinding", "Round Grinding - Q.C.", "Grinding Rework", "Lapping",  "Packing", "Final Inspection Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Round Grinding - Q.C.", Lists.newArrayList("Round Grinding", "Packing", "Laser Marking", "Final Inspection Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Grinding Rework", Lists.newArrayList("Grinding Rework", "Lapping", "Packing", "Final Inspection Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Nitration Q.C.", Lists.newArrayList("Lapping","Laser Marking")));
        rules.add(new Or(KEY_ACTIVITY, "Packing", Lists.newArrayList("Turning & Milling", "Turning & Milling Q.C.", "Packing", "Final Inspection Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Final Inspection Q.C.", Lists.newArrayList("Turning & Milling", "Flat Grinding", "Packing", "Final Inspection Q.C.")));
        list.addInvariantBatchEventRule(rules);
    }

    public Map<String, Float> calculateCoefficientsMap() {
        Map<String, Float> correctionMap = new HashMap<>();
        correctionMap.put(KEY_CASE_ID, 2f);
        correctionMap.put(KEY_ACTIVITY, 2f);
        correctionMap.put(KEY_WORKER_ID, 2f);
        correctionMap.put(KEY_REPORT_TYPE, 0.0000000001f);
        correctionMap.put(KEY_SPAN, 0.0000000001f);
        correctionMap.put(KEY_RESOURCE, 0.0000000001f);
        correctionMap.put(KEY_PART_DESC, 0.0000000001f);
        return correctionMap;
    }
}
