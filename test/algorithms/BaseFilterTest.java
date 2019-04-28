package algorithms;

import algorithms.filter.BaseFilter;
import io.FileUtils;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class BaseFilterTest {


    @Test
    public void proceed() {
        File testLog = new File(FileUtils.getCurrentDirectoryPath() + "TestLog_4unique_traces_each_duplicated_twice.xes");
        try {
            FileUtils.createFileIfNeed(testLog);
            XesXmlParser xUniversalParser = new XesXmlParser();
            List<XLog> logList = xUniversalParser.parse(testLog);

            Assert.assertFalse(logList.isEmpty());
            Assert.assertTrue(logList.size() == 1);

            XLog cleanedLog = new BaseFilter().proceed(logList.get(0));
            Assert.assertThat(cleanedLog, notNullValue());
            Assert.assertFalse(cleanedLog.isEmpty());
            Assert.assertTrue(cleanedLog.get(0).size() == 12 * 3);

        } catch (Exception e) {
            assert false;
        }
    }
}