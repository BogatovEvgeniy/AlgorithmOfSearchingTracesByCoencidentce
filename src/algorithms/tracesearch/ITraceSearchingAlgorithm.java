package algorithms.tracesearch;

import exceptions.LogParsingError;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;


public interface ITraceSearchingAlgorithm {

    /**
     * Launch method execution
     *
     * @return
     * @throws LogParsingError
     */
    XLog proceed() throws LogParsingError;

    void registerTraceLocator(TraceLocator traceLocator);

    void unregisterTraceLocator(String traceLocatorID);

    interface TraceLocator {

        /**
         * An is of current locator. Used to manage registered in algorithm locators
         *
         * @return
         */
        String getId();

        /**
         * The method which defines most suitable traces for current event
         *
         * @param xLog  - current log as result of current state of analyze
         * @param event - current event
         * @return Indexes of trace ordered descending by suitability
         */
        int[] defineTrace(XLog xLog, XEvent event);
    }
}
