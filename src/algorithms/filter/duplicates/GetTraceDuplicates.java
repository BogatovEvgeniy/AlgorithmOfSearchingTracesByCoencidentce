package algorithms.filter.duplicates;

import javafx.util.Pair;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;

import java.util.List;

public class GetTraceDuplicates extends ValueSetBasedFilter {

    public GetTraceDuplicates(List<Pair<String, String>> attrValPairs) {
        super(attrValPairs);
    }

    @Override
    protected void addEvents(XLog originLog, XLog resLog) {
        for (int traceIndex = 0; traceIndex < originLog.size(); traceIndex++) {
            if (resLog.get(0).size() == 0){
                for (Pair<String, String> pair : attrValPairs) {
                    XAttribute attribute = originLog.get(traceIndex).get(0).getAttributes().get(pair.getKey());
                    if (attribute.toString().equals(pair.getValue())){
                        resLog.get(0).addAll(originLog.get(traceIndex).subList(0, originLog.get(traceIndex).size()));
                    }
                }
            } else if (isAttrValuePairExists(originLog.get(traceIndex), resLog)) {
                traceCounter++;
                resLog.add(originLog.get(traceIndex));
            }
        }
    }
}
