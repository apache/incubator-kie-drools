package org.drools.process.command;

import java.util.Collection;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

public class GetWorkingMemoryEntryPointsCommand
    implements
    Command<Collection<? extends WorkingMemoryEntryPoint>> {

    public GetWorkingMemoryEntryPointsCommand() {
    }

    public Collection<? extends WorkingMemoryEntryPoint> execute(ReteooWorkingMemory session) {
        return session.getWorkingMemoryEntryPoints();
    }

    public String toString() {
        return "session.getWorkingMemoryEntryPoints( );";
    }
}
