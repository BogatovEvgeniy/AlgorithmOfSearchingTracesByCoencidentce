package algorithms.traceremoval;

import org.deckfour.xes.model.XLog;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ievgen_Bogatov on 09.04.2018.
 */
public interface ITraceRemovingAlgorithm {

    XLog removeTraces();
}
