import algorithms.traceremoval.ParallelTraceTagRemovingAlgorithm;
import algorithms.TraceSearchingAlgorithmBasedOnCoefficient;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        File srcFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\400_traces_of_BPI_Challenge_2013_incidents (2).xes");
        File desctFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\BPI_Challenge__log_Ordered_By_TimeEvents_In_Single_Trace1.xes");
        File resultFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\BPI_Challenge__log_AlgorithmImplementation.xes");
        new ParallelTraceTagRemovingAlgorithm(srcFile, desctFile).removeTraces();
        new TraceSearchingAlgorithmBasedOnCoefficient(desctFile, resultFile, 2).proceed();
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime));
    }
}
