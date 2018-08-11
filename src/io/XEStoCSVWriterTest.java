package io;

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
            + "0,val2,val2,val2,2010-04-12T11:05:58.001+0300,val2,\r\n"
            + "0,val3,val3,val3,2010-04-12T11:05:58.002+0300,val3,\r\n"
            + "1,val2,val2,val2,2010-04-12T11:05:58.003+0300,val2,\r\n"
            + "1,val1,val1,val1,2010-04-12T11:05:58.004+0300,val1,\r\n"
            + "1,val3,val3,val3,2010-04-12T11:05:58.005+0300,val3,\r\n"
            + "2,val3,val3,val3,2010-04-12T11:05:58.006+0300,val3,\r\n"
            + "2,val1,val1,val1,2010-04-12T11:05:58.007+0300,val1,\r\n"
            + "2,val2,val2,val2,2010-04-12T11:05:58.008+0300,val2,\r\n"
            + "3,val3,val3,val3,2010-04-12T11:05:58.009+0300,val3,\r\n"
            + "3,val2,val2,val2,2010-04-12T10:55:58.010+0300,val2,\r\n"
            + "3,val1,val1,val1,2010-04-12T11:05:58.011+0300,val1,\r\n"
            + "4,val1,val1,val1,2010-04-12T11:05:58.012+0300,val1,\r\n"
            + "4,val2,val2,val2,2010-04-12T11:05:58.013+0300,val2,\r\n"
            + "4,val3,val3,val3,2010-04-12T11:05:58.014+0300,val3,\r\n"
            + "5,val2,val2,val2,2010-04-12T11:05:58.015+0300,val2,\r\n"
            + "5,val1,val1,val1,2010-04-12T11:05:58.016+0300,val1,\r\n"
            + "5,val3,val3,val3,2010-04-12T11:05:58.017+0300,val3,\r\n"
            + "6,val3,val3,val3,2010-04-12T11:05:58.018+0300,val3,\r\n"
            + "6,val1,val1,val1,2010-04-12T11:05:58.019+0300,val1,\r\n"
            + "6,val2,val2,val2,2010-04-12T11:05:58.020+0300,val2,\r\n"
            + "7,val3,val3,val3,2010-04-12T11:05:58.021+0300,val3,\r\n"
            + "7,val2,val2,val2,2010-04-12T11:05:58.022+0300,val2,\r\n"
            + "7,val1,val1,val1,2010-04-12T11:05:58.023+0300,val1,\r\n"
            + "8,val1,val1,val1,2010-04-12T11:05:58.024+0300,val1,\r\n"
            + "8,val2,val2,val2,2010-04-12T11:05:58.025+0300,val2,\r\n"
            + "8,val3,val3,val3,2010-04-12T11:05:58.027+0300,val3,\r\n"
            + "9,val2,val2,val2,2010-04-12T11:05:58.028+0300,val2,\r\n"
            + "9,val1,val1,val1,2010-04-12T11:05:58.029+0300,val1,\r\n"
            + "9,val3,val3,val3,2010-04-12T11:05:58.030+0300,val3,\r\n"
            + "10,val3,val3,val3,2010-04-12T11:05:58.031+0300,val3,\r\n"
            + "10,val1,val1,val1,2010-04-12T11:05:58.032+0300,val1,\r\n"
            + "10,val2,val2,val2,2010-04-12T11:05:58.033+0300,val2,\r\n"
            + "11,val3,val3,val3,2010-04-12T11:05:58.034+0300,val3,\r\n"
            + "11,val2,val2,val2,2010-04-12T11:05:58.035+0300,val2,\r\n"
            + "11,val1,val1,val1,2010-04-12T11:05:58.036+0300,val1,";
    @Test
    public void write() {
        File testLog = new File("D:\\IntelliJProjects\\Diss\\AlgorithmOfSearchingTracesByCoencidentce\\TestLog_4unique_traces_each_duplicated_twice.xes");
        XesXmlParser xUniversalParser = new XesXmlParser();
        try {
            List<XLog> logList = xUniversalParser.parse(testLog);
            Assert.assertFalse(logList.isEmpty());
            Assert.assertTrue(logList.size() == 1);

            ILogWriter logWriter = new XEStoCSVWriter();
            File cvsTest = logWriter.write(logList.get(0), "D:\\IntelliJProjects\\Diss\\AlgorithmOfSearchingTracesByCoencidentce\\", "CVSTest");
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