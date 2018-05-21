package algorithms.tracesearch;

import algorithms.ILogAlgorithm;
import com.sun.istack.internal.Nullable;
import exceptions.LogParsingError;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;


public interface ITraceSearchingAlgorithm extends ILogAlgorithm {

    /**
     * Launch method execution
     *
     * @return
     * @throws LogParsingError
     */
    XLog proceed(XLog origin);

    void setTraceLocator(TraceLocator traceLocator);

    void setTraceLocators(ILocatorResultMerger locatorResultMerger, TraceLocator... traceLocator);

    interface TraceLocator {

        int TRACE_INDEX_UNDEFINED_VALUE = -1;

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
         * @return Indexes of trace ordered descending by suitability. The NULL value can be returned in case of
         * no traces in log was suitable for the event value in parameters
         */
        @Nullable
        int[] defineTrace(XLog xLog, XEvent event);
    }
}
