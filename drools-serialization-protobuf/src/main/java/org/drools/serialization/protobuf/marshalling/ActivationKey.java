package org.drools.serialization.protobuf.marshalling;

import java.util.Arrays;

public class ActivationKey {

    private final String pkgName;
    private final String ruleName;
    private final Object[] tuple;

    public ActivationKey(String pkgName,
                         String ruleName,
                         Object[] tuple) {
        this.pkgName = pkgName;
        this.ruleName = ruleName;
        this.tuple = tuple;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pkgName == null) ? 0 : pkgName.hashCode());
        result = prime * result + ((ruleName == null) ? 0 : ruleName.hashCode());
        result = prime * result + Arrays.deepHashCode( tuple );
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ActivationKey other = (ActivationKey) obj;
        if ( pkgName == null ) {
            if ( other.pkgName != null ) return false;
        } else if ( !pkgName.equals( other.pkgName ) ) return false;
        if ( ruleName == null ) {
            if ( other.ruleName != null ) return false;
        } else if ( !ruleName.equals( other.ruleName ) ) return false;
        return Arrays.deepEquals( tuple, other.tuple );
    }
}
