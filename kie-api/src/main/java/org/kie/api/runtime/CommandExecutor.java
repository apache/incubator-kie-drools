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

package org.kie.api.runtime;

import org.kie.api.command.Command;

/**
 * <p>
 * Batch Executor allows for the scripting of a KieSession using Commands, both the KieSession and KieSession
 * implement this interface.
 * </p>
 *
 * <p>
 * Commands are created using the CommandFactory and executed using the "execute" method, such as the following insert Command:
 * <p>
 *
 * <pre>
 * ksession.execute( CommandFactory.newInsert( person ) );
 * </pre>
 *
 * <p>
 * Typically though you will want to execute a batch of commands, this can be achieved via the composite Command BatchExecution. Further to this
 * results are scoped to this execute call and return via the ExecutionResults:
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
 * <p>
 * The CommandFactory details the supported commands, all of which can marshalled using XStream and the BatchExecutionHelper. BatchExecutionHelper provides details
 * on the xml format as well as how to use Drools Pipeline to automate the marshalling of BatchExecution and ExecutionResults.
 * </p>
 */
public interface CommandExecutor {
    /**
     * Execute the command and return a ExecutionResults for the results of the Command.
     *
     * @param command
     * @return execution result
     */
    <T> T execute(Command<T> command);
}
