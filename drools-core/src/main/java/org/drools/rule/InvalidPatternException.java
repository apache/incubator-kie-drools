package org.drools.rule;

public class InvalidPatternException extends RuleConstructionException {
    /**
     * @see java.lang.Exception#Exception()
     */
    public InvalidPatternException() {
        super();
    }

    /**
     * @see java.lang.Exception#Exception(String message)
     */
    public InvalidPatternException(String message) {
        super( message );
    }

    /**
     * @see java.lang.Exception#Exception(String message, Throwable cause)
     */
    public InvalidPatternException(String message,
                                   Throwable cause) {
        super( message,
               cause );
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     */
    public InvalidPatternException(Throwable cause) {
        super( cause );
    }
}
