package org.drools.rule;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.spi.Extractor;
import org.drools.spi.Restriction;

public class OrCompositeRestriction extends AbstractCompositeRestriction {

    private static final long serialVersionUID = 400L;

    public OrCompositeRestriction(final Restriction[] restriction) {
        super( restriction );
    }

    public boolean isAllowed(final Extractor extractor,
                             final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory) {
        for ( int i = 0, ilength = this.restrictions.length; i < ilength; i++ ) {
            if ( this.restrictions[i].isAllowed( extractor,
                                                 handle,
                                                 workingMemory ) ) {
                return true;
            }
        }
        return false;
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        for ( int i = 0, ilength = this.restrictions.length; i < ilength; i++ ) {
            if ( this.restrictions[i].isAllowedCachedLeft( this.contextEntry.contextEntries[i],
                                                           handle ) ) {
                return true;
            }
        }
        return false;
    }

    public boolean isAllowedCachedRight(final ReteTuple tuple,
                                        final ContextEntry context) {
        for ( int i = 0, ilength = this.restrictions.length; i < ilength; i++ ) {
            if ( this.restrictions[i].isAllowedCachedRight( tuple,
                                                            this.contextEntry.contextEntries[i] ) ) {
                return true;
            }
        }
        return false;
    }

    public Object clone() {
        Restriction[] clone = new Restriction[ this.restrictions.length ];
        for( int i = 0; i < clone.length; i++ ) {
            clone[i] = (Restriction) this.restrictions[i].clone();
        }
        return new OrCompositeRestriction( clone );
    }
}
