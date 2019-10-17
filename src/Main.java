import io.*;
import io.log.XesLogReader;
import io.log.XesLogWriter;
import org.deckfour.xes.model.XLog;
import usecases.ForHundredLog;
import usecases.IUseCase;
import usecases.KhladopromLogUseCase;

import java.io.File;

public class Main {

    private static final String SOURCE_DIR = FileUtils.getCurrentDirectoryPath() + "Sources" + File.separator;

    private static final String FILE_EXTENSION = ".xes";

    private static LogWriter logWriter = new LogWriter();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        ForHundredLog forHoundredsLog = new ForHundredLog();
        KhladopromLogUseCase khladopromLog = new KhladopromLogUseCase();
        try {
            if (args != null && args.length > 0) {
                CommandParser.parse(args, logWriter);
            } else {
//                launchParsingAlgorithms(forHoundredsLog);
                launchParsingAlgorithms(khladopromLog);
            }

            // Track execution time
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void launchParsingAlgorithms(IUseCase useCase) throws Exception {
        launchDefaultAlgorithmSetAnalyze(useCase);
        launchTraceSearchAlgorithmSetAnalyze(useCase);
        launchInvariantAnalyze(useCase);
    }

    private static void launchDefaultAlgorithmSetAnalyze(IUseCase useCase) throws Exception {
        String srcFilePath = SOURCE_DIR + useCase.getLogName() + FILE_EXTENSION;
        XLog originLog = new XesLogReader().parse(new File(srcFilePath)).get(0);
        AnalyzeProcessAlgorithmsFactory.AlgorithmVariant algorithmsSet = AnalyzeProcessAlgorithmsFactory.AlgorithmVariant.DEFAULT;
        algorithmsSet.setIAttributeSetHolder(useCase);
        AnalyzeProcessAlgorithmsFactory
                .get(algorithmsSet)
                .launch(new XesLogWriter(), originLog);

    }

    private static void launchTraceSearchAlgorithmSetAnalyze(IUseCase useCase) throws Exception {
        String srcFilePath = SOURCE_DIR + useCase.getLogName() + FILE_EXTENSION;
        XLog originLog = new XesLogReader().parse(new File(srcFilePath)).get(0);
        AnalyzeProcessAlgorithmsFactory.AlgorithmVariant algorithmsSet = AnalyzeProcessAlgorithmsFactory.AlgorithmVariant.TRACE_SEARCH_ATTRIBUTE_COMPARISION_BASED;
        algorithmsSet.setICoefficientMapCalculator(useCase);
        AnalyzeProcessAlgorithmsFactory
                .get(algorithmsSet)
                .launch(new XesLogWriter(), originLog);
    }

    private static void launchInvariantAnalyze(IUseCase useCase) throws Exception {
        String srcFilePath = SOURCE_DIR + useCase.getLogName() + FILE_EXTENSION;
        XLog originLog = new XesLogReader().parse(new File(srcFilePath)).get(0);
        AlgorithmSequence mergeAlgorithm = AnalyzeProcessAlgorithmsFactory.get(AnalyzeProcessAlgorithmsFactory.AlgorithmVariant.MERGE_ALL_EVENTS_IN_ONE_TRACE);
        AnalyzeProcessAlgorithmsFactory.AlgorithmVariant traceSearchInvariantBased = AnalyzeProcessAlgorithmsFactory.AlgorithmVariant.TRACE_SEARCH_INVARIANT_BASED;
        traceSearchInvariantBased.setIInvariantSetHolder(useCase);
        AlgorithmSequence invariantAlgorithm = AnalyzeProcessAlgorithmsFactory.get(traceSearchInvariantBased);
        mergeAlgorithm.append(invariantAlgorithm).launch(new XesLogWriter(), originLog);
    }
}
