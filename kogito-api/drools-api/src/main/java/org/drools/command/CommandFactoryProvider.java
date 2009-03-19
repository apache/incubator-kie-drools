package org.drools.command;

import java.util.List;
import java.util.Map;

import org.drools.runtime.ObjectFilter;

public interface CommandFactoryProvider {
    Command newInsertObject(Object object);

    Command newInsertObject(Object object,
                            String outIdentifier);

    Command newInsertElements(Iterable iterable);
    
    Command newFireAllRules();

    Command newFireAllRules(int max);    
    
	Command newGetObjects();

    public Command newGetObjects(ObjectFilter filter);
    
    Command newSetGlobal(String identifie,
                         Object object);

    Command newSetGlobal(String identifier,
                         Object object,
                         boolean out);

    Command newSetGlobal(String identifier,
                         Object object,
                         String outIdentifier);

    Command newGetGlobal(String identifier);

    Command newGetGlobal(String identifier,
                         String outIdentifier);

    Command newStartProcess(String processId);

    Command newStartProcess(String processId,
                            Map<String, Object> parameters);

    Command newQuery(String identifier,
                     String name);

    Command newQuery(String identifier,
                     String name,
                     Object[] arguments);
    
    Command newBatchExecution(List<? extends Command> commands);
		
}
