package org.drools.command.impl;

import org.drools.command.CommandService;
import org.drools.command.runtime.GetFactCountInEntryPointCommand;
import org.drools.command.runtime.rule.GetFactHandleInEntryPointCommand;
import org.drools.command.runtime.rule.GetFactHandlesInEntryPointCommand;
import org.drools.command.runtime.rule.GetObjectInEntryPointCommand;
import org.drools.command.runtime.rule.GetObjectsInEntryPointCommand;
import org.drools.command.runtime.rule.InsertObjectCommand;
import org.drools.command.runtime.rule.InsertObjectInEntryPointCommand;
import org.drools.command.runtime.rule.RetractFromEntryPointCommand;
import org.drools.command.runtime.rule.UpdateInEntryPointCommand;
import org.drools.runtime.ObjectFilter;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

import java.util.Collection;

public class CommandBasedWorkingMemoryEntryPoint implements WorkingMemoryEntryPoint {

    private final CommandService commandService;
    private final String entryPoint;

    public CommandBasedWorkingMemoryEntryPoint(CommandService commandService, String entryPoint) {
        this.commandService = commandService;
        this.entryPoint = entryPoint;
    }

    public String getEntryPointId() {
        return entryPoint;
    }

    public FactHandle insert(Object object) {
        return commandService.execute( new InsertObjectInEntryPointCommand( object, entryPoint ) );
    }

    public void retract(FactHandle handle) {
        commandService.execute( new RetractFromEntryPointCommand( handle, entryPoint ) );
    }

    public void update(FactHandle handle, Object object) {
        commandService.execute( new UpdateInEntryPointCommand( handle, object, entryPoint ) );
    }

    public FactHandle getFactHandle(Object object) {
        return commandService.execute( new GetFactHandleInEntryPointCommand(object, entryPoint) );
    }

    public Object getObject(FactHandle factHandle) {
        return commandService.execute( new GetObjectInEntryPointCommand(factHandle, entryPoint) );
    }

    public Collection<Object> getObjects() {
        return commandService.execute( new GetObjectsInEntryPointCommand(null, entryPoint) );
    }

    public Collection<Object> getObjects(ObjectFilter filter) {
        return commandService.execute( new GetObjectsInEntryPointCommand(filter, entryPoint) );
    }

    public <T extends FactHandle> Collection<T> getFactHandles() {
        return (Collection<T>) commandService.execute( new GetFactHandlesInEntryPointCommand(entryPoint) );
    }

    public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        return (Collection<T>) commandService.execute( new GetFactHandlesInEntryPointCommand(entryPoint, filter) );
    }

    public long getFactCount() {
        return commandService.execute( new GetFactCountInEntryPointCommand(entryPoint) );
    }
}
