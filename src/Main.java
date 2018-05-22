import io.ILogReader;
import io.ILogWriter;
import io.XesLogReader;
import io.XesLogWriter;
import algorithms.removal.ParallelTraceTagRemovingAlgorithm;
import algorithms.search.TraceSearchingAlgorithm;
import algorithms.search.locators.coefficient.CoefficientsTraceLocator;
import org.deckfour.xes.model.XLog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static final String DESTINATION_DIR = "C:\\Users\\ievgen_bogatov\\Desktop\\";
    public static final String FILE_EXTENSION = ".xes";

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        String srcFileName = "400_traces_of_BPI_Challenge_2013_incidents";
        String destFileName = "BPI_Challenge_log";
        String srcFilePath = DESTINATION_DIR + srcFileName + FILE_EXTENSION;

        ILogReader logReader = new XesLogReader();
        ILogWriter logWriter = new XesLogWriter();
        XLog originLog = logReader.parse(new File(srcFilePath)).get(0);

        // Remove traces which produces the same product, than put all events into a one trace
        XLog xLog = new ParallelTraceTagRemovingAlgorithm(logWriter, "product").proceed(originLog);
        File savedLog = logWriter.write(xLog, DESTINATION_DIR + "ParallelProcessesRemoved_", destFileName);

        // Build an map which will reflect an majority of each attribute for future analyse
        Map<String, Float> correctionMap = calculateCoefficientsMap(savedLog);

        // Launch the algorithm of searching traces by coincidences of event's attributes values
        // also tacking in a count coefficientMap
        TraceSearchingAlgorithm searchingAlgorithm = new TraceSearchingAlgorithm();
        searchingAlgorithm.setTraceLocator(new CoefficientsTraceLocator(0.7f, correctionMap));
        xLog = searchingAlgorithm.proceed(xLog);
        logWriter.write(xLog, DESTINATION_DIR + "TracesRestored_", destFileName);

        // Track execution time
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime));
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
