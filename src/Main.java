import algorithms.traceremoval.ParallelTraceTagRemovingAlgorithm;
import algorithms.tracesearch.coefficient.TraceSearchingAlgorithm;
import exceptions.LogParsingError;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XesXmlSerializer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static final String DESTINATION_DIR = "C:\\Users\\ievgen_bogatov\\Desktop\\";
    public static final String FILE_EXTENSION = ".xes";

    public static void main(String[] args) throws IOException, LogParsingError {
        long startTime = System.currentTimeMillis();
        String srcFileName = "400_traces_of_BPI_Challenge_2013_incidents";
        String destFileName ="BPI_Challenge_log";
        String srcFilePath = DESTINATION_DIR + srcFileName + FILE_EXTENSION;

        // Remove traces which produces the same product, than put all events into a one trace
        XLog xLog = new ParallelTraceTagRemovingAlgorithm(new File(srcFilePath), "product").removeTraces();
        File savedLog = saveLog(xLog, DESTINATION_DIR, "ParallelProcessesRemoved_", destFileName, FILE_EXTENSION);

        // Build an map which will reflect an majority of each attribute for future analyse
        Map<String, Float> correctionMap = calculateCoefficientsMap(savedLog);

        // Launch the algorithm of searching traces by coincidences of event's attributes values
        // also tacking in a count coefficientMap
        xLog = new TraceSearchingAlgorithm(savedLog, correctionMap, 0.7f).proceed();
        saveLog(xLog, DESTINATION_DIR, "TracesRestored_", destFileName, FILE_EXTENSION);

        // Track execution time
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime));
    }

    private static File saveLog(XLog xLog, String destDir, String prefix, String destFile, String fileExtension) {
        File file = new File(destDir + prefix + destFile + fileExtension);
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            new XesXmlSerializer().serialize(xLog, new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
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
