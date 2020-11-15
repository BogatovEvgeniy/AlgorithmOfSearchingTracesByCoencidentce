import io.FileUtils;
import io.log.ILogWriter;
import io.log.XEStoCSVWriter;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public class XEStoCSVWriterTest {

    String checkString =
            "traceId,attr2,attr1,attr4,time:timestamp,attr3,\r\n"
            + "0,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,\r\n"
            + "0,val2,val2,val2,2010-04-12T11:05:58.000+0300,val2,\r\n"
            + "0,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "1,val2,val2,val2,2010-04-12T11:05:58.000+0300,val2,\r\n"
            + "1,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,\r\n"
            + "1,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "2,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "2,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,\r\n"
            + "2,val2,val2,val2,2010-04-12T11:05:58.000+0300,val2,\r\n"
            + "3,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "3,val2,val2,val2,2010-04-12T10:55:58.000+0300,val2,\r\n"
            + "3,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,\r\n"
            + "4,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,\r\n"
            + "4,val2,val2,val2,2010-04-12T11:05:58.000+0300,val2,\r\n"
            + "4,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "5,val2,val2,val2,2010-04-12T11:05:58.000+0300,val2,\r\n"
            + "5,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,\r\n"
            + "5,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "6,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "6,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,\r\n"
            + "6,val2,val2,val2,2010-04-12T11:05:58.000+0300,val2,\r\n"
            + "7,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "7,val2,val2,val2,2010-04-12T11:05:58.000+0300,val2,\r\n"
            + "7,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,\r\n"
            + "8,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,\r\n"
            + "8,val2,val2,val2,2010-04-12T11:05:58.000+0300,val2,\r\n"
            + "8,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "9,val2,val2,val2,2010-04-12T11:05:58.000+0300,val2,\r\n"
            + "9,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,\r\n"
            + "9,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "10,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "10,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,\r\n"
            + "10,val2,val2,val2,2010-04-12T11:05:58.000+0300,val2,\r\n"
            + "11,val3,val3,val3,2010-04-12T11:05:58.000+0300,val3,\r\n"
            + "11,val2,val2,val2,2010-04-12T11:05:58.000+0300,val2,\r\n"
            + "11,val1,val1,val1,2010-04-12T11:05:58.000+0300,val1,";
    @Test
    public void write() {
        String currentDirectoryPath = FileUtils.getCurrentDirectoryPath();
        File testLog = new File(currentDirectoryPath + "TestLog_4unique_traces_each_duplicated_twice.xes");
        try {
            FileUtils.createFileIfNeed(testLog);
            XesXmlParser xUniversalParser = new XesXmlParser();
            List<XLog> logList = xUniversalParser.parse(testLog);
            Assert.assertFalse(logList.isEmpty());
            Assert.assertTrue(logList.size() == 1);

            ILogWriter logWriter = new XEStoCSVWriter();
            File cvsTest = logWriter.write(logList.get(0), currentDirectoryPath, "CVSTest");
            FileReader fileReader = new FileReader(cvsTest);
            int read;
            StringBuffer controlStringBuffer = new StringBuffer();
            while ((read =fileReader.read()) != -1){
                controlStringBuffer.append((char)read);
            }
            fileReader.close();
            Assert.assertTrue(controlStringBuffer.toString().contains(checkString));

        } catch (Exception e) {
            assert false;
        }
    }
}