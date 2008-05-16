package org.drools.rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ReadAccessor;
import org.drools.spi.Restriction;

public class MultiRestrictionFieldConstraint extends MutableTypeConstraint {

    private static final long    serialVersionUID = 400L;

    private InternalReadAccessor extractor;

    private Restriction          restrictions;

    public MultiRestrictionFieldConstraint() {

    }

    public MultiRestrictionFieldConstraint(final InternalReadAccessor extractor,
                                           final Restriction restrictions) {
        this.extractor = extractor;
        this.restrictions = restrictions;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        extractor = (InternalReadAccessor) in.readObject();
        restrictions = (Restriction) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( extractor );
        out.writeObject( restrictions );
    }

    public ReadAccessor getFieldExtractor() {
        return this.extractor;
    }

    public Declaration[] getRequiredDeclarations() {
        return this.restrictions.getRequiredDeclarations();
    }

    public void replaceDeclaration(Declaration oldDecl,
                                   Declaration newDecl) {
        this.restrictions.replaceDeclaration( oldDecl,
                                              newDecl );
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

    public boolean isAllowed(final InternalFactHandle handle,
                             final InternalWorkingMemory workingMemory,
                             final ContextEntry context) {
        return this.restrictions.isAllowed( this.extractor,
                                            handle,
                                            workingMemory,
                                            context );
    }

    public ContextEntry createContextEntry() {
        return this.restrictions.createContextEntry();
    }

    public boolean isAllowedCachedLeft(final ContextEntry context,
                                       final InternalFactHandle handle) {
        return this.restrictions.isAllowedCachedLeft( context,
                                                      handle );
    }

    public boolean isAllowedCachedRight(final LeftTuple tuple,
                                        final ContextEntry context) {
        return this.restrictions.isAllowedCachedRight( tuple,
                                                       context );
    }

    public Object clone() {
        return new MultiRestrictionFieldConstraint( this.extractor,
                                                    (Restriction) this.restrictions.clone() );
    }

}