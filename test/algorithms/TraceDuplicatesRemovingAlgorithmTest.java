package algorithms;

import algorithms.removal.TraceDuplicatesRemovingAlgorithm;
import io.FileUtils;
import io.log.ILogWriter;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class TraceDuplicatesRemovingAlgorithmTest {

    @Test
    public void proceed() {
        File testLog = new File(FileUtils.getCurrentDirectoryPath() + "TestLog_4unique_traces_each_duplicated_twice.xes");
        try {
            FileUtils.createFileIfNeed(testLog);
            XesXmlParser xUniversalParser = new XesXmlParser();
            List<XLog> logList = xUniversalParser.parse(testLog);
            Assert.assertFalse(logList.isEmpty());
            Assert.assertTrue(logList.size() == 1);

            ILogWriter logWriter = getLogWriterInstance();
            XLog cleanedLog = new TraceDuplicatesRemovingAlgorithm(logWriter, "attr1").proceed(logList.get(0));
            Assert.assertThat(cleanedLog, notNullValue());
            Assert.assertThat(cleanedLog.size(), is(3));
        } catch (Exception e) {
            assert false;
        }
    }

    private ILogWriter getLogWriterInstance() {
        return (log, destDirectory, fileName) -> {
            File file = new File(destDirectory + fileName + ".xes");
            try {
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                new XesXmlSerializer().serialize(log, new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;
        };
    }
}