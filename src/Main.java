import algorithms.removal.MergeEventsInOneTraceAndTraceTagsRemovingAlgorithm;
import algorithms.search.trace.*;
import algorithms.search.trace.locator.coefficient.LastEventCoefficientsTraceLocator;
import algorithms.search.trace.locator.invariant.Node;
import algorithms.search.trace.locator.invariant.TraceInvariantList;
import algorithms.search.trace.locator.invariant.ByFirstTraceCoincidenceInvariantsTraceLocator;
import io.*;
import io.log.ILogReader;
import io.log.ILogWriter;
import io.log.XesLogReader;
import io.log.XesLogWriter;
import javafx.util.Pair;
import org.deckfour.xes.model.XLog;
import parser.WriterFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

public class Main {

    private static final String SOURCE_DIR =
            FileUtils.getCurrentDirectoryPath()
                    + "Sources"
                    + File.separator;

    private static final String FILE_EXTENSION = ".xes";

    private static LogWriter logWriter = new LogWriter();

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            CommandParser.parse(args, logWriter);
        } else {
            launchParsingAlgorithms();
        }
    }

    private static void launchParsingAlgorithms() {
        long startTime = System.currentTimeMillis();

        String srcFileName = "400_traces_of_BPI_Challenge_2013_incidents";
        String srcFilePath = SOURCE_DIR + srcFileName + FILE_EXTENSION;

        try {
            XLog originLog = new XesLogReader().parse(new File(srcFilePath)).get(0);
            new AnalyzeProcessFactory()
                    .get(AnalyzeProcessFactory.AnalyzeProcessVariant.DEFAULT)
                    .launch(new XesLogWriter(), originLog);

//            Remove traces which produces the same product, than put all events into a one trace
//            xLog = new TraceDuplicatesRemovingAlgorithm(logWriter, "product").proceed(originLog);
//            savedLog = logWriter.write(xLog, DESTINATION_DIR + "ParallelProcessesRemoved_", destFileName);


            // Track execution time
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
