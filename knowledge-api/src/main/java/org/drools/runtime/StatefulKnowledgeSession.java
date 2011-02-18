/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.runtime;

import org.drools.runtime.process.StatefulProcessSession;
import org.drools.runtime.rule.StatefulRuleSession;

/**
 * StatefulKnowledgeSession is the most common way to interact with a rules engine. A StatefulKnowledgeSession
 * allows the application to establish an iterative conversation with the engine, where the reasoning process
 * may be triggered multiple times for the same set of data. After the application finishes using the session,
 * though, it <b>must</b> call the <code>dispose()</code> method in order to free the resources and used memory.
 * 
 * <p>
 * Simple example showing a stateful session executing for a given collection of java objects.
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
 * StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
 * for( Object fact : facts ) {
 *     ksession.insert( fact );
 * }
 * ksession.fireAllRules();
 * ksession.dispose();
 * </pre>
 * 
 * <p>
 * StatefulKnowledgeSessions support globals. Globals are used to pass information into the engine and receive callbacks
 * from your rules, but they should not be used to reason over. If you need to reason over your data, make sure you insert
 * it as a fact, not a global.</p>
 * <p>Globals are shared among ALL your rules, so be especially careful of (and avoid as much as possible) mutable globals. 
 * Also, it is a good practice to set your globals before inserting your facts. Rules engines evaluate rules at fact insertion
 * time, and so, if you are using a global to constraint a fact pattern, and the global is not set, you may receive a 
 * <code>NullPointerException</code>. </p> 
 * <p>Globals can be resolved in two ways. The StatefulKnowledgeSession supports getGlobals() which returns the internal Globals, which itself
 * can take a delegate. Calling of setGlobal(String, Object) will set the global on an internal Collection. Identifiers in this internal 
 * Collection will have priority over the externally supplied Globals delegate. If an identifier cannot be found in 
 * the internal Collection, it will then check the externally supplied Globals delegate, if one has been set.
 * </p>
 * 
 * <p>Code snippet for setting a global:</p>
 * <pre>
 * StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
 * ksession.setGlobal( "hbnSession", hibernateSession ); // sets a global hibernate session, that can be used for DB interactions in the rules.
 * for( Object fact : facts ) {
 *     ksession.insert( fact );
 * }
 * ksession.fireAllRules(); // this will now execute and will be able to resolve the "hbnSession" identifier.
 * ksession.dispose();
 * </pre>
 * 
 * <p>
 * Like StatelessKnowledgeSession this also implements CommandExecutor which can be used to script a StatefulKnowledgeSession. See CommandExecutor
 * for more details.
 * </p>
 * 
 * @see org.drools.runtime.Globals
 */
public interface StatefulKnowledgeSession
    extends
    StatefulRuleSession,
    StatefulProcessSession,
    CommandExecutor,
    KnowledgeRuntime {

    int getId();
    
    /**
     * Releases all the current session resources, setting up the session for garbage collection.
     * This method <b>must</b> always be called after finishing using the session, or the engine
     * will not free the memory used by the session.
     */
    void dispose();

}
