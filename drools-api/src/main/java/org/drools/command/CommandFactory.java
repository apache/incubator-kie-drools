package org.drools.command;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.ProviderInitializationException;

public class CommandFactory {
    private static volatile CommandFactoryProvider provider;

    public static Command newInsertObject(Object object) {
        return getCommandFactoryProvider().newInsertObject( object );
    }

    public static Command newInsertObject(Object object,
                                          String outIdentifier) {
        return getCommandFactoryProvider().newInsertObject( object,
                                                            outIdentifier );
    }

    public static Command newInsertElements(Collection collection) {
        return getCommandFactoryProvider().newInsertElements( collection );
    }

    public static Command newSetGlobal(String identifier,
                                       Object object) {
        return getCommandFactoryProvider().newSetGlobal( identifier,
                                                         object );
    }

    public static Command newSetGlobal(String identifier,
                                       Object object,
                                       boolean out) {
        return getCommandFactoryProvider().newSetGlobal( identifier,
                                                         object,
                                                         out );
    }

    public static Command newSetGlobal(String identifier,
                                       Object object,
                                       String outIdentifier) {
        return getCommandFactoryProvider().newSetGlobal( identifier,
                                                         object,
                                                         outIdentifier );
    }

    public static Command newGetGlobal(String identifier) {
        return getCommandFactoryProvider().newGetGlobal( identifier );
    }

    public static Command newGetGlobal(String identifier,
                                       String outIdentifier) {
        return getCommandFactoryProvider().newGetGlobal( identifier,
                                                         outIdentifier );
    }

    public static Command newStartProcess(String processId) {
        return getCommandFactoryProvider().newStartProcess( processId );
    }

    public static Command newStartProcess(String processId,
                                          Map<String, Object> parameters) {
        return getCommandFactoryProvider().newStartProcess( processId );
    }
    
    public static Command newQuery(String identifier,
                                    String name) {
        return getCommandFactoryProvider().newQuery( identifier, name );
        
    }
    
    public static Command newQuery(String identifier,
                                    String name,
                                    Object[] arguments) {
        return getCommandFactoryProvider().newQuery( identifier, name, arguments );  
    }    
    
    public static Command newBatchExecution(List<? extends Command> commands) {
        return getCommandFactoryProvider().newBatchExecution( commands );
    }

    private static synchronized void setCommandFactoryProvider(CommandFactoryProvider provider) {
        CommandFactory.provider = provider;
    }

    private static synchronized CommandFactoryProvider getCommandFactoryProvider() {
        if ( provider == null ) {
            loadProvider();
        }
        return provider;
    }

    private static void loadProvider() {
        try {
            Class<CommandFactoryProvider> cls = (Class<CommandFactoryProvider>) Class.forName( "org.drools.command.impl.CommandFactoryProviderImpl" );
            setCommandFactoryProvider( cls.newInstance() );
        } catch ( Exception e2 ) {
            throw new ProviderInitializationException( "Provider org.drools.command.impl.CommandFactoryProviderImpl could not be set.",
                                                       e2 );
        }
    }
}
