package org.drools.core.reteoo;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.base.rule.EntryPointId;
import org.drools.base.rule.TypeDeclaration;
import org.drools.core.rule.accessor.FactHandleFactory;

public interface ObjectTypeConf {
	String getTypeName();

    ObjectTypeNode[] getObjectTypeNodes();

    ObjectTypeNode getConcreteObjectTypeNode();

    void resetCache();

    boolean isAssignableFrom(Object object);

    boolean isActive();

    boolean isEvent();

    boolean isDynamic();

    boolean isPrototype();

    TypeDeclaration getTypeDeclaration();
    
    /** Whether or not, TMS is active for this object type. */
    boolean isTMSEnabled();

    /**
     * Enable TMS for this object type. 
     * */
    void enableTMS();
    
    EntryPointId getEntryPoint();

    InternalFactHandle createFactHandle(FactHandleFactory factHandleFactory, long id, Object object, long recency,
                                        ReteEvaluator reteEvaluator, WorkingMemoryEntryPoint entryPoint);
}
