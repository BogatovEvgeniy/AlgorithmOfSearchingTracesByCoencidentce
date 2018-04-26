import algorithms.traceremoval.ParallelTraceTagRemovingAlgorithm;
import algorithms.tracesearch.TraceSearchingAlgorithmBasedOnCoefficient;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        File srcFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\400_traces_of_BPI_Challenge_2013_incidents (2).xes");
        File desctFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\BPI_Challenge__log_Ordered_By_TimeEvents_In_Single_Trace1.xes");
        File resultFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\BPI_Challenge__log_AlgorithmImplementation.xes");

//        File srcFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\VolvoLog_3_logs_in_one.xes");
//        File desctFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\VolvoLog_3_logs_in_one_Ordered_By_TimeEvents_In_Single_Trace1.xes");
//        File resultFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\VolvoLog_3_logs_in_one_AlgorithmImplementation.xes");
        new ParallelTraceTagRemovingAlgorithm(srcFile, desctFile).removeTraces();

        new ParallelTraceTagRemovingAlgorithm(srcFile, desctFile).removeTraces();
        new TraceSearchingAlgorithmBasedOnCoefficient(desctFile, resultFile, 0.50f).proceed();
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime));
    }
}
