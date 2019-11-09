package algorithms.filter.duplicates;

import javafx.util.Pair;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;

import java.util.List;

public class RemoveTraceDuplicates extends ValueSetBasedFilter {

    public RemoveTraceDuplicates(List<Pair<String, String>> attributesForComparision) {
        super(attributesForComparision);
    }

    @Override
    protected void addEvents(XLog originLog, XLog algorithmResultLog) {
        for (int traceIndex = 0; traceIndex < originLog.size(); traceIndex++) {
            if (algorithmResultLog.size() == 0){
                for (Pair<String, String> pair : attrValPairs) {
                    XAttribute attribute = originLog.get(traceIndex).get(0).getAttributes().get(pair.getKey());
                    if (attribute.toString().equals(pair.getValue())){
                        algorithmResultLog.add(originLog.get(traceIndex));
                    }
                }
            } else if(!isDuplicatesForAttrValuesExists(originLog.get(traceIndex), algorithmResultLog)) {
                traceCounter++;
                algorithmResultLog.add(originLog.get(traceIndex));
            }
        }
    }
}
