package algorithms;

import org.deckfour.xes.model.XLog;

public interface ILogAlgorithm<T> {

    T proceed(XLog originLog);
}
