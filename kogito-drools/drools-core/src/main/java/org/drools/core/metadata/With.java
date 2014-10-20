package org.drools.core.metadata;

import java.util.Collection;

public class With {

    private Object[] args;

    public static With with( Object... args ) {
        return new With( args );
    }

    public static With with( Collection args ) {
        return new With( args.toArray() );
    }

    public static With updates( Object... args ) {
        return new With( args );
    }

    public static With updates( Collection args ) {
        return new With( args.toArray() );
    }

    protected With( Object[] args ) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

}
