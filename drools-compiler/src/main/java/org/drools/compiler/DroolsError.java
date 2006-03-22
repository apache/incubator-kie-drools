package org.drools.compiler;

public abstract class DroolsError {

    /**
     * Classes that extend this must provide a printable message,
     * which summarises the error.
     */
    public abstract String getMessage();
}
