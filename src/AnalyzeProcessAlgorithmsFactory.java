import algorithms.ILogAlgorithm;
import algorithms.filter.MergeEventsInOneTrace;
import algorithms.filter.duplicates.GetTraceDuplicates;
import algorithms.search.trace.PredefinedAttributeWeightsSearchAlgorithm;
import algorithms.search.trace.TraceSearchingAlgorithm;
import com.google.common.collect.Lists;
import javafx.util.Pair;
import usecases.IAttributeSetHolder;
import usecases.ICoefficientMapCalculator;
import usecases.IInvariantSetHolder;

import java.util.ArrayList;
import java.util.List;

public class AnalyzeProcessAlgorithmsFactory {

    public static AlgorithmSequence get(AlgorithmVariant variant) {
        List<ILogAlgorithm<?>> algorithms = new ArrayList<>();
        switch (variant) {
            case MERGE_ALL_EVENTS_IN_ONE_TRACE:
                algorithms.add(new MergeEventsInOneTrace());
                break;
            case GET_ONE_PROCESS_TRACES:
                algorithms.add(new GetTraceDuplicates(variant.searchDuplicatesValues));
                break;
            case PREDEFINED_ATTRIBUTE_WEIGHT_SEARCH_ALGORITHM:
                algorithms.add(PredefinedAttributeWeightsSearchAlgorithm.init(variant.algorithmModel.getAttributeSets()));
                break;
            case TRACE_SEARCH_ATTRIBUTE_COMPARISION_BASED:
                algorithms.add(TraceSearchingAlgorithm.initAlgorithmBasedOnAttributeComparision(variant.coeficientMapCalculator.calculateCoefficientsMap()));
                break;
            case TRACE_SEARCH_INVARIANT_BASED:
                algorithms.add(TraceSearchingAlgorithm.initAlgorithmBasedOnInvariantComparision(variant.invariantSetHolder.getInvariants()));
                break;
            default:
                algorithms =
                        Lists.newArrayList(
                                new MergeEventsInOneTrace(),
                                PredefinedAttributeWeightsSearchAlgorithm.init(variant.algorithmModel.getAttributeSets()),
                                TraceSearchingAlgorithm.initAlgorithmBasedOnAttributeComparision(variant.coeficientMapCalculator.calculateCoefficientsMap()),
                                TraceSearchingAlgorithm.initAlgorithmBasedOnInvariantComparision(variant.invariantSetHolder.getInvariants())
                        );
        }
        return new AlgorithmSequence(algorithms);
    }

    public enum AlgorithmVariant {
        DEFAULT,
        MERGE_ALL_EVENTS_IN_ONE_TRACE,
        GET_ONE_PROCESS_TRACES,
        PREDEFINED_ATTRIBUTE_WEIGHT_SEARCH_ALGORITHM,
        TRACE_SEARCH_ATTRIBUTE_COMPARISION_BASED,
        TRACE_SEARCH_INVARIANT_BASED;

        private IAttributeSetHolder algorithmModel;
        private IInvariantSetHolder invariantSetHolder;
        private ICoefficientMapCalculator coeficientMapCalculator;
        private List<Pair<String, String>> searchDuplicatesValues;

        void setIAttributeSetHolder(IAttributeSetHolder iAlgorithmModel) {
            this.algorithmModel = iAlgorithmModel;
        }

        void setIInvariantSetHolder(IInvariantSetHolder iInvariantSetHolder) {
            this.invariantSetHolder = iInvariantSetHolder;
        }

        void setICoefficientMapCalculator(ICoefficientMapCalculator coeficientMapCalculator) {
            this.coeficientMapCalculator = coeficientMapCalculator;
        }

        void setDuplicateSearchValues(List<Pair<String, String>> keys) {
            searchDuplicatesValues = keys;
        }
    }
}
