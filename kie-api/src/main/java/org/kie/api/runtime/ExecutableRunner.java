package org.kie.api.runtime;

import org.kie.api.command.Command;

public interface ExecutableRunner<C extends Context> extends CommandExecutor {
    C execute( Executable executable );

    C execute( Executable executable, C ctx );

    <T> T execute( Command<T> command, Context ctx );

    C createContext();

    static ExecutableRunner<RequestContext> create() {
        try {
            return (ExecutableRunner) Class.forName( "org.drools.commands.fluent.PseudoClockRunner" ).getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance ExecutableRunner, please add org.drools:drools-commands to your classpath", e);
        }
    }

    static ExecutableRunner<RequestContext> create(long startTime) {
        try {
            return (ExecutableRunner) Class.forName( "org.drools.commands.fluent.PseudoClockRunner" ).getConstructor( long.class ).newInstance(startTime);
        } catch (Exception e) {
            throw new RuntimeException("Unable to instance ExecutableRunner, please add org.drools:drools-commands to your classpath", e);
        }
    }
}
