package org.drools;

import java.util.Collection;
import java.util.List;

import org.drools.concurrent.Future;
import org.drools.spi.AgendaFilter;

/**
 * A stateful session represents a working memory which keeps state
 * between invocations (accumulating facts/knowledge).
 *
 * Caution should be used when using the async methods (take note of the javadocs for specific methods).
 */
public interface StatefulSession
    extends
    WorkingMemory {

    /**
     * Forces the workingMemory to be derefenced from
     *
     */
    void dispose();

    /**
     * Insert/Assert an object asynchronously.
     * (return immediately, even while the insertion is taking effect).
     * The returned Future object can be queried to check on the status of the task.
     * You should only use the async methods if you are sure you require a background
     * insertion task to take effect (a new thread may be created).
     * If you are not sure, then you probably don't need to use it !
     */
    Future asyncInsert(Object object);

    Future asyncRetract(FactHandle factHandle);

    Future asyncUpdate(FactHandle factHandle,
                       Object object);

    /**
     * Insert/Assert an array of objects..
     * (return immediately, even while the insertion is taking effect).
     * The returned Future object can be queried to check on the status of the task.
     * You should only use the async methods if you are sure you require a background
     * insertion task to take effect (a new thread may be created).
     * If you are not sure, then you probably don't need to use it !
     */
    Future asyncInsert(Object[] array);

    /**
     * Insert/Assert a collect of objects..
     * (return immediately, even while the insertion is taking effect).
     * The returned Future object can be queried to check on the status of the task.
     * You should only use the async methods if you are sure you require a background
     * insertion task to take effect (a new thread may be created).
     * If you are not sure, then you probably don't need to use it !
     */
    Future asyncInsert(Collection collect);

    /**
     * This will initiate the firing phase (in the background).
     * And return immediately. The returned Future object can be queried
     * to check on the status of the task.
     */
    Future asyncFireAllRules();

    /**
     * This will initiate the firing phase (in the background).
     * And return immediately. The returned Future object can be queried
     * to check on the status of the task.
     */
    Future asyncFireAllRules(AgendaFilter agendaFilter);

    public List getRuleBaseUpdateListeners();
}
