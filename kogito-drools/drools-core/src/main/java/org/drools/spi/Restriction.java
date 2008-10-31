package org.drools.spi;

import java.io.Externalizable;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;

public interface Restriction
    extends
    Externalizable,
    Cloneable {
    Declaration[] getRequiredDeclarations();

    public boolean isAllowed(InternalReadAccessor extractor,
                             InternalFactHandle handle,
                             InternalWorkingMemory workingMemory,
                             ContextEntry context );

    public boolean isAllowedCachedLeft(ContextEntry context,
                                       InternalFactHandle handle);

    public boolean isAllowedCachedRight(LeftTuple tuple,
                                        ContextEntry context);

    public ContextEntry createContextEntry();

    /**
     * A restriction may be required to replace an old
     * declaration object by a new updated one
     *
     * @param oldDecl
     * @param newDecl
     */
    void replaceDeclaration(Declaration oldDecl,
                            Declaration newDecl);

    public Object clone();

}
