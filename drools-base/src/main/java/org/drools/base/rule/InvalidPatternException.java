package org.drools.base.rule;

public class InvalidPatternException extends RuleConstructionException {
    private static final long serialVersionUID = 510l;

    /**
     * @see java.lang.Exception#Exception()
     */
    public InvalidPatternException() {
        super();
    }

    /**
     * @see java.lang.Exception#Exception(String message)
     */
    public InvalidPatternException(final String message) {
        super( message );
    }

    /**
     * @see java.lang.Exception#Exception(String message, Throwable cause)
     */
    public InvalidPatternException(final String message,
                                   final Throwable cause) {
        super( message,
               cause );
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     */
    public InvalidPatternException(final Throwable cause) {
        super( cause );
    }
}
