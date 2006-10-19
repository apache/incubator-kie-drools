package org.drools.rule;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.drools.spi.Restriction;

public abstract class AbstractCompositeRestriction
    implements
    Restriction {

    private static final long     serialVersionUID = 320;

    protected final Restriction[] restrictions;

    public AbstractCompositeRestriction(final Restriction[] restriction) {
        this.restrictions = restriction;
    }

    public Declaration[] getRequiredDeclarations() {
        // Iterate all restrictions building up a unique list of declarations
        // No need to cache, as this should only be called once at build time
        final Set set = new HashSet();
        for ( int i = 0, ilength = this.restrictions.length; i < ilength; i++ ) {
            final Declaration[] declarations = this.restrictions[i].getRequiredDeclarations();
            for ( int j = 0, jlength = declarations.length; j < jlength; j++ ) {
                set.add( declarations[j] );
            }
        }

        return (Declaration[]) set.toArray( new Declaration[set.size()] );
    }

    private static int hashCode(final Object[] array) {
        final int PRIME = 31;
        if ( array == null ) {
            return 0;
        }
        int result = 1;
        for ( int index = 0; index < array.length; index++ ) {
            result = PRIME * result + (array[index] == null ? 0 : array[index].hashCode());
        }
        return result;
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + AbstractCompositeRestriction.hashCode( this.restrictions );
        return result;
    }

    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }

        if ( obj == null || obj instanceof AbstractCompositeRestriction ) {
            return false;
        }

        final AbstractCompositeRestriction other = (AbstractCompositeRestriction) obj;
        if ( !Arrays.equals( this.restrictions,
                             other.restrictions ) ) {
            return false;
        }
        return true;
    }
}
