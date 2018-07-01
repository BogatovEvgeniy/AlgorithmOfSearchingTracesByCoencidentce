package algorithms.preprocess;

import algorithms.ILogAlgorithm;
import algorithms.search.invariant.AttributeInvariantTree;
import org.deckfour.xes.model.*;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;

import java.util.Iterator;
import java.util.Set;

public class InvariantInitialEventSearchAlgorithm implements ILogAlgorithm {
    private AttributeInvariantTree<String> invariantTree;

    public InvariantInitialEventSearchAlgorithm(AttributeInvariantTree<String> invariantTree) {
        this.invariantTree = invariantTree;
    }

    @Override
    public XLog proceed(XLog origin) {
        XLog result = new XLogImpl(origin.getAttributes());
        for (XTrace trace : origin) {
            for (XEvent xEvent : trace) {
                XAttributeMap attributes = xEvent.getAttributes();
                for (String key : attributes.keySet()) {
                    Set<AttributeInvariantTree.TreeNode<XAttribute, String>> invariantsForKey = invariantTree.getInvariantsForKey(attributes.get(key));
                    Iterator<AttributeInvariantTree.TreeNode<XAttribute, String>> iterator = invariantsForKey.iterator();
                    while (iterator.hasNext()) {
                        if (iterator.next().getValues().get(0).equals(attributes.get(key).toString())) {
                            XTrace validTrace = new XTraceImpl(trace.getAttributes());
                            validTrace.add(xEvent);
                            result.add(trace);
                            break;
                        }
                    }
                    if (!result.isEmpty()) {
                        break;
                    }
                }
                if (!result.isEmpty()) {
                    result.get(result.size()-1).add(xEvent);
                }
            }
            if (!result.isEmpty()) {
                result.add(trace);
            }
        }
        return result;
    }
}