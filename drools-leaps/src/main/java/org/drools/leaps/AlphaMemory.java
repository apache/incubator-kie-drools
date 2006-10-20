package org.drools.leaps;

import org.apache.commons.collections.map.IdentityMap;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.Tuple;

class AlphaMemory {
    private final IdentityMap alphaChecks = new IdentityMap();

    AlphaMemory() {

    }

    boolean checkAlpha(final AlphaNodeFieldConstraint alpha,
                       final InternalFactHandle factHandle,
                       final Tuple tuple,
                       final WorkingMemory workingMemory) {
        Boolean ret = (Boolean) this.alphaChecks.get( factHandle );
        if ( ret == null ) {
            ret = new Boolean( alpha.isAllowed( factHandle.getObject(),
                                                tuple,
                                                workingMemory ) );
            this.alphaChecks.put( factHandle,
                                  ret );
        }

        return ret.booleanValue();
    }

    boolean isAlphaBeenChecked(final InternalFactHandle factHandle) {
        return this.alphaChecks != null && this.alphaChecks.containsKey( factHandle );
    }
}
