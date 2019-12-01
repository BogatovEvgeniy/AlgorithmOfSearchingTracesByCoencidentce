package usecases;

import algorithms.search.trace.locator.invariant.IEventRule;
import algorithms.search.trace.locator.invariant.TraceInvariantList;
import algorithms.search.trace.locator.invariant.rule.event.Or;
import algorithms.search.trace.locator.invariant.rule.log.Final;
import algorithms.search.trace.locator.invariant.rule.log.Initial;
import com.google.common.collect.Lists;

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
        List<IEventRule> rules = new LinkedList<>();

        ArrayList<String> allOtherValues = Lists.newArrayList(
                "тип [Лимитки ->Списание на производство (склад сырья)]"
                , "тип [Лимитки ->Списание на производство (фасовочное отделение)]"
                , "тип [Лимитки ->Списание на производство (вафельное отделение)]"
                , "тип [Лимитки ->Списание на производство (заготовительное отделение)]"
                , "тип [Производство полуфаб->Рабочая рецептура на смесь]"
                , "тип [Производство полуфаб->Рабочая рецептура на стаканчики]"
                , "тип [Производство полуфаб->Рабочая рецептура на глазурь сиропы кремы]"
                , "тип [Производство полуфаб->Разборка брака ГП]"
                , "тип [Приход из производст->Выход ГП из закалки]"
                , "тип [Внутренние перемещен->Списание ГП на переработку (склад ГП)]"
                , "тип [Внутренние перемещен->Перемещение брака (склад ГП->ЗО)]"
                , "тип [Фасовка ГП ->Фасовка ГП]");

        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Лимитки ->Списание на производство (склад сырья)]", allOtherValues));
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Лимитки ->Списание на производство (фасовочное отделение)]", allOtherValues));
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Лимитки ->Списание на производство (вафельное отделение)]", allOtherValues));
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Лимитки ->Списание на производство (заготовительное отделение)]", allOtherValues));
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Производство полуфаб->Рабочая рецептура на смесь]", allOtherValues));
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Производство полуфаб->Рабочая рецептура на стаканчики]", allOtherValues));
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Производство полуфаб->Рабочая рецептура на глазурь сиропы кремы]", allOtherValues));
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Производство полуфаб->Разборка брака ГП]", allOtherValues));
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Приход из производст->Выход ГП из закалки]", allOtherValues));
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Внутренние перемещен->Списание ГП на переработку (склад ГП)]", allOtherValues));
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Внутренние перемещен->Перемещение брака (склад ГП->ЗО)]", allOtherValues));
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Фасовка ГП ->Фасовка ГП]", allOtherValues));

        List<String> afterInitialValues = new LinkedList<>(allOtherValues);
        afterInitialValues.remove("тип [Приход из производст->Выход ГП из закалки]");
        rules.add(new Or(KEY_ACTIVITY_TYPE, "тип [Приход на склад ->Приход сырья (лаборатория)]", afterInitialValues));
        list.addInvariantBatchEventRule(rules);

        list.addInitialEvents(new Initial(KEY_ACTIVITY_TYPE, "тип [Приход на склад ->Приход сырья (лаборатория)]"));
        list.addFinalEvents(new Final(KEY_ACTIVITY_TYPE, "тип [Приход из производст->Выход ГП из закалки]"));
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
