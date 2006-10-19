package org.drools.rule;

import org.drools.WorkingMemory;
import org.drools.spi.Restriction;
import org.drools.spi.Tuple;

public class OrCompositeRestriction extends AbstractCompositeRestriction {

    private static final long serialVersionUID = 320;

    public OrCompositeRestriction(final Restriction[] restriction) {
        super( restriction );
    }

    public boolean isAllowed(final Object object,
                             final Tuple tuple,
                             final WorkingMemory workingMemory) {

        for ( int i = 0, ilength = this.restrictions.length; i < ilength; i++ ) {
            if ( this.restrictions[i].isAllowed( object,
                                            tuple,
                                            workingMemory ) ) {
                return true;
            }
        }
        return false;

    }
}
