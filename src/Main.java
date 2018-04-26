import algorithms.TraceRemovingAlgorithmImpl;
import algorithms.TraceSearchingAlgorithmImpl;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        File srcFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\400_traces_of_BPI_Challenge_2013_incidents (2).xes");
        File desctFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\BPI_Challenge__log_Ordered_By_TimeEvents_In_Single_Trace.xes");
        File resultFile = new File("C:\\Users\\ievgen_bogatov\\Desktop\\BPI_Challenge__log_AlgorithmImplementation.xes");
        new TraceRemovingAlgorithmImpl(srcFile, desctFile).removeTraces();
        new TraceSearchingAlgorithmImpl(desctFile, resultFile, 7).proceed();
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime));
    }
}
