package org.objenesis;

/**
 * Exception thrown by Objenesis. It wraps any instantiation exceptions. Note that this exception is
 * runtime to prevent having to catch it. It will do normal exception wrapping for JDK 1.4 and more
 * and basic message wrapping for JDK 1.3.
 * 
 * @author Henri Tremblay
 */
public class ObjenesisException extends RuntimeException {

    private static final boolean jdk14 = (Double.parseDouble( System.getProperty( "java.specification.version" ) ) > 1.3);

    /**
     * @param msg Error message
     */
    public ObjenesisException(final String msg) {
        super( msg );
    }

    /**
     * @param cause Wrapped exception. The message will be the one of the cause.
     */
    public ObjenesisException(final Throwable cause) {
        super( cause == null ? null : cause.toString() );
        if ( jdk14 ) {
            initCause( cause );
        }
    }

    /**
     * @param msg Error message
     * @param cause Wrapped exception
     */
    public ObjenesisException(final String msg,
                              final Throwable cause) {
        super( msg );
        if ( jdk14 ) {
            initCause( cause );
        }
    }
}
