package exceptions;

public class InvariantAlreadyExistsException extends Exception {

    public InvariantAlreadyExistsException(String key) {
        super("Invariant for the \"" + key + "\" attribute already exists");
    }
}
