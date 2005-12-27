package org.drools.rule;

public class InvalidPatternException extends RuleConstructionException {
    /**
     * @see java.lang.Exception#Exception()
     */
    InvalidPatternException(){
        super();
    }

    /**
     * @see java.lang.Exception#Exception(String message)
     */
    InvalidPatternException(String message){
        super( message );
    }

    /**
     * @see java.lang.Exception#Exception(String message, Throwable cause)
     */
    InvalidPatternException(String message,
                            Throwable cause){
        super( message,
               cause );
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     */
    InvalidPatternException(Throwable cause){
        super( cause );
    }
}
