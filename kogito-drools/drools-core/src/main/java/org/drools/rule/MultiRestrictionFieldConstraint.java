package org.drools.rule;

import org.drools.WorkingMemory;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.FieldExtractor;
import org.drools.spi.Restriction;
import org.drools.spi.Tuple;

public class MultiRestrictionFieldConstraint
    implements
    AlphaNodeFieldConstraint {

    /**
     * 
     */
    private static final long    serialVersionUID = 320;

    private final FieldExtractor extractor;

    private final Restriction    restrictions;

    public MultiRestrictionFieldConstraint(final FieldExtractor extractor,
                                           final Restriction restrictions) {
        this.extractor = extractor;
        this.restrictions = restrictions;
    }

    public FieldExtractor getFieldExtractor() {
        return this.extractor;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.restrictions.getRequiredDeclarations();
    }

    public boolean isAllowed(final Object object,
                             final Tuple tuple,
                             final WorkingMemory workingMemory) {
        return this.restrictions.isAllowed( this.extractor.getValue( object ),
                                            tuple,
                                            workingMemory );
    }

    public String toString() {
        return "[MultiRestrictionConstraint fieldExtractor=" + this.extractor + " restrictions =" + this.restrictions + "]";
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * this.extractor.hashCode();
        result = PRIME * this.restrictions.hashCode();
        return result;
    }

    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }
        if ( object == null || object.getClass() != MultiRestrictionFieldConstraint.class ) {
            return false;
        }
        final MultiRestrictionFieldConstraint other = (MultiRestrictionFieldConstraint) object;

        return this.extractor.equals( other.extractor ) && this.restrictions.equals( other.restrictions );
    }

}