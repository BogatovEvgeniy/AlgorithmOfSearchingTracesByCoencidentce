package algorithms.search.trace;

import org.deckfour.xes.model.XLog;

import java.util.List;

public class TraceValidator {
    public TraceValidator() {
    }

    public boolean validateIsEmpty(XLog parsedLog) {
        // If there are no resultLog, trace or event nothing will be written in file
        if (parsedLog.size() == 0) return true;
        if (parsedLog.get(0).size() == 0) return true;
        return false;
    }
}