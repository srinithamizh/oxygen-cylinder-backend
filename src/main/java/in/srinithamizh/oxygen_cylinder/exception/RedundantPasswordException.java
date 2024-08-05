package in.srinithamizh.oxygen_cylinder.exception;

public class RedundantPasswordException extends RuntimeException {
    public RedundantPasswordException(String message) {
        super(message);
    }
}
