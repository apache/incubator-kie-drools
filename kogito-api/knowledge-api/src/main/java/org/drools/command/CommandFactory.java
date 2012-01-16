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

package org.drools.command;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;

/**
 * <p>
 * The CommandFactory returns Commands that can be used by classes that implement CommandExecutor. Typically more than one Command
 * will want to be executed, where is where the BatchExecution comes in, which takes a List of commands, think of it as CompositeCommand.
 * </p> 
 * 
 * <p>
 * Out of the box marshalling to XML is support for the Commands, specifically the BatchExecution command and ExecutionResults, using the Drools Pipeline. If the 
 * drools-pipeline module is added then the PipelineFactory can be used in conjunction with the BatchExecutionHelper to marshall to and from XML. BatchExecutionHelper
 * also provides additional documentation on the supported XML format.
 * </p>
 *`
 *
 * <p>This api is experimental and thus the classes and the interfaces returned are subject to change.</p>  
 * @BETA
 */
public class CommandFactory {
    private static volatile CommandFactoryService provider;

    /**
     * Inserts a new instance
     * 
     * @param object
     * @return
     */
    public static Command newInsert(Object object) {
        return getCommandFactoryProvider().newInsert( object );
    }

    /**
     * Inserts a new instance but references via the outIdentifier, which is returned as part of the ExecutionResults
     * 
     * @param object
     * @param outIdentifier
     * @return
     */
    public static Command newInsert(Object object,
                                    String outIdentifier) {
        return getCommandFactoryProvider().newInsert( object,
                                                      outIdentifier,
                                                      true,
                                                      null );
    }
    
    /**
     * Inserts a new instance but references via the outIdentifier, which is returned as part of the ExecutionResults
     * The outIdentifier can be null.
     * The entryPoint, which can also be null, specifies the entrypoint the object is inserted into.
     * 
     * @param object
     * @param outIdentifier
     * @param entryPoint
     * @return
     */
    public static Command newInsert(Object object,
                                    String outIdentifier,
                                    boolean returnObject,
                                    String entryPoint ) {
        return getCommandFactoryProvider().newInsert( object,
                                                      outIdentifier,
                                                      returnObject,
                                                      entryPoint );
    }

    /**
     * Iterate and insert each of the elements of the Collection.
     * 
    * 
     * @param objects
     *    The objects to insert
     * @return
     */
    public static Command newInsertElements(Collection objects) {
        return getCommandFactoryProvider().newInsertElements( objects );
    }
    
    /**
     * Iterate and insert each of the elements of the Collection.
     * 
     * @param objects
     *    The objects to insert
     * @param outIdentifier
     *    Identifier to lookup the returned objects
     * @param returnObject
     *    boolean to specify whether the inserted Collection is part of the ExecutionResults
     * @param entryPoint
     *    Optional EntryPoint for the insertions
     * @return
     */
    public static Command newInsertElements(Collection objects, String outIdentifier, boolean returnObject, String entryPoint) {
        return getCommandFactoryProvider().newInsertElements( objects, outIdentifier, returnObject, entryPoint );
    }

    public static Command newRetract(FactHandle factHandle) {
        return getCommandFactoryProvider().newRetract( factHandle );
    }

    public static Setter newSetter(String accessor,
                                   String value) {
        return getCommandFactoryProvider().newSetter( accessor,
                                                      value );
    }

    public static Command newModify(FactHandle factHandle,
                                    List<Setter> setters) {
        return getCommandFactoryProvider().newModify( factHandle,
                                                      setters );
    }

    public static Command newGetObject(FactHandle factHandle) {
        return getCommandFactoryProvider().newGetObject( factHandle );
    }

    public static Command newGetObject(FactHandle factHandle, String outIdentifier ) {
        return getCommandFactoryProvider().newGetObject( factHandle, outIdentifier );
    }

    public static Command newGetObjects() {
        return getCommandFactoryProvider().newGetObjects();
    }

    public static Command newGetObjects( String outIdentifier ) {
        return getCommandFactoryProvider().newGetObjects( outIdentifier );
    }

    public static Command newGetObjects(ObjectFilter filter) {
        return getCommandFactoryProvider().newGetObjects( filter );
    }

    public static Command newGetObjects(ObjectFilter filter, String outIdentifier ) {
        return getCommandFactoryProvider().newGetObjects( filter );
    }

    /**
     * Sets the global. Does not add the global to the ExecutionResults.
     * 
     * @param identifier
     *     The identifier of the global
     * @param object
     *     The instance to be set as the global.
     * @return
     */
    public static Command newSetGlobal(String identifier,
                                       Object object) {
        return getCommandFactoryProvider().newSetGlobal( identifier,
                                                         object );
    }

    /**
     * Sets the global but also when the out parameter is true specifies that the global is added to the ExecutionResults.
     *
     * @param identifier
     *     The identifier of the global
     * @param object
     *     The instance to be set as the global.
     * @param out
     *     When true the global will be added to the ExecutionResults using the global's identifier.
     * @return
     */
    public static Command newSetGlobal(String identifier,
                                       Object object,
                                       boolean out) {
        return getCommandFactoryProvider().newSetGlobal( identifier,
                                                         object,
                                                         out );
    }

