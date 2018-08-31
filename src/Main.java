import algorithms.preprocess.InvariantInitialEventSearchAlgorithm;
import algorithms.removal.TraceDuplicatesRemovingAlgorithm;
import algorithms.search.TraceSearchingAlgorithm;
import algorithms.search.invariant.AttributeInvariantTree;
import algorithms.search.invariant.InvariantsTraceLocator;
import io.*;
import org.deckfour.xes.model.XLog;
import parser.WriterFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String SOURCE_DIR = "Sources" + File.separator;
    private static final String DESTINATION_DIR = FileUtils.getCurrentDirectoryPath() + SOURCE_DIR;
    private static final String FILE_EXTENSION = ".xes";
    private static final int COMMAND_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int LOG_PATH_INDEX = 2;
    private static LogWriter logWriter = new LogWriter();

    public static void main(String[] args) {
        if (args != null && args.length > 0) {
            parseArguments(args);
        } else {
            new File(DESTINATION_DIR).mkdirs();
            launchParsingAlgorithms();
        }
    }

    private static boolean parseArguments(String[] args) {
        switch (args[COMMAND_INDEX]) {
            case "-saveAs":
                saveAs(args);
            default:
                return false;
        }
    }

    private static Boolean saveAs(String[] args) {
        try {
            String logFilePath = args[LOG_PATH_INDEX];
            File file = new File(logFilePath);
            List<XLog> parsedLog = new XesLogReader().parse(file);
            if (parsedLog != null & parsedLog.size() > 0) {
                String currentDirPath = System.getProperty("user.dir") + File.separator;
                WriterFactory.parserFor(args[VALUE_INDEX]).write(parsedLog.get(0), currentDirPath, file.getName());
                return true;
            } else {
                logWriter.write("Unable to parse log");
                return false;
            }
        } catch (IndexOutOfBoundsException e) {
            logWriter.write("Some of arguments missed. Arguments received:" + Arrays.toString(args));
            logWriter.write("Input format should be the next: <command>[-saveAs] <Save as type>[\"OpenXEStoCSV\"] <Path to log file>. Arguments received:" + Arrays.toString(args));
        } catch (Exception e) {
            logWriter.write("Something goes wrong. Arguments received:" + Arrays.toString(args));
            logWriter.write(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    private static void launchParsingAlgorithms() {
        long startTime = System.currentTimeMillis();

        String srcFileName = "400_traces_of_BPI_Challenge_2013_incidents";
        String destFileName = "BPI_Challenge_log";
        String srcFilePath = DESTINATION_DIR + srcFileName + FILE_EXTENSION;

        try {
            ILogReader logReader = new XesLogReader();
            ILogWriter logWriter = new XesLogWriter();
            XLog originLog = logReader.parse(new File(srcFilePath)).get(0);
            AttributeInvariantTree<String> invariantTree = getInvariants();

            // Remove traces which produces the same product, than put all events into a one trace
            XLog xLog = new TraceDuplicatesRemovingAlgorithm(logWriter, "product").proceed(originLog);
            File savedLog = logWriter.write(xLog, DESTINATION_DIR + "ParallelProcessesRemoved_", destFileName);

            // Define first suitable for flowing analyze event
            xLog = new InvariantInitialEventSearchAlgorithm(invariantTree).proceed(xLog);

            // Build an map which will reflect an majority of each attribute for future analyse
            Map<String, Float> correctionMap = calculateCoefficientsMap(savedLog);

            // Launch the algorithm of searching traces by coincidences of event's attributes values
            // also tacking in a count coefficientMap
            TraceSearchingAlgorithm searchingAlgorithm = new TraceSearchingAlgorithm();
//        searchingAlgorithm.setTraceLocator(new LastEventCoefficientsTraceLocator(0.7f, correctionMap));
//        List<AttributeInvariantTree> attributeInvariantTrees = new LinkedList<>();
            searchingAlgorithm.setTraceLocator(new InvariantsTraceLocator(xLog, invariantTree));
            xLog = searchingAlgorithm.proceed(xLog);
            logWriter.write(xLog, DESTINATION_DIR + "TracesRestored_", destFileName);

            // Track execution time
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static AttributeInvariantTree<String> getInvariants() {
        return null;
    }


    private static Map<String, Float> calculateCoefficientsMap(File srcFile) {
        Map<String, Float> correctionMap = new HashMap<>();
        correctionMap.put("product", 4f);
        correctionMap.put("org:resource", 0.5f);
        correctionMap.put("org:group", 0.5f);

        Map<String, Float> stringFloatMap = new CoefficientMapBuilder(srcFile, correctionMap).build();
        return stringFloatMap;
    }
}
