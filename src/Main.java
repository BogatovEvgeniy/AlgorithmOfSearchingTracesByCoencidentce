import algorithms.traceremoval.ParallelTraceTagRemovingAlgorithm;
import algorithms.traceremoval.TraceTagRemovingAlgorithm;
import algorithms.tracesearch.TraceSearchingAlgorithmBasedOnCoefficient;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        File srcFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\400_traces_of_BPI_Challenge_2013_incidents.xes");
        File desctFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\BPI_Challenge__log_Ordered_By_TimeEvents_In_Single_Trace1.xes");
        File resultFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\BPI_Challenge__log_AlgorithmImplementation.xes");

        new ParallelTraceTagRemovingAlgorithm(srcFile, desctFile).removeTraces();
        new TraceTagRemovingAlgorithm(srcFile, desctFile).removeTraces();

        Map<String, Float> correctionMap = new HashMap<>();
        correctionMap.put("org:group", 0.5f);

        new TraceSearchingAlgorithmBasedOnCoefficient(desctFile, resultFile, correctionMap, 0.7f).proceed();
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime));
    }
}
