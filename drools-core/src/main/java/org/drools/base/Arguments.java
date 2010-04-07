package org.drools.base;

import java.util.Arrays;


public class Arguments {
    private final Object[] args;
    
    private static final Object[] EMPTY_PARAMS = new Object[0];
    
    public Arguments() {
        this(null);
    }
    
    public Arguments(final Object[] params) {
        if ( params != null ) {
            this.args = params;
        } else {
            this.args = EMPTY_PARAMS;
        }
    }    
    
    public Object[] getArguments() {
        return this.args;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode( args );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        Arguments other = (Arguments) obj;
        if ( !Arrays.equals( args,
                             other.args ) ) return false;
        return true;
    }
    
    
}
