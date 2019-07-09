package semantic;

/**
 * An exception for semantic errors.
 */
public class SemanticException extends Exception {
    private static final long serialVersionUID = 726147768988453466L;

    /**
     * Constructs a SemanticException with the specified detail message.
     * 
     * @param message
     *            the detail message.
     */
    SemanticException(String message) {
        super(message);
    }
}
