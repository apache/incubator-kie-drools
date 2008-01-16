package org.drools.common;

import java.util.Iterator;

import org.drools.ObjectFilter;

public interface ObjectStore {

    public abstract int size();

    public abstract boolean isEmpty();

    public abstract Object getObjectForHandle(InternalFactHandle handle);

    public abstract InternalFactHandle getHandleForObject(Object object);
    
    public abstract InternalFactHandle getHandleForObjectIdentity(Object object);

    public abstract void updateHandle(InternalFactHandle handle,
                                      Object object);

    public abstract void addHandle(InternalFactHandle handle,
                                   Object object);

    public abstract void removeHandle(final InternalFactHandle handle);

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateObjects();

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateObjects(ObjectFilter filter);

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateFactHandles();

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public abstract Iterator iterateFactHandles(ObjectFilter filter);

}