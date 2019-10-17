package usecases;

import algorithms.search.trace.locator.invariant.Node;
import algorithms.search.trace.locator.invariant.TraceInvariantList;

import java.util.*;

public class KhladopromLogUseCase implements IUseCase, ICoefficientMapCalculator {

    public static final String KEY_ACTION = "Action";
//    public static final String KEY_PROCESS = "Process";
    public static final String KEY_ACTIVITY_DETAILS = "Activity_details";
    public static final String KEY_ACTIVITY_TYPE = "concept:name";
    public static final String KEY_DB_FILE_NUM = "DB_File_Num";
    public static final String KEY_KODDOC = "koddoc";
    public static final String KEY_USER_ID = "User_id";

    @Override
    public String getLogName() {
        return "Khladoprom_Log_without_counting_dept_without_cycles";
    }

    @Override
    public List<List<String>> getAttributeSets() {
        List<List<String>> attributeSets = new LinkedList<>();
        attributeSets.add(Arrays.asList(KEY_ACTION));
//        attributeSets.add(Arrays.asList(KEY_PROCESS));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_TYPE));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_KODDOC));
        attributeSets.add(Arrays.asList(KEY_USER_ID));
//        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_ACTIVITY_DETAILS));
//        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_ACTIVITY_TYPE));
//        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_DB_FILE_NUM));
//        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_DB_FILE_NUM));
//        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_KODDOC));
//        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_USER_ID));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_KODDOC));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_USER_ID));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM, KEY_KODDOC));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM, KEY_USER_ID));
//        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_ACTIVITY_DETAILS, KEY_ACTIVITY_TYPE));
//        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_ACTIVITY_DETAILS, KEY_ACTIVITY_TYPE, KEY_USER_ID));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_ACTIVITY_TYPE));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_ACTIVITY_TYPE, KEY_USER_ID));
        return attributeSets;
    }

    @Override
    public TraceInvariantList getInvariants() {
        TraceInvariantList list = new TraceInvariantList();
        Node activity_type_node = new Node(KEY_ACTIVITY_TYPE);
        ArrayList<String> invariantValues = new ArrayList<>();
        invariantValues.add("тип [Производство полуфаб->Рабочая рецептура на смесь]");
        invariantValues.add("тип [Производство полуфаб->Рабочая рецептура на стаканчики]");
        invariantValues.add("тип [Производство полуфаб->Рабочая рецептура на глазурь сиропы кремы]");
        invariantValues.add("тип [Производство полуфаб->Разборка брака ГП]");
        invariantValues.add("тип [Фасовка ГП ->Фасовка ГП]");
        invariantValues.add("тип [Приход из производст->Выход ГП из закалки]");
        activity_type_node.addInvariant(invariantValues);
        list.addInvariantNode(activity_type_node);

        return list;
    }

    public Map<String, Float> calculateCoefficientsMap() {
        Map<String, Float> correctionMap = new HashMap<>();
        correctionMap.put(KEY_ACTIVITY_TYPE, 0.520899941f);
//        correctionMap.put(KEY_PROCESS, 0.729700019f);
        correctionMap.put(KEY_ACTIVITY_DETAILS, 0.529700019f);
        correctionMap.put(KEY_USER_ID, 0.319400038f);
        correctionMap.put(KEY_ACTION, 0.00000000038f);
        return correctionMap;
    }
}
