import algorithms.ILogAlgorithm;
import algorithms.filter.BaseFilter;
import algorithms.search.trace.PredefinedAttributeWeightsSearchAlgorithm;
import algorithms.search.trace.TraceSearchingAlgorithm;
import algorithms.search.trace.locator.invariant.Node;
import algorithms.search.trace.locator.invariant.TraceInvariantList;
import com.google.common.collect.Lists;

import java.util.*;

public class AnalyzeProcessFactory {

    public static final String KEY_PRODUCT = "product";
    public static final String KEY_ORG_RESOURCE = "org:resource";
    public static final String KEY_ORG_ROLE = "org:role";
    public static final String KEY_ORG_GROUP = "org:group";

    private List<ILogAlgorithm<?>> algorithms = new ArrayList<>();

    public AlgorithmSequence get(AnalyzeProcessVariant variant) {
        switch (variant) {
            case MERGE_ALL_EVENTS_IN_ONE_TRACE:
                algorithms.add(new BaseFilter());
                break;
            case PREDEFINED_ATTRIBUTE_WEIGHT_SEARCH_ALGORITHM:
                algorithms.add(PredefinedAttributeWeightsSearchAlgorithm.init(initAttributeSetsFor400TraceLog()));
                break;
            case TRACE_SEARCH_ATTRIBUTE_COMPARISION_BASED:
                algorithms.add(TraceSearchingAlgorithm.initAlgorithmBasedOnAttributeComparision(calculateCoefficientsMap()));
                break;
            case TRACE_SEARCH_INVARIANT_BASED:
                algorithms.add(TraceSearchingAlgorithm.initAlgorithmBasedOnInvariantComparision(getInvariants()));
                break;
            default:
                return new AlgorithmSequence(
                        Lists.newArrayList(
                                new BaseFilter(),
                                PredefinedAttributeWeightsSearchAlgorithm.init(initAttributeSetsFor400TraceLog()),
                                TraceSearchingAlgorithm.initAlgorithmBasedOnAttributeComparision(calculateCoefficientsMap())
                        ));
        }
        return new AlgorithmSequence(algorithms);
    }

    public enum AnalyzeProcessVariant {
        DEFAULT,
        MERGE_ALL_EVENTS_IN_ONE_TRACE,
        PREDEFINED_ATTRIBUTE_WEIGHT_SEARCH_ALGORITHM,
        TRACE_SEARCH_ATTRIBUTE_COMPARISION_BASED,
        TRACE_SEARCH_INVARIANT_BASED
    }


    private List<List<String>> initAttributeSetsFor400TraceLog() {
//        attributeSets.add(Arrays.asList("product"));
//        attributeSets.add(Arrays.asList("org:group"));
//        attributeSets.add(Arrays.asList("org:resource"));
//        attributeSets.add(Arrays.asList("organization involved"));
//        attributeSets.add(Arrays.asList("org:role"));
//        attributeSets.add(Arrays.asList("org:resource", "product"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource"));
//        attributeSets.add(Arrays.asList("org:group", "org:role", "product"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "product"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "organization involved"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "org:role", "product"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "organization involved", "org:role"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "organization involved", "org:role", "product"));
//        attributeSets.add(Arrays.asList("org:role", "org:resource"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "org:role"));

        List<List<String>> attributeSets = new LinkedList<>();
        attributeSets.add(Arrays.asList("concept:name"));
        attributeSets.add(Arrays.asList("Doc_type"));
        attributeSets.add(Arrays.asList("koddoc"));
        attributeSets.add(Arrays.asList("User_Id"));
        attributeSets.add(Arrays.asList("File"));
        attributeSets.add(Arrays.asList("concept:name", "Doc_type"));
        attributeSets.add(Arrays.asList("concept:name", "koddoc"));
        attributeSets.add(Arrays.asList("concept:name", "User_Id"));
        attributeSets.add(Arrays.asList("concept:name", "Doc_type"));
        attributeSets.add(Arrays.asList("concept:name", "File"));
        attributeSets.add(Arrays.asList("Doc_type", "File"));
        attributeSets.add(Arrays.asList("koddoc", "File"));
        attributeSets.add(Arrays.asList("User_Id", "File"));
        attributeSets.add(Arrays.asList("User_Id", "koddoc"));
        attributeSets.add(Arrays.asList("User_Id", "File"));
        attributeSets.add(Arrays.asList("Doc_type", "koddoc", "User_Id"));
        attributeSets.add(Arrays.asList("File", "koddoc", "User_Id"));
        attributeSets.add(Arrays.asList("File", "Doc_type", "User_Id"));
        attributeSets.add(Arrays.asList("File", "Doc_type", "koddoc"));

        return attributeSets;
    }

    private static Map<String, Float> calculateCoefficientsMap() {
        Map<String, Float> correctionMap = new HashMap<>();
        correctionMap.put(KEY_PRODUCT, 0.520899941f);
        correctionMap.put(KEY_ORG_RESOURCE, 0.159700019f);
        correctionMap.put(KEY_ORG_GROUP, 0.319400038f);
//        Map<String, Float> stringFloatMap = new CoefficientMapBuilder(xLog, correctionMap, false).build();
        return correctionMap;
    }

    private static TraceInvariantList getInvariants() {
        TraceInvariantList list = new TraceInvariantList();

        Node product = new Node(KEY_PRODUCT);
        product.addInvariant(Arrays.asList(new String[]{"PROD582"}));

        Node resource = new Node(KEY_ORG_RESOURCE);
        Node orgGroup = new Node(KEY_ORG_GROUP);
        return list;

    }
}
