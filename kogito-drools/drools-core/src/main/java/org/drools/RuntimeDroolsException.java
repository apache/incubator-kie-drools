package org.drools;

/**
 * The base Drools exception for all internal thrown exceptions. If any exceptions are thrown during the
 * runtime execution of Drools they are considered non-recoverable and thus thrown as  Runtime exceptions
 * all the way up to the <Code>WorkingMemory</code> at which point they are nested inside the 
 * <code>CheckedDroolsException</code> for the user to decide how to respond.
 * 
 * @see RuntimeException
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public class RuntimeDroolsException extends RuntimeException {
    /**
     * @see java.lang.Exception#Exception()
     */
    public RuntimeDroolsException() {
        super();
    }

    /**
     * @see java.lang.Exception#Exception(String message)
     */
    public RuntimeDroolsException(String message) {
        super( message );
    }

    /**
     * @see java.lang.Exception#Exception(String message, Throwable cause)
     */
    public RuntimeDroolsException(String message,
                           Throwable cause) {
        super( message );
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     */
    public RuntimeDroolsException(Throwable cause) {
        super( cause );
    }

}
