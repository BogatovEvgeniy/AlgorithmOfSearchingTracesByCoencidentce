package base;

import io.FileUtils;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.util.List;

public class LogTestBaseClass {

    protected static XLog getLogInstance() {

        File testLog = new File(FileUtils.getCurrentDirectoryPath() + "TestLog_4unique_traces_for_invariant_test.xes");
        XLog log = null;

        try {
            FileUtils.createFileIfNeed(testLog);
            XesXmlParser xUniversalParser = new XesXmlParser();
            List<XLog> parse = xUniversalParser.parse(testLog);
            if (parse == null || parse.size() == 0) {
                assert false;
            }

            log = parse.get(0);

        } catch (Exception e) {
            assert false;
        }
        return log;
    }

}
