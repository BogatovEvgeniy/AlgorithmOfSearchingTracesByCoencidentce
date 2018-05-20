package algorithms;

import exceptions.LogParsingError;
import org.deckfour.xes.model.XLog;

/**
 * Created by Ievgen_Bogatov on 09.04.2018.
 */
public interface ITraceSearchingAlgorithm {

    XLog proceed() throws LogParsingError;
}
