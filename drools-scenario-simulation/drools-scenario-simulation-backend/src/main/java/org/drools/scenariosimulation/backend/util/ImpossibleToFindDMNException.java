package org.drools.scenariosimulation.backend.util;

/**
 * Utility that provide classPath scan to retrieve resources
 */
public class ImpossibleToFindDMNException extends IllegalArgumentException {

    public ImpossibleToFindDMNException(String message) {
        super(message);
    }

    public ImpossibleToFindDMNException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImpossibleToFindDMNException(Throwable cause) {
        super(cause.getMessage(), cause);
    }
}
