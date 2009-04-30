package org.drools.command;

import java.util.List;
import java.util.Map;

import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;

public interface CommandFactoryProvider {
    Command newInsert(Object object);

    Command newInsert(Object object,
                      String outIdentifier);

    Command newInsertElements(Iterable iterable);

    Command newRetract(FactHandle factHandle);

    Setter newSetter(String accessor,
                     String value);

    Command newModify(FactHandle factHandle,
                      List<Setter> setters);

    Command newFireAllRules();

    Command newFireAllRules(int max);

    Command newGetObject(FactHandle factHandle);

    Command newGetObjects();

    Command newGetObjects(ObjectFilter filter);

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

    Command newSignalEvent(String type,
                           Object event);

    Command newSignalEvent(long processInstanceId,
                           String type,
                           Object event);

    Command newCompleteWorkItem(long workItemId,
                                Map<String, Object> results);

    Command newQuery(String identifier,
                     String name);

    Command newQuery(String identifier,
                     String name,
                     Object[] arguments);

    Command newBatchExecution(List< ? extends Command> commands);

    Command newAbortWorkItem(long workItemId);

}
