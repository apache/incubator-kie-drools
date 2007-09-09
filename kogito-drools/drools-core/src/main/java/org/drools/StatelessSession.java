package org.drools;

import java.util.Collection;
import java.util.List;

import org.drools.event.AgendaEventListener;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.spi.AgendaFilter;
import org.drools.spi.GlobalExporter;
import org.drools.spi.GlobalResolver;

/**
 * This represents a working memory session where state is not kept between
 * invocations.
 * This is typically used for "decision services" where the rules are
 * provided all the data in one hit, and a conclusion reached by the engine.
 * (there is no accumulation of facts/knowledge - each invocation is on a fresh session).
 * 
 * Each created StatelessSession can be call execute() any number of times, in a stateless manner
 * however the GlobalResolver, unless set each time, is shared between each execute() method call.
 * 
 * Care should be used when using the async versions of the methods, consult the javadoc for 
 * the specific information.
 */
public interface StatelessSession extends EventManager {
        
    void setAgendaFilter(AgendaFilter agendaFilter);
    
    /**
     * Delegate used to resolve any global names not found in the global map.
     * @param globalResolver
     */
    void setGlobalResolver(GlobalResolver globalResolver);
    
    /**
     * Sets a global value
     * @param identifer
     * @param value
     */
    void setGlobal(String identifer, Object value);   
    
    /**
     * Used to specify a global exporting strategy
     * so that global variables can be available to StatelessSessionResults.
     * 
     * If this is not set, then StatelessSessionResult will have no globals.
     * @param globalExporter
     *                     The GlobalExporter instance
     */
    public void setGlobalExporter(GlobalExporter globalExporter);    
    
    
    /**
     * Insert a single fact, an fire the rules, returning when finished.
     */
    void execute(Object object);

    /**
     * Insert an array of facts, an fire the rules, returning when finished.
     * This will assert the list of facts as SEPARATE facts to the engine
     * (NOT an array).
     */    
    void execute(Object[] array);

    /**
     * Insert a List of facts, an fire the rules, returning when finished.
     * This will assert the list of facts as SEPARATE facts to the engine
     * (NOT as a List).
     */        
    void execute(Collection collection);    
    
    /**
     * This will assert the object in the background. This is
     * "send and forget" execution.
     */
    void asyncExecute(Object object);

    /**
     * This will assert the object array (as SEPARATE facts) in the background. This is
     * "send and forget" execution.
     */
    void asyncExecute(Object[] array);

    /**
     * This will assert the object List (as SEPARATE facts) in the background. This is
     * "send and forget" execution.
     */    
    void asyncExecute(Collection collection);      
    
    
    /**
     * Similar to the normal execute method, but this will return
     * "results". 
     */
    StatelessSessionResult executeWithResults(Object object);

    /**
     * Similar to the normal execute method, but this will return
     * "results". 
     */    
    StatelessSessionResult executeWithResults(Object[] array);

    /**
     * Similar to the normal execute method, but this will return
     * "results". 
     */   
    StatelessSessionResult executeWithResults(Collection collection);
}
