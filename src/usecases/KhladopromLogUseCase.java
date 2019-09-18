package usecases;

import algorithms.search.trace.locator.invariant.TraceInvariantList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class KhladopromLogUseCase implements IUseCase {

    public static final String KEY_ACTION = "Action";
    public static final String KEY_TASK_ID = "Task_ID";
    public static final String KEY_ACTIVITY_DETAILS = "Activity_details";
    public static final String KEY_ACTIVITY_TYPE = "Activity_type";
    public static final String KEY_DB_FILE_NUM = "DB_File_Num";
    public static final String KEY_UNIQUESOD = "uniquesod";
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
        attributeSets.add(Arrays.asList(KEY_TASK_ID));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_TYPE));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_UNIQUESOD));
        attributeSets.add(Arrays.asList(KEY_KODDOC));
        attributeSets.add(Arrays.asList(KEY_USER_ID));
        attributeSets.add(Arrays.asList(KEY_TASK_ID, KEY_ACTIVITY_DETAILS));
        attributeSets.add(Arrays.asList(KEY_TASK_ID, KEY_ACTIVITY_TYPE));
        attributeSets.add(Arrays.asList(KEY_TASK_ID, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_TASK_ID, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_TASK_ID, KEY_UNIQUESOD));
        attributeSets.add(Arrays.asList(KEY_TASK_ID, KEY_KODDOC));
        attributeSets.add(Arrays.asList(KEY_TASK_ID, KEY_USER_ID));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_UNIQUESOD));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_KODDOC));
        attributeSets.add(Arrays.asList(KEY_ACTIVITY_DETAILS, KEY_USER_ID));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM, KEY_DB_FILE_NUM));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM, KEY_UNIQUESOD));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM, KEY_KODDOC));
        attributeSets.add(Arrays.asList(KEY_DB_FILE_NUM, KEY_USER_ID));
        attributeSets.add(Arrays.asList(KEY_TASK_ID, KEY_ACTIVITY_DETAILS, KEY_ACTIVITY_TYPE));
        attributeSets.add(Arrays.asList(KEY_TASK_ID, KEY_ACTIVITY_DETAILS, KEY_ACTIVITY_TYPE, KEY_USER_ID));
        return attributeSets;
    }

    @Override
    public TraceInvariantList getInvariants() {
        TraceInvariantList list = new TraceInvariantList();
        return list;
    }
}
