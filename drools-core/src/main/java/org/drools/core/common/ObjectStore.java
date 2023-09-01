package org.drools.core.common;

import org.kie.api.internal.utils.KieService;
import org.kie.api.runtime.ObjectFilter;

import java.util.Iterator;

public interface ObjectStore extends KieService {

    int size();

    boolean isEmpty();
    
    void clear();

    Object getObjectForHandle(InternalFactHandle handle);
    
    InternalFactHandle reconnect(InternalFactHandle factHandle);

    InternalFactHandle getHandleForObject(Object object);

    void updateHandle(InternalFactHandle handle, Object object);

    void addHandle(InternalFactHandle handle, Object object);

    void removeHandle(final InternalFactHandle handle);

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    Iterator<Object> iterateObjects();

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    Iterator<Object> iterateObjects(ObjectFilter filter);

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    Iterator<InternalFactHandle> iterateFactHandles();

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    Iterator<InternalFactHandle> iterateFactHandles(ObjectFilter filter);

    Iterator<Object> iterateNegObjects(ObjectFilter filter);

    Iterator<InternalFactHandle> iterateNegFactHandles(ObjectFilter filter);

    FactHandleClassStore getStoreForClass(Class<?> clazz);

    boolean clearClassStore(Class<?> clazz);

    default Iterator<InternalFactHandle> iterateFactHandles(Class<?> clazz) {
        return getStoreForClass(clazz).iterator();
    }
}
