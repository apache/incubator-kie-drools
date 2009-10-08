package org.drools.common;

import java.util.Iterator;

import org.drools.runtime.rule.FactHandle;

public interface ObjectStore {

    int size();

    boolean isEmpty();
    
    void clear();    

    Object getObjectForHandle(FactHandle handle);
    
    InternalFactHandle reconnect(FactHandle factHandle);

    InternalFactHandle getHandleForObject(Object object);
    
    InternalFactHandle getHandleForObjectIdentity(Object object);

    void updateHandle(InternalFactHandle handle,
                                      Object object);

    public abstract void addHandle(InternalFactHandle handle,
                                   Object object);

    public abstract void removeHandle(final FactHandle handle);

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateObjects();

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateObjects(org.drools.runtime.ObjectFilter filter);

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateFactHandles();

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateFactHandles(org.drools.runtime.ObjectFilter filter);

}