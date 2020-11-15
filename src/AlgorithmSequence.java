import algorithms.ILogAlgorithm;
import io.log.ILogWriter;
import org.deckfour.xes.model.XLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlgorithmSequence {

    private final List<ILogAlgorithm<?>> algorithms;

    AlgorithmSequence (List<ILogAlgorithm<?>> algorithms){
        this.algorithms = algorithms;
    }

    AlgorithmSequence (ILogAlgorithm<?> ... algorithmVararg){
        this.algorithms = new ArrayList<>();
        algorithms.addAll(Arrays.asList(algorithmVararg));
    }

    public void launch(ILogWriter logWriter, XLog xLog){
        XLog currentLog = xLog;
        for (ILogAlgorithm<?> algorithm : algorithms) {
            Object result = algorithm.proceed(currentLog);
            if (result instanceof XLog) {
                currentLog = (XLog) result;
            }

            logWriter.write(currentLog, algorithm.getResultsName() + "_Results");
        }
    }

    public AlgorithmSequence append(ILogAlgorithm algorithm) {
        algorithms.add(algorithm);
        return this;
    }

    public AlgorithmSequence append(List<ILogAlgorithm> algorithms) {
        algorithms.addAll(algorithms);
        return this;
    }

    public AlgorithmSequence append(AlgorithmSequence sequence) {
        algorithms.addAll(sequence.getAlgorithms());
        return this;
    }

    private List<? extends ILogAlgorithm<?>> getAlgorithms() {
        return algorithms;
    }
}
