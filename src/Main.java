import algorithms.removal.MergeEventsInOneTraceAndTraceTagsRemovingAlgorithm;
import algorithms.removal.TraceDuplicatesRemovingAlgorithm;
import algorithms.search.trace.AttributeWeightsSearchAlgorithm;
import algorithms.search.trace.TraceSearchingAlgorithm;
import algorithms.search.trace.ITraceSearchingAlgorithm;
import algorithms.search.trace.locator.invariant.Node;
import algorithms.search.trace.locator.invariant.TraceInvariantList;
import algorithms.search.trace.locator.invariant.ByFirstTraceCoincidenceInvariantsTraceLocator;
import io.*;
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
        String destFileName = "BPI_Challenge_log";
        String srcFilePath = DESTINATION_DIR + srcFileName + FILE_EXTENSION;

        try {
            ILogReader logReader = new XesLogReader();
            ILogWriter logWriter = new XesLogWriter();
            XLog originLog = logReader.parse(new File(srcFilePath)).get(0);

            // Remove traces which produces the same product, than put all events into a one trace
//            XLog xLog = new TraceDuplicatesRemovingAlgorithm(logWriter, "product").proceed(originLog);
//            File savedLog = logWriter.write(xLog, DESTINATION_DIR + "ParallelProcessesRemoved_", destFileName);
            XLog xLog = new MergeEventsInOneTraceAndTraceTagsRemovingAlgorithm().proceed(originLog);
            // Search for attributes weights
            AttributeWeightsSearchAlgorithm attrWeightSearchAlgorithm = initAttributeWeightsSearchAlgorithm();
            List<Map<String, Float>> weightsValues = attrWeightSearchAlgorithm.proceed(xLog);
            System.out.println("Weights were defined: " + weightsValues.toString());

            /*// Build an map which will reflect an majority of each attribute for future analyse
            ITraceSearchingAlgorithm searchingAlgorithm = initTraceSearchingAlgorithm(destFileName, logWriter, xLog);

            xLog = searchingAlgorithm.proceed(xLog);
            logWriter.write(xLog, DESTINATION_DIR + "TracesRestored_", destFileName);
*/
            // Track execution time
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static AttributeWeightsSearchAlgorithm initAttributeWeightsSearchAlgorithm() {
        Set<Pair<Integer, Integer>> rangeSet = new HashSet<>();
        rangeSet.add(new Pair<>(100, 1000));
        rangeSet.add(new Pair<>(2000, 3500));
        rangeSet.add(new Pair<>(3000, 4500));
        rangeSet.add(new Pair<>(4000, 5400));
        rangeSet.add(new Pair<>(6400, 7400));

        List<List<String>> attributeSets = new LinkedList<>();
//        attributeSets.add(Arrays.asList("org:group", "org:resource"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "organization involved"));
//        attributeSets.add(Arrays.asList("org:group","org:resource","product"));
        attributeSets.add(Arrays.asList("org:resource","product"));
//        attributeSets.add(Arrays.asList("org:group","org:role","product"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "organization involved","org:role"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "organization involved","org:role","product"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource", "organization involved","org:role","product"));
//        attributeSets.add(Arrays.asList("org:group", "org:resource","org:role","product"));
        return new AttributeWeightsSearchAlgorithm(5, 3, 0.5f, rangeSet, attributeSets);
    }

    private static ITraceSearchingAlgorithm initTraceSearchingAlgorithm(String destFileName, ILogWriter logWriter, XLog xLog) throws IOException {
        // Launch the algorithm of searching traces by coincidences of event's attributes values
        // also tacking in a count coefficientMap
        TraceSearchingAlgorithm searchingAlgorithm = new TraceSearchingAlgorithm();

        // Define locators
        Map<String, Float> correctionMap = calculateCoefficientsMap(xLog);
//        searchingAlgorithm.setTraceLocator(new LastEventCoefficientsTraceLocator(0.7f, correctionMap));
        ITraceSearchingAlgorithm.TraceLocator invariantTraceLocator = initInvariantTraceLocator(xLog);
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
        correctionMap.put(KEY_PRODUCT, 4f);
        correctionMap.put(KEY_ORG_RESOURCE, 0.5f);
        correctionMap.put(KEY_ORG_GROUP, 0.5f);

        Map<String, Float> stringFloatMap = new CoefficientMapBuilder(xLog, correctionMap).build();
        return stringFloatMap;
    }
}
