package exceptions;

import org.deckfour.xes.model.XAttribute;

public class InvariantAlreadyExistsException extends Exception {

    public InvariantAlreadyExistsException(XAttribute attribute) {
        super("Invariant for the \"" + attribute.getKey() + "\" attribute already exists");
    }
}
