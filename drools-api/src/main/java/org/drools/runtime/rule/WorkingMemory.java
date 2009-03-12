package org.drools.runtime.rule;

import java.util.Collection;

import org.drools.KnowledgeBaseConfiguration;
import org.drools.runtime.ObjectFilter;
import org.drools.time.SessionClock;

/**
 * The <code>WorkingMemory</code> is a super-interface for all <code>StatefulKnowledgeSession</code>s.
 * Although, users are encouraged to use <code>StatefulKnowledgeSession</code> or <code>KnowledgeRuntime</code>
 * interface instead of <code>WorkingMemory</code> interface, specially because of the <code>dispose()</code> method
 * that is only available in the <code>StatefulKnowledgeSession</code> interface.  
 * 
 * @see org.drools.runtime.StatefulKnowledgeSession 
 */
public interface WorkingMemory
    extends
    WorkingMemoryEntryPoint {

    /**
     * <p>Request the engine to stop firing rules. If the engine is currently firing a rule, it will
     * finish executing this rule's consequence before stopping.</p>
     * <p>This method will not remove active activations from the Agenda.
     * In case the application later wants to continue firing rules from the point where it stopped,
     * it should just call <code>org.drools.runtime.StatefulKnowledgeSession.fireAllRules()</code> or 
     * <code>org.drools.runtime.StatefulKnowledgeSession.fireUntilHalt()</code> again.</p>
     */
    void halt();

    /**
     * Returns the session clock instance assigned to this session
     * @return
     */
    public SessionClock getSessionClock();

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
    Collection< ? > getObjects();

    /**
     * Returns all facts from the current session that are accepted by the given <code>ObjectFilter</code>.
     * 
     * @param filter the filter to be applied to the returned collection of facts.
     *  
     * @return
     */
    Collection< ? > getObjects(ObjectFilter filter);

    /**
     * Returns all <code>FactHandle</code>s from the current session.
     * 
     * @return
     */
    Collection< ? extends FactHandle> getFactHandles();

    /**
     * Returns all <code>FactHandle</code>s from the current session for which the facts are accepted by 
     * the given filter.
     * 
     * @param filter the filter to be applied to the returned collection of <code>FactHandle</code>s.
     * 
     * @return
     */
    Collection< ? extends FactHandle> getFactHandles(ObjectFilter filter);

    /**
     * Returns a reference to this session's <code>Agenda</code>.
     * 
     * @return
     */
    Agenda getAgenda();

    /**
     * Returns the WorkingMemoryEntryPoint instance associated with the given name.
     * 
     * @param name
     * @return
     */
    WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name);
    
    /**
     * Returns a collection of all available working memory entry points
     * for this session.
     * 
     * @return the collection of all available entry points for this session
     */
    Collection<? extends WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints();
    
    /**
     * Retrieve the QueryResults of the specified query.
     *
     * @param query
     *            The name of the query.
     *
     * @return The QueryResults of the specified query.
     *         If no results match the query it is empty.
     *
     * @throws IllegalArgumentException
     *         if no query named "query" is found in the KnowledgeBase
     */
    public QueryResults getQueryResults(String query);

    /**
     * Retrieve the QueryResults of the specified query and arguments
     *
     * @param query
     *            The name of the query.
     *
     * @param arguments
     *            The arguments used for the query
     *
     * @return The QueryResults of the specified query.
     *         If no results match the query it is empty.
     *
     * @throws IllegalArgumentException
     *         if no query named "query" is found in the KnowledgeBase
     */
    public QueryResults getQueryResults(String query, Object[] arguments);      
}
