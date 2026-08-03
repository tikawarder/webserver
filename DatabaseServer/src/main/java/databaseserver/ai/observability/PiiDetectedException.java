package databaseserver.ai.observability;

public class PiiDetectedException extends RuntimeException {

    public PiiDetectedException(String message) {
        super(message);
    }
}
