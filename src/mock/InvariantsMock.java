package mock;

import java.util.Arrays;
import java.util.List;

public class InvariantsMock {
    public static final String ORG_GROUP = "org:group";
    public static final String ORG_RESOURCE = "org:resource";
    public static final String ORG_ORGANIZATION_INVOLVED = "organization involved";
    public static final String ORG_ROLE = "org:role";
    public static final String PRODUCT = "product";

    // GROUP
    public static final List<String> group_invar_1 = Arrays.asList(
            new String[]{"V30", "V30", "V5 3rd", "V5 3rd", "V30", "V13 2nd 3rd",
            "V13 2nd 3rd", "V30", "V30", "V5 3rd", "V5 3rd", "V5 3rd", "V5 3rd",
            "V5 3rd", "V5 3rd", "V5 3rd", "V5 3rd", "V5 3rd"});

    public static final List<String> group_invar_2 = Arrays.asList(
            new String[]{"S42", "S42", "N52 2nd", "N52 2nd", "N52 2nd", "O3 3rd",
            "O3 3rd", "O3 3rd", "G140 2nd", "G140 2nd", "G140 2nd", "G140 2nd",
            "G140 2nd", "G140 2nd", "G140 2nd", "G140 2nd", "G140 2nd", "G140 2nd",
            "G140 2nd", "N52 2nd", "N52 2nd", "N52 2nd", "N52 2nd", "N52 2nd",
            "N52 2nd", "N52 2nd", "N52 2nd", "N52 2nd", "G137 2nd", "G137 2nd",
            "M25", "M25", "M25", "M25", "G142 2nd", "G142 2nd", "M25", "S42", "S42", "S42"});


    // RESOURCE
    public static final List<String> resource_invar_1 = Arrays.asList(
            new String[]{"Frederic", "Frederic", "Frederic", "Anne Claire",
            "Anne Claire", "Anne Claire", "Anne Claire", "Anne Claire", "Eric", "Eric",
            "Anne Claire", "Anne Claire", "Sarah", "Sarah", "Loic", "Siebel"});

    public static final List<String> resource_invar_2 = Arrays.asList(
            new String[]{"Adam", "Adam", "Adam", "Denny", "Denny", "Denny", "Paul", "Paul",
            "Åse", "Åse", "Joseph", "Joseph", "Björn T", "Björn T", "Björn T", "Björn T",
            "Åse", "Åse", "Suliman", "Suliman", "Denny", "Denny", "Jon", "Jon", "Jon", "Jon",
            "Jon", "Jon", "Jon", "Gustav", "Magnus", "Magnus", "Magnus", "Magnus", "Magnus",
            "Andreas", "Torbjörn", "Torbjörn", "Siebel"});
}
