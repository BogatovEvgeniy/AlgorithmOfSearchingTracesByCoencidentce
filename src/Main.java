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
        // Config input data

        String srcFilePath = SOURCE_DIR + useCase.getLogName() + FILE_EXTENSION;

        XLog originLog = new XesLogReader().parse(new File(srcFilePath)).get(0);
        AnalyzeProcessAlgorithmsFactory.AlgorithmVariant aDefault = AnalyzeProcessAlgorithmsFactory.AlgorithmVariant.DEFAULT;
        aDefault.setIAttributeSetHolder(useCase);
        aDefault.setIInvariantSetHolder(useCase);
        AnalyzeProcessAlgorithmsFactory
                .get(aDefault)
                .launch(new XesLogWriter(), originLog);

    }
}
