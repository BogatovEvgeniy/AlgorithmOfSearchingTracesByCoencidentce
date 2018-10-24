package algorithms.search.invariant;

import base.LogTestBaseClass;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class CompareEventDataTest extends LogTestBaseClass {

    /**
     * Check compare data logic correctness
     *
     * Data was gotten from "TestLog_4unique_traces_each_duplicated_twice.xes"
     * First event of log
     * 	<event>
     * 			<string key="attr1" value="val1"/>
     * 			<string key="attr2" value="val1"/>
     * 			<string key="attr3" value="val1"/>
     * 			<string key="attr4" value="val1"/>
     * 			<date key="time:timestamp" value="2010-04-12T10:05:58.000+02:00"/>
     * 		</event>
     *
     * 	First Trace in the test log
     * 	<trace>
     * 		<event>
     * 			<string key="attr1" value="val1"/>
     * 			<string key="attr2" value="val1"/>
     * 			<string key="attr3" value="val1"/>
     * 			<string key="attr4" value="val1"/>
     * 			<date key="time:timestamp" value="2010-04-12T10:05:58.000+02:00"/>
     * 		</event>
     * 		<event>
     * 			<string key="attr1" value="val2"/>
     * 			<string key="attr2" value="val2"/>
     * 			<string key="attr3" value="val2"/>
     * 			<string key="attr4" value="val2"/>
     * 			<date key="time:timestamp" value="2010-04-12T10:05:58.001+02:00"/>
     * 		</event>
     * 		<event>
     * 			<string key="attr1" value="val3"/>
     * 			<string key="attr2" value="val3"/>
     * 			<string key="attr3" value="val3"/>
     * 			<string key="attr4" value="val3"/>
     * 			<date key="time:timestamp" value="2010-04-12T10:05:58.002+02:00"/>
     * 		</event>
     * 	</trace>
     */
    @Test
    public void initCompareEventData() {
        XLog logInstance = getLogInstance();
        XEvent event = logInstance.get(0).get(0);

        CompareEventData compareEventData = CompareEventData.initCompareEventData(event, logInstance.get(0));

        Map<String, String> eventValues = getAttrValMapForTestEvent();
        Map<String, List<String>> traceValues = getAttrValMapForTestTrace();

        assertTrue(compareEventData.eventValues.equals(eventValues));
        assertTrue(compareEventData.inTraceValues.equals(traceValues));
    }

    /**
     * First event of log
     * 	<event>
     * 		<string key="attr1" value="val1"/>
     * 		<string key="attr2" value="val1"/>
     * 		<string key="attr3" value="val1"/>
     *      <string key="attr4" value="val1"/>
     *      <date key="time:timestamp" value="2010-04-12T10:05:58.000+02:00"/>
     * 	</event>
     *
     * @return
     */

    private Map<String, String> getAttrValMapForTestEvent() {
        Map<String, String> result = new HashMap<>();
        result.put("attr1", "val1");
        result.put("attr2", "val1");
        result.put("attr3", "val1");
        result.put("attr4", "val1");
        result.put("time:timestamp", "2010-04-12T11:05:58+03:00"); // HARD CODED CONVERTATION FOR time in Ukraine. Should be improved
        return result;
    }

    /**
     * 	First Trace in the test log
     * 	<trace>
     * 		<event>
     * 			<string key="attr1" value="val1"/>
     * 			<string key="attr2" value="val1"/>
     * 			<string key="attr3" value="val1"/>
     * 			<string key="attr4" value="val1"/>
     * 			<date key="time:timestamp" value="2010-04-12T10:05:58.000+02:00"/>
     * 		</event>
     * 		<event>
     * 			<string key="attr1" value="val2"/>
     * 			<string key="attr2" value="val2"/>
     * 			<string key="attr3" value="val2"/>
     * 			<string key="attr4" value="val2"/>
     * 			<date key="time:timestamp" value="2010-04-12T10:05:58.001+02:00"/>
     * 		</event>
     * 		<event>
     * 			<string key="attr1" value="val3"/>
     * 			<string key="attr2" value="val3"/>
     * 			<string key="attr3" value="val3"/>
     * 			<string key="attr4" value="val3"/>
     * 			<date key="time:timestamp" value="2010-04-12T10:05:58.002+02:00"/>
     * 		</event>
     * 	</trace>
     */
    private Map<String, List<String>> getAttrValMapForTestTrace() {
        Map<String, List<String>> result = new HashMap<>();
        result.put("attr1", Arrays.asList("val1", "val2", "val3"));
        result.put("attr2", Arrays.asList("val1", "val2", "val3"));
        result.put("attr3", Arrays.asList("val1", "val2", "val3"));
        result.put("attr4", Arrays.asList("val1", "val2", "val3"));
        result.put("time:timestamp", Arrays.asList("2010-04-12T11:05:58+03:00", "2010-04-12T11:05:58+03:00", "2010-04-12T11:05:58+03:00"));
        return result;
    }
}