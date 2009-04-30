package org.drools.runtime.rule;

import java.util.Collection;

import org.drools.KnowledgeBaseConfiguration;
import org.drools.runtime.ObjectFilter;

/**
 * <p>An entry-point is an abstract channel through where facts are inserted into the engine.</p>
 * <p>Drools 5 supports multiple entry-points into a single <code>StatefulKnowledgeBase</code>: the
 * default, anonymous entry-point, as well as as many user declared entry points the application 
 * requires.</p>
 * 
 * <p>To get a reference to an entry point, just request the session:</p>
 * <pre>
 * StatefulKnowledgeSession session = kbase.newStatelessKnowledgeSession();
 * ...
 * WorkingMemoryEntryPoint entrypoint = session.getWorkingMemoryEntryPoint("my entry point");
 * </pre> 
 * <p>Once a reference to an entry point is acquired, the application can insert, update and retract facts
 * to/from that entry-point as usual:</p>
 * <pre>
 * ...
 * FactHandle factHandle = entrypoint.insert( fact );
 * ...
 * entrypoint.update( factHandle, newFact );
 * ...
 * entrypoint.retract( factHandle );
 * ...
 * </pre> 
 * 
 */
public interface WorkingMemoryEntryPoint {

    /**
     * Inserts a new fact into this entry point
     * 
     * @param object 
     *        the fact to be inserted
     *        
     * @return the fact handle created for the given fact
     */
    FactHandle insert(Object object);

    /**
     * Retracts the fact for which the given FactHandle was assigned.
     * 
     * @param handle the handle whose fact is to be retracted.
     */
    void retract(FactHandle handle);

    /**
     * Updates the fact for which the given FactHandle was assigned with the new
     * fact set as the second parameter in this method.
     *  
     * @param handle the FactHandle for the fact to be updated.
     * 
     * @param object the new value for the fact being updated.
     */
    void update(FactHandle handle,
                Object object);

    /**
     * Returns the fact handle associated with the given object. It is important to note that this 
     * method behaves in accordance with the configured assert behaviour for this knowledge base
     * (either IDENTITY or EQUALITY).
     *  
     * @param object 
     *               the fact for which the fact handle will be returned.
     * 
     * @return the fact handle for the given object, or null in case no fact handle was found for the
     *         given object.
     *         
     * @see KnowledgeBaseConfiguration
     */
    FactHandle getFactHandle(Object object);

    /**
     * Returns the object associated with the given FactHandle.
     * 
     * @param factHandle
     * @return
     */
    Object getObject(FactHandle factHandle);

    /**
     * Returns all facts from the current session.
     * 
     * @return
     */
    Collection< Object > getObjects();

    /**
     * Returns all facts from the current session that are accepted by the given <code>ObjectFilter</code>.
     * 
     * @param filter the filter to be applied to the returned collection of facts.
     *  
     * @return
     */
    Collection< Object > getObjects(ObjectFilter filter);

    /**
     * Returns all <code>FactHandle</code>s from the current session.
     * 
     * @return
     */
    <T extends FactHandle> Collection< T > getFactHandles();

    /**
     * Returns all <code>FactHandle</code>s from the current session for which the facts are accepted by 
     * the given filter.
     * 
     * @param filter the filter to be applied to the returned collection of <code>FactHandle</code>s.
     * 
     * @return
     */
    <T extends FactHandle> Collection< T > getFactHandles(ObjectFilter filter);    
}
