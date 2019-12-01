import io.FileUtils;
import io.log.XesLogReader;
import io.log.XesLogWriter;
import javafx.util.Pair;
import org.deckfour.xes.model.XLog;
import usecases.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final String SOURCE_DIR = FileUtils.getCurrentDirectoryPath() + "Sources" + File.separator;

    private static final String FILE_EXTENSION = ".xes";

    private static LogWriter logWriter = new LogWriter();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        FewInstances5tracesLogVolvoIncidents fewInstances5tracesLogVolvoIncidents = new FewInstances5tracesLogVolvoIncidents();
        BPIChallenge2013Incidents bpiChallenge2013Incidents = new BPIChallenge2013Incidents();
        KhladopromLogUseCase khladopromLog = new KhladopromLogUseCase();
        Product_production product_production = new Product_production();
        try {
            if (args != null && args.length > 0) {
                CommandParser.parse(args, logWriter);
            } else {
//                launchRemoveDuplicates(product_production);
//                launchParsingAlgorithms(product_production);
//                launchParsingAlgorithms(bpiChallenge2013Incidents);
//                launchParsingAlgorithms(fewInstances5tracesLogVolvoIncidents);
                launchParsingAlgorithms(khladopromLog);
            }

            // Track execution time
            final long endTime = System.currentTimeMillis();
            System.out.println("Total execution time: " + (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void launchRemoveDuplicates(FewInstances5tracesLogVolvoIncidents incidentsUseCase) throws Exception {
        String srcFilePath = SOURCE_DIR + incidentsUseCase.getLogName() + FILE_EXTENSION;
        XLog originLog = new XesLogReader().parse(new File(srcFilePath)).get(0);
        AnalyzeProcessAlgorithmsFactory.AlgorithmVariant algorithmsSet = AnalyzeProcessAlgorithmsFactory.AlgorithmVariant.GET_ONE_PROCESS_TRACES;
        List<Pair<String, String>> duplicateSearchValues = new ArrayList<>();
        duplicateSearchValues.add(new Pair<>(FewInstances5tracesLogVolvoIncidents.KEY_PRODUCT, "PROD542"));
        duplicateSearchValues.add(new Pair<>(FewInstances5tracesLogVolvoIncidents.KEY_PRODUCT, "PROD660"));
        duplicateSearchValues.add(new Pair<>(FewInstances5tracesLogVolvoIncidents.KEY_PRODUCT, "PROD455"));
        algorithmsSet.setDuplicateSearchValues(duplicateSearchValues);
        AnalyzeProcessAlgorithmsFactory
                .get(algorithmsSet)
                .launch(new XesLogWriter(), originLog);
    }

    private static void launchParsingAlgorithms(IUseCase useCase) throws Exception {
//        launchDefaultAlgorithmSetAnalyze(useCase);
//        launchTraceSearchAlgorithmSetAnalyze(useCase);
        launchInvariantAnalyze(useCase);
    }

    private static void launchDefaultAlgorithmSetAnalyze(IUseCase useCase) throws Exception {
        String srcFilePath = SOURCE_DIR + useCase.getLogName() + FILE_EXTENSION;
        XLog originLog = new XesLogReader().parse(new File(srcFilePath)).get(0);
        getDefaultAlgorithmSet(useCase).launch(new XesLogWriter(), originLog);

    }

    private static void launchTraceSearchAlgorithmSetAnalyze(IUseCase useCase) throws Exception {
        String srcFilePath = SOURCE_DIR + useCase.getLogName() + FILE_EXTENSION;
        XLog originLog = new XesLogReader().parse(new File(srcFilePath)).get(0);

        attributeCoincidenceAlgorithm(useCase).launch(new XesLogWriter(), originLog);
    }

    private static void launchInvariantAnalyze(IUseCase useCase) throws Exception {
        String srcFilePath = SOURCE_DIR + useCase.getLogName() + FILE_EXTENSION;
        XLog originLog = new XesLogReader().parse(new File(srcFilePath)).get(0);

        mergeEventsInOneTrace()
                .append(invariantAlgorithm(useCase))
                .launch(new XesLogWriter(), originLog);
    }

    private static AlgorithmSequence getDefaultAlgorithmSet(IUseCase useCase) {
        AnalyzeProcessAlgorithmsFactory.AlgorithmVariant algorithmsSet =
                AnalyzeProcessAlgorithmsFactory.AlgorithmVariant.DEFAULT;
        algorithmsSet.setIAttributeSetHolder(useCase);

        return AnalyzeProcessAlgorithmsFactory.get(algorithmsSet);
    }

    private static AlgorithmSequence mergeEventsInOneTrace() {
        return AnalyzeProcessAlgorithmsFactory.get(AnalyzeProcessAlgorithmsFactory.AlgorithmVariant.MERGE_ALL_EVENTS_IN_ONE_TRACE);
    }

    private static AlgorithmSequence attributeCoincidenceAlgorithm(IUseCase useCase) {
        AnalyzeProcessAlgorithmsFactory.AlgorithmVariant algorithmVariant =
                AnalyzeProcessAlgorithmsFactory.AlgorithmVariant.TRACE_SEARCH_ATTRIBUTE_COMPARISION_BASED;

        algorithmVariant.setICoefficientMapCalculator(useCase);
        return AnalyzeProcessAlgorithmsFactory.get(algorithmVariant);
    }

    private static AlgorithmSequence invariantAlgorithm(IUseCase useCase) {
        AnalyzeProcessAlgorithmsFactory.AlgorithmVariant traceSearchInvariantBased =
                AnalyzeProcessAlgorithmsFactory.AlgorithmVariant.TRACE_SEARCH_INVARIANT_BASED;

        traceSearchInvariantBased.setIInvariantSetHolder(useCase);
        return AnalyzeProcessAlgorithmsFactory.get(traceSearchInvariantBased);
    }
}
