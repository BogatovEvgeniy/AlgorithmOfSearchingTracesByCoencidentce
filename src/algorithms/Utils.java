package algorithms;

import java.util.*;

public class Utils {


    public static Map<Integer, Float> sortMap(Map<Integer, Float> traceCoincidenceMap) {
        Map<Integer, Float> result = new HashMap<>();
        List<Float> values = new LinkedList<>();
        values.addAll(traceCoincidenceMap.values());
        Collections.sort(values, Float::compare);

        for (Integer traceIndex : traceCoincidenceMap.keySet()) {
            for (Float value : values) {
                if (traceCoincidenceMap.get(traceIndex).equals(value)) {
                    result.put(traceIndex, value);
                    break;
                }
            }
        }
        return result;
    }

    public static int[] toPrimitives(Set<Integer> integers) {
        return integers.stream().mapToInt(Integer::intValue).toArray();
    }
}
