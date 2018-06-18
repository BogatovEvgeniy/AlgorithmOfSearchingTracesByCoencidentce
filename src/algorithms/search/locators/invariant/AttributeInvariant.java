package algorithms.search.locators.invariant;

import org.deckfour.xes.model.XAttribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AttributeInvariant {
    XAttribute xAttribute;

    // TODO here we need a tree structure
    List<String> invariants = new ArrayList<String>();

    public void getValueOfNode (String currentValue) {
    }

    public void addValueForNode(String value) {
    }

    public  InvariantIterator getIterator(){
        return InvariantIterator(/*Invariants tree*/)
    }

    class InvariantIterator implements Iterator {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }

        @Override
        public void remove() {

        }
    }
}
