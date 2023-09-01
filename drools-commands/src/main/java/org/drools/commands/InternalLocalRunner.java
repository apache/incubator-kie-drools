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
