package org.drools.core.metadata;

public class With {

    private Object[] args;

    public static With updates( Object... args ) {
        return new With( args );
    }

    protected With( Object[] args ) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

}
