package org.drools.runtime;

import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.runtime.process.StatelessProcessSession;
import org.drools.runtime.rule.StatelessRuleSession;

/**
 * StatelessKnowledgeSessions are convenience api, that wraps a StatefulKonwledgeSession. It removes the need to
 * call dispose(), as well as providing support for execution parameters. Stateless sessions do not support
 * iterative insertions and fireAllRules from java code, the act of calling executeObject() or executeIterable() is a single
 * shot method that will internally instantiate a StatefullKnowledgeSession, add all the user data, call fireAllRules, and then
 * call dispose(). Additionally to this convenience it also adds additional functionality via the use of Parameters.
 * 
 * <p>
 * Simple example showing a stateless session executing for a given collection of java objects.
 * </p>
 * <pre>
 * KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
 * kbuilder.add( ResourceFactory.newFileSystemResource( fileName ), ResourceType.DRL );
 * assertFalse( kbuilder.hasErrors() );     
 * if (kbuilder.hasErrors() ) {
 *     System.out.println( kbuilder.getErrors() );
 * }
 * KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
 * kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
 * 
 * StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
 * ksession.executeIterable( collection );
 * </pre>
 * 
 * <p>
 * StatelessKnowledgeSessions support globals, scoped in a number of ways. I'll cover the non-parameter way first,
 * as parameters are scoped to a specific execution call. Globals can be resolved in three ways. The StatelessKnowledgeSession 
 * supports setGlobalResolver() and setGlobal(). These globals are shared for ALL execution calls, so be especially careful of mutable
 * globals in these cases - as often execution calls can be executing simultaneously in different threads.
 * Calling of setGlobal(String, Object) will actually be set on an internal Collection, identifiers in this internal 
 * Collection will have priority over the externally supplied GlobalResolver. If an identifier cannot be found in 
 * the internal Collection, it will then check the externally supplied Global Resolver, if one has been set.
 * </p>
 * 
 * <p>code snippet for setting a global:</p>
 * <pre>
 * StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
 * ksession.setGlobal( "hbnSession", hibernateSession ); // sets a global hibernate session, that can be used for DB interactions in the rules.
 * ksession.executeIterable( collection ); // this will now execute and will be able to resolve the "hbnSession" identifier.
 * </pre>
 * 
 * <p>
 * Stateless sessions also support in, out, inOut parameters. These parameters and their values are scoped to the execution call they are used in. 
 * All external variables in Drools stateless sessions are represented by globals in the drl. The in, out, inOut parameters are effectively mappings 
 * to these globals. To work with parameters the Parameters class must be used. in, out, inOut parameters can be used for Globals and for Facts.
 * </p>
 * 
 * <p>This example shows the setting of both an in and an out global, as well as both an in and an out fact.</p>
 * <pre>
 * Parameters parameters = ksession.newParameters();
 * Map<String, Object> globalsIn = new HashMap<String, Object>();
 * globalsIn.put( "inString", "string" );
 * parameters.getGlobalParams().setIn( globalsIn );        
 * parameters.getGlobalParams().setOut( Arrays.asList(  new String[]{"list"} ) ); 
 *       
 * Map<String, Object> factIn = new HashMap<String, Object>();
 * factIn.put( "inCheese", cheddar );
 * parameters.getFactParams().setIn( factIn );
 * parameters.getFactParams().setOut( Arrays.asList(  new String[]{ "outCheese"} ) );         
 * 
 * StatelessKnowledgeSessionResults results = ksession.executeObjectWithParameters( collection, // these facts are anonymous
 *                                                                                 parameters );
 * </pre>
 * <p>
 * A created and inserted fact, from inside of the engine, is not automatically associated with an out parameter - there is no way for the engine
 * to infer this information. So it is up to the user to do this mapping manually. The following code snippet demonstrates how to do this, for the above 
 * "outCheese" parameter:
 * </p>
 * <pre>
 * global Cheese outCheese
 *  
 * rule "out example"
 * when
 *     ...
 * then
 *     Cheese brie = new Cheese("brie", 50);
 *     insert( brie );
 *     drools.getWorkingMemory().setGlobal("outCheese", brie);
 * end  
 * </pre>  
 * 
 * @see org.drools.runtime.Parameters
 * @see org.drools.runtime.GlobalResolver
 */
public interface StatelessKnowledgeSession
    extends
    StatelessRuleSession,
    StatelessProcessSession,
    KnowledgeRuntimeEventManager {

    /**
     * Delegate used to resolve any global names not found in the internally collection.
     * @param globalResolver
     */
    void setGlobalResolver(GlobalResolver globalResolver);

    /**
     * Sets a global value on the internal collection
     * @param identifer the global identifier
     * @param value the value assigned to the global identifier
     */
    void setGlobal(String identifer,
                   Object value);
}
