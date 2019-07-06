package semantic;

/**
 * An exception for semantic errors.
 */
public class SemanticException extends Exception {
    private static final long serialVersionUID = 726147768988453466L;

    /**
     * Constructs a SemanticException with no detail message.
     */
    public SemanticException() {
        super();
    }

    /**
     * Constructs a SemanticException with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    public SemanticException(String message) {
        super(message);
    }

    /**
     * Constructs a SemanticException with the specified cause.
     * 
     * @param cause
     *            the cause.
     */
    public SemanticException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SemanticException with the specified detail message and cause.
     * 
     * @param message
     *            the detail message.
     * @param cause
     *            the cause.
     */
    public SemanticException(String message, Throwable cause) {
        super(message, cause);
    }
}
