package algorithms.search.invariant;

import org.junit.Test;

import static org.junit.Assert.*;

public class InvariantsTraceLocatorTest {

    @Test
    public void getId() {
        assertTrue(new InvariantsTraceLocator(new TraceInvariantList()).getId().equals(InvariantsTraceLocator.class.getSimpleName()));
    }

    @Test
    public void defineSuitableTracesList() {

    }

    @Test
    public void getLogValidator() {
    }
}