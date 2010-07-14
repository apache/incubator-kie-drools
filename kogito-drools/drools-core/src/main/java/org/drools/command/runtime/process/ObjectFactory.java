package org.drools.command.runtime.process;


import javax.xml.bind.annotation.XmlRegistry;

import org.drools.command.runtime.process.AbortWorkItemCommand;
import org.drools.command.runtime.process.CompleteWorkItemCommand;
import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.command.runtime.process.StartProcessCommand;


@XmlRegistry
public class ObjectFactory {
    public AbortWorkItemCommand createAbortWorkItemCommand() {
        return new AbortWorkItemCommand();
    }
    
    public CompleteWorkItemCommand createCompleteWorkItemCommand() {
        return new CompleteWorkItemCommand();
    }
    
    public SignalEventCommand createSignalEventCommand() {
        return new SignalEventCommand();
    }
    
    public StartProcessCommand createStartProcessCommand() {
        return new StartProcessCommand();
    }
}
