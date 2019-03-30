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
import java.util.*;

public class Main {

    private static final String SOURCE_DIR = "Sources" + File.separator;
    private static final String DESTINATION_DIR = FileUtils.getCurrentDirectoryPath() + SOURCE_DIR;
    private static final String FILE_EXTENSION = ".xes";
    private static final int COMMAND_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int LOG_PATH_INDEX = 2;
    public static final String KEY_PRODUCT = "product";
    public static final String KEY_ORG_RESOURCE = "org:resource";
    public static final String KEY_ORG_ROLE = "org:role";
    public static final String KEY_ORG_GROUP = "org:group";
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
        String destFileName = "400_traces";
        String srcFilePath = DESTINATION_DIR + srcFileName + FILE_EXTENSION;

        try {
            ILogReader logReader = new XesLogReader();
            ILogWriter logWriter = new XesLogWriter();
            XLog originLog = logReader.parse(new File(srcFilePath)).get(0);
            XLog xLog = null;
            File savedLog = null;

//             Remove traces which produces the same product, than put all events into a one trace
//            xLog = new TraceDuplicatesRemovingAlgorithm(logWriter, "product").proceed(originLog);
//            savedLog = logWriter.write(xLog, DESTINATION_DIR + "ParallelProcessesRemoved_", destFileName);

            //---------------------------- MERGE ALL EVENTS IN ONE TRACE -------------------------------- //
            xLog = new MergeEventsInOneTraceAndTraceTagsRemovingAlgorithm().proceed(originLog);
            logWriter.write(xLog, DESTINATION_DIR + "ParallelProcessesRemoved_", destFileName);

            //----------------------------------- WEIGHTS SEARCH -----------------------------------------//
//             Search for attributes weights
            PredefinedAttributeWeightsSearchAlgorithm attrWeightSearchAlgorithm = initAttributeWeightsSearchAlgorithm();
            List<AttributeSetWeightPerRanges> weightsValues = attrWeightSearchAlgorithm.proceed(xLog);
            for (AttributeSetWeightPerRanges attributeSetWeightPerRanges : weightsValues) {
                System.out.println(attributeSetWeightPerRanges);
            }

            //------------------------------- SEARCH ALGORITHMS ------------------------------------------//
            //--------------------------- WEIGHTS BASED SEARCH ALGORITHM ---------------------------------//
//            Map<String, Float> correctionMap = calculateCoefficientsMap(xLog);
//            ITraceSearchingAlgorithm.TraceLocator traceLocator = new LastEventCoefficientsTraceLocator(0.6f, correctionMap);
//            ITraceSearchingAlgorithm iTraceSearchingAlgorithm = initInvariantTraceSearchingAlgorithm(destFileName, logWriter, xLog, traceLocator);
//            xLog = iTraceSearchingAlgorithm.proceed(xLog);
//            savedLog = logWriter.write(xLog, DESTINATION_DIR + "TracesRestored_", destFileName);

//            //-------------------------- INVARIANT BASED SEARCH ALGORITHM --------------------------------//
//            // Build an map which will reflect an majority of each attribute for future analyse
//            ITraceSearchingAlgorithm.TraceLocator invariantTraceLocator = initInvariantTraceLocator(xLog);
//            ITraceSearchingAlgorithm iTraceSearchingAlgorithm1 = initInvariantTraceSearchingAlgorithm(destFileName, logWriter, xLog, invariantTraceLocator);
//            xLog = iTraceSearchingAlgorithm1.proceed(xLog);
//            logWriter.write(xLog, DESTINATION_DIR + "TracesRestored_", destFileName);

            // Track execution time
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PredefinedAttributeWeightsSearchAlgorithm initAttributeWeightsSearchAlgorithm() {
        List<List<String>> attributeSets = new LinkedList<>();
        initRangesFor400TracesLog();
        initAttributeSetsFor400TraceLog(attributeSets);
        return new PredefinedAttributeWeightsSearchAlgorithm(3,
                PredefinedAttributeWeightsSearchAlgorithm.FAIL_COUNT_UNLIMITED,
                0.0f,
                attributeSets);
    }

    private static void initAttributeSetsFor400TraceLog(List<List<String>> attributeSets) {
//        attributeSets.add(Arrays.asList("product"));
//        attributeSets.add(Arrays.asList("org:group"));
//        attributeSets.add(Arrays.asList("org:resource"));
//        attributeSets.add(Arrays.asList("organization involved"));
//        attributeSets.add(Arrays.asList("org:role"));
//        attributeSets.add(Arrays.asList("org:resource", "product"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource"));
//        attributeSets.add(Arrays.asList("org:group", "org:role", "product"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "product"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "organization involved"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "org:role", "product"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "organization involved", "org:role"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "organization involved", "org:role", "product"));
        attributeSets.add(Arrays.asList("org:role", "org:resource"));
        attributeSets.add(Arrays.asList("org:group", "org:resource", "org:role"));
    }

    private static Set<Pair<Integer, Integer>> initRangesFor400TracesLog() {
        Set<Pair<Integer, Integer>> rangeSet = new HashSet<>();
        rangeSet.add(new Pair<>(100, 1000));
        rangeSet.add(new Pair<>(2000, 3500));
        rangeSet.add(new Pair<>(3000, 4500));
        rangeSet.add(new Pair<>(4000, 5400));
        rangeSet.add(new Pair<>(6400, 7400));
        return rangeSet;
    }

    private static ITraceSearchingAlgorithm initInvariantTraceSearchingAlgorithm(String destFileName,
                                                                                 ILogWriter logWriter,
                                                                                 XLog xLog,
                                                                                 ITraceSearchingAlgorithm.TraceLocator invariantTraceLocator) throws IOException {
        // Launch the algorithm of searching traces by coincidences of event's attributes values
        // also tacking in a count coefficientMap
        TraceSearchingAlgorithm searchingAlgorithm = new TraceSearchingAlgorithm();

        // Define locators
        searchingAlgorithm.setTraceLocator(invariantTraceLocator);
        return searchingAlgorithm;
    }

    private static ITraceSearchingAlgorithm.TraceLocator initInvariantTraceLocator(XLog xLog) {
        TraceInvariantList invariantTree = getInvariants();
        // Define first suitable for flowing analyze event
//        xLog = new InvariantInitialEventSearchAlgorithm(invariantTree).proceed(xLog);
        return new ByFirstTraceCoincidenceInvariantsTraceLocator(0.0f, invariantTree);
    }

    private static TraceInvariantList getInvariants() {
        TraceInvariantList list = new TraceInvariantList();

        Node product = new Node(KEY_PRODUCT);
        product.addInvariant(Arrays.asList(new String[]{"PROD582"}));

        Node resource = new Node(KEY_ORG_RESOURCE);
        Node orgGroup = new Node(KEY_ORG_GROUP);
        return list;

    }


    private static Map<String, Float> calculateCoefficientsMap(XLog xLog) {
        Map<String, Float> correctionMap = new HashMap<>();
        correctionMap.put(KEY_PRODUCT, 0.520899941f);
        correctionMap.put(KEY_ORG_RESOURCE, 0.159700019f);
        correctionMap.put(KEY_ORG_GROUP, 0.319400038f);

        Map<String, Float> stringFloatMap = new CoefficientMapBuilder(xLog, correctionMap, false).build();
        return stringFloatMap;
    }
}
