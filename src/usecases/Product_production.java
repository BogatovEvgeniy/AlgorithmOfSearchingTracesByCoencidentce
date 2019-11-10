package usecases;

import algorithms.search.trace.locator.invariant.Node;
import algorithms.search.trace.locator.invariant.TraceInvariantList;

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
        return "Ballnut_production";
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
        Node activity_type_node = new Node(KEY_ACTIVITY);
        ArrayList<String> invariantValues = new ArrayList<>();
//        invariantValues.add("тип [Производство полуфаб->Рабочая рецептура на смесь]");
//        invariantValues.add("тип [Производство полуфаб->Рабочая рецептура на стаканчики]");
//        invariantValues.add("тип [Производство полуфаб->Рабочая рецептура на глазурь сиропы кремы]");
//        invariantValues.add("тип [Производство полуфаб->Разборка брака ГП]");
//        invariantValues.add("тип [Фасовка ГП ->Фасовка ГП]");
//        invariantValues.add("тип [Приход из производст->Выход ГП из закалки]");
        activity_type_node.addInvariant(invariantValues);
        list.addInvariantNode(activity_type_node);

        return list;
    }

    public Map<String, Float> calculateCoefficientsMap() {
        Map<String, Float> correctionMap = new HashMap<>();
//        correctionMap.put(KEY_ACTIVITY_TYPE, 0.520899941f);
////        correctionMap.put(KEY_PROCESS, 0.729700019f);
//        correctionMap.put(KEY_ACTIVITY_DETAILS, 0.529700019f);
//        correctionMap.put(KEY_USER_ID, 0.319400038f);
//        correctionMap.put(KEY_ACTION, 0.00000000038f);
        return correctionMap;
    }
}
