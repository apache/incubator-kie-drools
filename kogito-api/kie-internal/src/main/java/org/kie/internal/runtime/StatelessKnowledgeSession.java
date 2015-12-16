/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.runtime;

import org.kie.internal.event.KnowledgeRuntimeEventManager;
import org.kie.api.runtime.StatelessKieSession;


/**
 * StatelessKnowledgeSession provides a convenience API, wrapping StatefulKnowledgeSession. It avoids the need to
 * call dispose(). Stateless sessions do not support
 * iterative invocations, the act of calling execute(...) is a single
 * shot method that will internally instantiate a StatefulKnowledgeSession, add all the user data and execute user commands, call fireAllRules, and then
 * call dispose(). While the main way to work with this class is via the BatchExecution Command as supported by the CommandExecutor interface, 
 * two convenience methods are provided for when simple object insertion is all that's required.
 * 
 * <p>
 * Simple example showing a stateless session executing for a given collection of java objects using the convenience api. It will iterate the collection inserting
 * each element in turn
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
 * ksession.execute( collection );
 * </pre>
 * 
 * <p>
 * If this was done as a single Command it would be as follows:
 * </p>
 * <pre>
 * ksession.execute( CommandFactory.newInsertElements( collection ) );
 * </pre>
 * 
 * <p>
 * Note if you wanted to insert the collection itself, and not the iterate and insert the elements, then CommandFactory.newInsert( collection ) would do the job.  
 * </p>
 * 
 * <p>
 * The CommandFactory details the supported commands, all of which can be marshalled using XStream and the BatchExecutionHelper. BatchExecutionHelper provides details
 * on the XML format as well as how to use Drools Pipeline to automate the marshalling of BatchExecution and ExecutionResults.
 * </p>
 * 
 * <p>
 * StatelessKnowledgeSessions support globals, scoped in a number of ways. I'll cover the non-command way first,
 * as commands are scoped to a specific execution call. Globals can be resolved in three ways. The StatelessKnowledgeSession 
 * supports getGlobals(), which returns a Globals instance. These globals are shared for ALL execution calls, so be especially careful of mutable
 * globals in these cases - as often execution calls can be executing simultaneously in different threads. Globals also supports a delegate, which 
 * adds a second way of resolving globals. Calling of setGlobal(String, Object) will actually be set on an internal Collection, identifiers in this internal 
 * Collection will have priority over supplied delegate, if one is added. If an identifier cannot be found in 
 * the internal Collection, it will then check the delegate Globals, if one has been set.
 * </p>
 * 
 * <p>Code snippet for setting a session scoped global:</p>
 * <pre>
 * StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
 * ksession.setGlobal( "hbnSession", hibernateSession ); // sets a global hibernate session, that can be used for DB interactions in the rules.
 * ksession.execute( collection ); // this will now execute and will be able to resolve the "hbnSession" identifier.
 * </pre>
 * 
 * <p>
 * The third way is execution scopped globals using the CommandExecutor and SetGlobal Commands:
 * </p>
 * 
 * <pre>
 * List&lt;Command&gt; cmds = new ArrayList&lt;Command&gt;();
 * cmds.add( CommandFactory.newSetGlobal( "list1", new ArrayList() ) );
 * cmds.add( CommandFactory.newInsert( new Person( "jon", 102 ) ) );
 * 
 * ksession.execute( CommandFactory.newBatchExecution( cmds ) );
 * </pre>
 * 
 * <p>
 * The CommandExecutor interface also supports the ability to export data via "out" parameters. Inserted facts, globals and query results can all be returned.
 * </p>
 * 
 * <pre>
 * List&lt;Command&gt; cmds = new ArrayList&lt;Command&gt;();
 * cmds.add( CommandFactory.newSetGlobal( "list1", new ArrayList(), true ) );
 * cmds.add( CommandFactory.newInsert( new Person( "jon", 102 ), "person" ) );
 * cmds.add( CommandFactory.newQuery( "Get People" "getPeople" );
 * 
 * ExecutionResults results = ksession.execute( CommandFactory.newBatchExecution( cmds ) );
 * results.getValue( "list1" ); // returns the ArrayList
 * results.getValue( "person" ); // returns the inserted fact Person
 * results.getValue( "Get People" );// returns the query as a QueryResults instance.
 * </pre>
 * 
 * 
 * @see org.kie.api.runtime.CommandExecutor
 * @see org.kie.internal.command.CommandFactory
 * @see org.kie.api.command.BatchExecutionCommand
 * @see org.kie.internal.runtime.helper.BatchExecutionHelper
 * @see org.kie.api.runtime.Globals
 */
public interface StatelessKnowledgeSession
    extends
        StatelessKieSession, KnowledgeRuntimeEventManager {

    
}
