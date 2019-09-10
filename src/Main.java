import io.*;
import io.log.XesLogReader;
import io.log.XesLogWriter;
import org.deckfour.xes.model.XLog;

import java.io.File;

public class Main {

    private static final String SOURCE_DIR = FileUtils.getCurrentDirectoryPath() + "Sources" + File.separator;

    private static final String FILE_EXTENSION = ".xes";

    private static LogWriter logWriter = new LogWriter();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        try {
            if (args != null && args.length > 0) {
                CommandParser.parse(args, logWriter);
            } else {
                launchParsingAlgorithms();
            }

            // Track execution time
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void launchParsingAlgorithms() throws Exception {
        // Config input data
        String srcFileName = "BPI_Challenge_2013_incidents";
        String srcFilePath = SOURCE_DIR + srcFileName + FILE_EXTENSION;

        XLog originLog = new XesLogReader().parse(new File(srcFilePath)).get(0);
        AnalyzeProcessAlgorithmsFactory
                .get(AnalyzeProcessAlgorithmsFactory.AlgorithmVariant.DEFAULT)
                .launch(new XesLogWriter(), originLog);

    }
}
