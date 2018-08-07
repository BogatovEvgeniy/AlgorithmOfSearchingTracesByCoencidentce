package io;

import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class XEStoCSVWriterTest {

            String checkString =
            "traceId;attr2;attr1;attr4;time:timestamp;attr3;\n"+
            "0;val1;val1;val1;Apr 12, 2010 11:05:58 AM;val1;\n"+
            "0;val2;val2;val2;Apr 12, 2010 11:05:58 AM;val2;\n"+
            "0;val3;val3;val3;Apr 12, 2010 11:05:58 AM;val3;\n"+
            "1;val2;val2;val2;Apr 12, 2010 11:05:58 AM;val2;\n"+
            "1;val1;val1;val1;Apr 12, 2010 11:05:58 AM;val1;\n"+
            "1;val3;val3;val3;Apr 12, 2010 11:05:58 AM;val3;\n"+
            "2;val3;val3;val3;Apr 12, 2010 11:05:58 AM;val3;\n"+
            "2;val1;val1;val1;Apr 12, 2010 11:05:58 AM;val1;\n"+
            "2;val2;val2;val2;Apr 12, 2010 11:05:58 AM;val2;\n";

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