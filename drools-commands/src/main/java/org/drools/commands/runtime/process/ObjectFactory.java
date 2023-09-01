package org.drools.commands.runtime.process;


import javax.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory {

    public AbortWorkItemCommand createAbortWorkItemCommand() {
        return new AbortWorkItemCommand();
    }
    
    public CompleteWorkItemCommand createCompleteWorkItemCommand() {
        return new CompleteWorkItemCommand();
    }
    
    public CreateProcessInstanceCommand createCreateProcessInstanceCommand() {
        return new CreateProcessInstanceCommand();
    }

    public SignalEventCommand createSignalEventCommand() {
        return new SignalEventCommand();
    }
    
    public StartProcessCommand createStartProcessCommand() {
        return new StartProcessCommand();
    }

}
