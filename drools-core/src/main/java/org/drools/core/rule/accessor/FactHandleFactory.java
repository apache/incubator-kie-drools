package org.drools.core.rule.accessor;

import java.util.Collection;

import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.reteoo.ObjectTypeConf;

/**
 * Factory Interface to return new <code>FactHandle</code>s
 */
public interface FactHandleFactory {
   /**
     * Construct a handle with a new id.
     * 
     * @return The handle.
     */
    InternalFactHandle newFactHandle(Object object,
                                     ObjectTypeConf conf,
                                     ReteEvaluator reteEvaluator,
                                     WorkingMemoryEntryPoint wmEntryPoint );
    
    InternalFactHandle newFactHandle(long id,
                                     Object object,
                                     long recency,
                                     ObjectTypeConf conf,
                                     ReteEvaluator reteEvaluator,
                                     WorkingMemoryEntryPoint wmEntryPoint );

    InternalFactHandle newInitialFactHandle(WorkingMemoryEntryPoint wmEntryPoint);
    
    /**
     * Increases the recency of the FactHandle
     * 
     * @param factHandle
     *      The fact handle to have its recency increased.
     */
    void increaseFactHandleRecency(InternalFactHandle factHandle);

    void destroyFactHandle(InternalFactHandle factHandle);

    /**
     * @return a fresh instance of the fact handle factory, with any IDs reset etc.
     */
    FactHandleFactory newInstance();
    
    FactHandleFactory newInstance(long id, long counter);

    Class<?> getFactHandleType();

    long getId();

    long getRecency();

    long getNextId();

    long getNextRecency();
    
    void clear(long id, long counter);

    void doRecycleIds( Collection<Long> usedIds );
    void stopRecycleIds();

    DefaultFactHandle createDefaultFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint entryPoint);

    DefaultEventHandle createEventFactHandle(long id, Object object, long recency, WorkingMemoryEntryPoint entryPoint, long timestamp, long duration);
}
