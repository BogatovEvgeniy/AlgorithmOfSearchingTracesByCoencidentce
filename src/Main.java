import algorithms.TraceRemovingAlgorithmImpl;
import algorithms.TraceSearchingAlgorithmImpl;

import java.io.*;

/**
 * Created by Ievgen_Bogatov on 04.12.2017.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        File srcFile = new File("D:\\BPI_Challenge_2013_incidents.xes");
        File desctFile = new File("D:\\BPI_Challenge_2013_Ordered_By_TimeEvents_In_Single_Trace.xes");
        File resultFile = new File("D:\\BPI_Challenge_2013_AlgorithmImplementation.xes");
        new TraceRemovingAlgorithmImpl(srcFile, desctFile).removeTraces();
        new TraceSearchingAlgorithmImpl(desctFile, resultFile, 3).proceed();
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime));
    }
}
