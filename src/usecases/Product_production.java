package usecases;

import algorithms.search.trace.locator.invariant.IEventRule;
import algorithms.search.trace.locator.invariant.TraceInvariantList;
import algorithms.search.trace.locator.invariant.rule.event.Or;
import algorithms.search.trace.locator.invariant.rule.event.Then;
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
        List<IEventRule> rules = new LinkedList<>();
        rules.add(new Or(KEY_ACTIVITY, "Turn & Mill. & Screw Assem", Lists.newArrayList("Turning & Milling", "Turn & Mill. & Screw Assem", "Turning & Milling Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Turning & Milling", Lists.newArrayList("Turning & Milling", "Turning Q.C.", "Turning & Milling Q.C.", "Grinding Rework", "Laser Marking", "Packing", "Final Inspection Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Turning & Milling Q.C.", Lists.newArrayList("Turning & Milling", "Turning & Milling Q.C.", "Turn & Mill. & Screw Assem", "Lapping", "Turning Rework", "Laser Marking")));
        rules.add(new Or(KEY_ACTIVITY, "Laser Marking", Lists.newArrayList("Flat Grinding", "Laser Marking", "Lapping")));
        rules.add(new Or(KEY_ACTIVITY, "Lapping", Lists.newArrayList("Turning & Milling", "Turning & Milling Q.C.", "Round Grinding", "Lapping", "Laser Marking", "Grinding Rework", "Final Inspection Q.C.")));
        rules.add(new Or(KEY_ACTIVITY, "Turning Rework", Lists.newArrayList("Turning & Milling", "Laser Marking")));
        rules.add(new Then(KEY_ACTIVITY, "Turning Q.C.", "Laser Marking"));
        rules.add(new Or(KEY_ACTIVITY, "Flat Grinding", Lists.newArrayList("Turn & Mill. & Screw Assem", "Laser Marking", "Turning & Milling Q.C.", "Flat Grinding", "Lapping")));
        rules.add(new Then(KEY_ACTIVITY, "Grinding Rework", "Final Inspection Q.C."));
        rules.add(new Then(KEY_ACTIVITY, "Final Inspection Q.C.", "Packing"));
        rules.add(new Or(KEY_ACTIVITY, "Packing", Lists.newArrayList("Turning & Milling", "Turning & Milling Q.C.", "Packing", "Final Inspection Q.C.")));
        rules.add(new Then(KEY_ACTIVITY, "Flat Grinding", "Final Inspection Q.C."));
        rules.add(new Or(KEY_ACTIVITY, "Round Grinding", Lists.newArrayList("Round Grinding - Q.C.", "Packing")));
        rules.add(new Or(KEY_ACTIVITY, "Round Grinding - Q.C.", Lists.newArrayList("Packing", "Final Inspection Q.C.")));
        rules.add(new Then(KEY_ACTIVITY, "Grinding Rework", "Lapping"));
        rules.add(new Then(KEY_ACTIVITY, "Final Inspection Q.C.", "Turning & Milling"));
        list.addInvariantBatchEventRule(rules);
        list.addInvariantTraceRule(new Same(KEY_CASE_ID));
        list.addInvariantTraceRule(new Same(KEY_WORK_ORDER_QTY));

        return list;
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
