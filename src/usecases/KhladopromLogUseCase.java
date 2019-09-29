package usecases;

import algorithms.search.trace.locator.invariant.TraceInvariantList;

import java.util.*;

public class KhladopromLogUseCase implements IUseCase, ICoefficientMapCalculator {

    public static final String KEY_ACTION = "Action";
    public static final String KEY_PROCESS = "Process";
    public static final String KEY_ACTIVITY_DETAILS = "Activity_details";
    public static final String KEY_ACTIVITY_TYPE = "Activity_type";
    public static final String KEY_DB_FILE_NUM = "DB_File_Num";
    public static final String KEY_KODDOC = "koddoc";
    public static final String KEY_USER_ID = "User_id";

    @Override
    public String getLogName() {
        return "Khladoprom_Log";
    }

    @Override
    public List<List<String>> getAttributeSets() {
        List<List<String>> attributeSets = new LinkedList<>();
        attributeSets.add(Arrays.asList(KEY_ACTION));
        attributeSets.add(Arrays.asList(KEY_PROCESS));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_TYPE));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_KODDOC));
        attributeSets.add(Arrays.asList(KEY_USER_ID));
        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_ACTIVITY_DETAILS));
        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_ACTIVITY_TYPE));
        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_KODDOC));
        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_USER_ID));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_KODDOC));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_USER_ID));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM, KEY_KODDOC));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM, KEY_USER_ID));
        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_ACTIVITY_DETAILS, KEY_ACTIVITY_TYPE));
        attributeSets.add(Arrays.asList(KEY_PROCESS, KEY_ACTIVITY_DETAILS, KEY_ACTIVITY_TYPE, KEY_USER_ID));
        return attributeSets;
    }

    @Override
    public TraceInvariantList getInvariants() {
        TraceInvariantList list = new TraceInvariantList();
        return list;
    }

    public Map<String, Float> calculateCoefficientsMap() {
        Map<String, Float> correctionMap = new HashMap<>();
        correctionMap.put(KEY_ACTIVITY_TYPE, 0.520899941f);
        correctionMap.put(KEY_PROCESS, 0.529700019f);
        correctionMap.put(KEY_ACTIVITY_DETAILS, 0.529700019f);
        correctionMap.put(KEY_USER_ID, 0.319400038f);
        return correctionMap;
    }
}
