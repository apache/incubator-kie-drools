package org.drools.leaps;

import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.spi.FieldConstraint;
import org.drools.spi.Tuple;
import org.drools.util.IdentityMap;

class AlphaMemory {
    private IdentityMap alphaChecks = new IdentityMap( );

    AlphaMemory() {

    }

    boolean checkAlpha( final FieldConstraint alpha,
                        final InternalFactHandle factHandle,
                        final Tuple tuple,
                        final WorkingMemory workingMemory ) {
        Boolean ret = (Boolean) this.alphaChecks.get( factHandle );
        if (ret == null) {
            ret = new Boolean( alpha.isAllowed( factHandle, tuple, workingMemory ) );
            this.alphaChecks.put( factHandle, ret );
        }

        return ret.booleanValue( );
    }
    
    boolean isAlphaBeenChecked(final InternalFactHandle factHandle){
        return this.alphaChecks != null && this.alphaChecks.containsKey( factHandle );
    }
}
