package exceptions;

public class InvariantAlreadyExistsException extends IllegalStateException {

    public InvariantAlreadyExistsException(String key) {
        super("Invariant for the \"" + key + "\" attribute already exists");
    }
}
