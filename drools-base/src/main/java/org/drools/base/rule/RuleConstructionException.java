package org.drools.base.rule;

/**
 * Base exception for errors during <code>Rule</code> construction.
 */
public class RuleConstructionException extends RuntimeException {
    private static final long serialVersionUID = 510l;

    /**
     * @see java.lang.Exception#Exception()
     */
    RuleConstructionException() {
        super();
    }

    /**
     * @see java.lang.Exception#Exception(String message)
     */
    RuleConstructionException(final String message) {
        super( message );
    }

    /**
     * @see java.lang.Exception#Exception(String message, Throwable cause)
     */
    RuleConstructionException(final String message,
                              final Throwable cause) {
        super( message,
               cause );
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     */
    RuleConstructionException(final Throwable cause) {
        super( cause );
    }
}
