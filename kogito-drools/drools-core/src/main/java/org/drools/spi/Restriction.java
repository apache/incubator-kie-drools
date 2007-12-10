package org.drools.spi;

import java.io.Serializable;

import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteTuple;
import org.drools.rule.ContextEntry;
import org.drools.rule.Declaration;

public interface Restriction
    extends
    Serializable,
    Cloneable {
    Declaration[] getRequiredDeclarations();

    public boolean isAllowed(Extractor extractor,
                             InternalFactHandle handle,
                             InternalWorkingMemory workingMemory);

    public boolean isAllowedCachedLeft(ContextEntry context,
                                       InternalFactHandle handle);

    public boolean isAllowedCachedRight(ReteTuple tuple,
                                        ContextEntry context);

    public ContextEntry getContextEntry();

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
