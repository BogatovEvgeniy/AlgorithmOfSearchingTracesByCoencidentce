package algorithms.search.locators.invariant;

import algorithms.search.ITraceSearchingAlgorithm;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import java.util.ArrayList;
import java.util.List;

public class InvariantsTraceLocator implements ITraceSearchingAlgorithm.TraceLocator {

    private List<AttributeInvariant> attributeInvariants = new ArrayList<>();

    public InvariantsTraceLocator(List<AttributeInvariant> attributeInvariants) {
        this.attributeInvariants = attributeInvariants;
    }

    @Override
    public String getId() {
        return getClass().getSimpleName();
    }

    @Override
    public int[] defineSuitableTracesList(XLog xLog, XEvent event) {
        for (XTrace trace : xLog){
            for(XAttribute xAttribute : event.getAttributes().values()){
                for (int i = 0; i < attributeInvariants.size(); i++) {
                    if (attributeInvariants.get(i).equals(xAttribute)){
                        AttributeInvariant.InvariantIterator iterator = attributeInvariants.get(i).getIterator();
                        if (iterator.hasNext()) {
                            String next = (String) iterator.next();
                            if (true){
                                //compare attribute of event with  one of next values for current node is suitable for
                                // if there is suitable invariant, then set suitable trace
                                // count how watch attributes of last events of each trace coincident for current event
                            }
                        }
                    }
                }
            }
        }

        return new int[0];
    }
}