    /**
     * Sets the global but also specifies that the global is added to the ExecutionResults. Instead of using the 
     * global's identifier it uses the outIdentifier when being added to the ExecutionResults.
     * 
     * @param identifier
     *     The identifier of the global
     * @param object
     *     The instance to be set as the global.
     * @param outIdentifier
     *     The identifier used to store the global in the ExecutionResults
     * @return
     */
    public static Command newSetGlobal(String identifier,
                                       Object object,
                                       String outIdentifier) {
        return getCommandFactoryProvider().newSetGlobal( identifier,
                                                         object,
                                                         outIdentifier );
    }

    /**
     * Gets the global and adds it to the ExecutionResults
     * @param identifier
     * @return
     */
    public static Command newGetGlobal(String identifier) {
        return getCommandFactoryProvider().newGetGlobal( identifier );
    }

    /**
     * Gets the global and adds it ot the BatchExecutionresults using the alternative outIdentifier.
     * 
     * @param identifier
     *     The identifier of the global
     * @param outIdentifier
     *     The identifier used in the ExecutionResults to store the global.
     * @return
     */
    public static Command newGetGlobal(String identifier,
                                       String outIdentifier) {
        return getCommandFactoryProvider().newGetGlobal( identifier,
                                                         outIdentifier );
    }

    public static Command newFireAllRules() {
        return getCommandFactoryProvider().newFireAllRules();
    }

    public static Command newFireAllRules(int max) {
        return getCommandFactoryProvider().newFireAllRules( max );
    }

    public static Command newFireAllRules(String outidentifier) {
        return getCommandFactoryProvider().newFireAllRules( outidentifier );
    }

    /**
     * Start a process
     * 
     * @param processId
     * @return
     */
    public static Command newStartProcess(String processId) {
        return getCommandFactoryProvider().newStartProcess( processId );
    }

    /**
     * Start a process using the given parameters.
     * 
     * @param processId
     * @param parameters
     * @return
     */
    public static Command newStartProcess(String processId,
                                          Map<String, Object> parameters) {
        return getCommandFactoryProvider().newStartProcess( processId );
    }

    public static Command newSignalEvent(String type,
                                         Object event) {
        return getCommandFactoryProvider().newSignalEvent( type,
                                                           event );
    }

    public static Command newSignalEvent(long processInstanceId,
                                         String type,
                                         Object event) {
        return getCommandFactoryProvider().newSignalEvent( processInstanceId,
                                                           type,
                                                           event );
    }

    public static Command newCompleteWorkItem(long workItemId,
                                              Map<String, Object> results) {
        return getCommandFactoryProvider().newCompleteWorkItem( workItemId,
                                                                results );
    }
    
    public static Command newAbortWorkItem(long workItemId) {
        return getCommandFactoryProvider().newAbortWorkItem( workItemId );
    }

    /**
     * Executes a query. The query results will be added to the ExecutionResults using the 
     * given identifier.
     * 
     * @param identifier
     *     The identifier to be used for the results when added to the ExecutionResults
     * @param name
     *     The name of the query to execute
     * @return
     */
    public static Command newQuery(String identifier,
                                   String name) {
        return getCommandFactoryProvider().newQuery( identifier,
                                                     name );

    }

    /**
     * Executes a query using the given parameters. The query results will be added to the 
     * ExecutionResults using the given identifier.
     * 
     * @param identifier
     *      The identifier to be used for the results when added to the ExecutionResults
     * @param name
     *      The name of the query to execute
     * @param arguments
     *     The arguments to be used for the query parameters
     * @return
     */
    public static Command newQuery(String identifier,
                                   String name,
                                   Object[] arguments) {
        return getCommandFactoryProvider().newQuery( identifier,
                                                     name,
                                                     arguments );
    }

    /**
     * This is a special composite command and will execute all the supplied commands in turn.
     * @param commands
     * @return
     */
    public static BatchExecutionCommand newBatchExecution(List< ? extends Command> commands) {
        return getCommandFactoryProvider().newBatchExecution( commands, null );
    }
    
    /**
     * 
     * @return
     */
    public static BatchExecutionCommand newBatchExecution(List< ? extends Command> commands, String lookup ) {
        return getCommandFactoryProvider().newBatchExecution( commands,
                                                              lookup);
    }

    private static synchronized void setCommandFactoryProvider(CommandFactoryService provider) {
        CommandFactory.provider = provider;
    }

    private static synchronized CommandFactoryService getCommandFactoryProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        try {
            Class<CommandFactoryService> cls = (Class<CommandFactoryService>) Class.forName( "org.drools.command.impl.CommandFactoryServiceImpl" );
            setCommandFactoryProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new RuntimeException( "Provider org.drools.command.impl.CommandFactoryProviderImpl could not be set.",
                                                       e2 );
        }
    }

    public static Command newKBuilderSetPropertyCommand(String id, String name, String value) {
        return getCommandFactoryProvider().newKBuilderSetPropertyCommand(id, name, value);
    }

    public static Command newNewKnowledgeBuilderConfigurationCommand(String localId) {
        return getCommandFactoryProvider().newNewKnowledgeBuilderConfigurationCommand(localId);
    }
    
    public static Command<FactHandle> fromExternalFactHandleCommand(String factHandleExternalForm) {
        return getCommandFactoryProvider().fromExternalFactHandleCommand(factHandleExternalForm);
    }

    public static Command<FactHandle> fromExternalFactHandleCommand(String factHandleExternalForm, boolean disconnected) {
        return getCommandFactoryProvider().fromExternalFactHandleCommand(factHandleExternalForm, disconnected);
    }
}
