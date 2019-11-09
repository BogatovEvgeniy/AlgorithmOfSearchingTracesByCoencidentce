package usecases;

import algorithms.search.trace.locator.invariant.Node;
import algorithms.search.trace.locator.invariant.TraceInvariantList;

import java.util.*;

public class BPIChallenge2013IncidentsUseCase implements IUseCase, ICoefficientMapCalculator {

    public static final String KEY_PRODUCT = "product";
    public static final String KEY_ORG_RESOURCE = "org:resource";
    public static final String KEY_ORG_ROLE = "org:role";
    public static final String KEY_ORG_GROUP = "org:group";

    @Override
    public String getLogName() {
        return "3Instances5traces";
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
        attributeSets.add(Arrays.asList("concept:name","org:group", "org:resource", "product"));
        attributeSets.add(Arrays.asList("concept:name","org:group", "org:resource", "org:role"));
        attributeSets.add(Arrays.asList("concept:name", "org:group", "org:resource", "organization involved"));
        attributeSets.add(Arrays.asList("concept:name", "org:group", "org:resource", "org:role", "product"));
        attributeSets.add(Arrays.asList("concept:name", "org:group", "org:resource", "organization involved", "org:role"));
        attributeSets.add(Arrays.asList("concept:name", "org:group", "org:resource", "organization involved", "org:role", "product"));

//        attributeSets.add(Arrays.asList("concept:name"));
//        attributeSets.add(Arrays.asList("Doc_type"));
//        attributeSets.add(Arrays.asList("koddoc"));
//        attributeSets.add(Arrays.asList("User_Id"));
//        attributeSets.add(Arrays.asList("File"));
//        attributeSets.add(Arrays.asList("concept:name", "Doc_type"));
//        attributeSets.add(Arrays.asList("concept:name", "koddoc"));
//        attributeSets.add(Arrays.asList("concept:name", "User_Id"));
//        attributeSets.add(Arrays.asList("concept:name", "Doc_type"));
//        attributeSets.add(Arrays.asList("concept:name", "File"));
//        attributeSets.add(Arrays.asList("Doc_type", "File"));
//        attributeSets.add(Arrays.asList("koddoc", "File"));
//        attributeSets.add(Arrays.asList("User_Id", "File"));
//        attributeSets.add(Arrays.asList("User_Id", "koddoc"));
//        attributeSets.add(Arrays.asList("User_Id", "File"));
//        attributeSets.add(Arrays.asList("Doc_type", "koddoc", "User_Id"));
//        attributeSets.add(Arrays.asList("File", "koddoc", "User_Id"));
//        attributeSets.add(Arrays.asList("File", "Doc_type", "User_Id"));
//        attributeSets.add(Arrays.asList("File", "Doc_type", "koddoc"));

        return attributeSets;
    }

    @Override
    public TraceInvariantList getInvariants() {
        TraceInvariantList list = new TraceInvariantList();

        Node product = new Node(KEY_PRODUCT);
        product.addInvariant(Arrays.asList(new String[]{"PROD542"}));

        Node resource = new Node(KEY_ORG_RESOURCE);
        Node orgGroup = new Node(KEY_ORG_GROUP);
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
