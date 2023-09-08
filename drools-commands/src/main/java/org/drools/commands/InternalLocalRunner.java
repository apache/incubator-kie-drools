/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.commands;

import java.util.List;

import org.drools.commands.fluent.Batch;
import org.drools.commands.fluent.BatchImpl;
import org.drools.commands.fluent.InternalExecutable;
import org.kie.api.command.BatchExecutionCommand;
import org.kie.api.command.Command;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.Executable;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.RequestContext;
import org.kie.internal.command.RegistryContext;

import static java.util.Collections.singletonList;

public interface InternalLocalRunner extends ExecutableRunner<RequestContext> {
    default RequestContext execute(Executable executable) {
        return execute( executable, createContext() );
    }

    default <T> T execute( Command<T> command ) {
        RequestContext ctx = execute(new SingleCommandExecutable(command ));
        return command instanceof BatchExecutionCommand ?
               (T) ( (RegistryContext) ctx ).lookup( ExecutionResults.class ) :
               (T) ctx.getResult();
    }

    default <T> T execute( Command<T> command, Context ctx ) {
        execute( new SingleCommandExecutable( command ), (RequestContext) ctx );
        return command instanceof BatchExecutionCommand ?
               (T) ( (RegistryContext) ctx ).lookup( ExecutionResults.class ) :
               (T) ( (RequestContext) ctx ).getResult();
    }

    class SingleBatchExecutable implements InternalExecutable {
        private final Batch batch;

        public SingleBatchExecutable( Batch batch ) {
            this.batch = batch;
        }

        @Override
        public List<Batch> getBatches() {
            return singletonList( batch );
        }
    }

    class SingleCommandExecutable extends SingleBatchExecutable {

        public SingleCommandExecutable( Command command ) {
            super(new BatchImpl().addCommand( command ) );
        }
    }
}
