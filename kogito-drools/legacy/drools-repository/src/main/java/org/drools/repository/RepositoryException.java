package org.drools.repository;

/**
 * This may be thrown by the repository if certain rules of access are broken.
 * Generally this will roll back any transactions in progress.
 * 
 * Mostly it will contain a validation message which may be displayed.
 * 
 * The repository instance should remain valid, however.
 * 
 * If any other exceptions are thrown, however, the repository manager instance will be 
 * invalid.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RepositoryException extends RuntimeException {

    private static final long serialVersionUID = 6720400703993720887L;

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
