package org.drools.process.command;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.process.ProcessInstance;

public class GetProcessInstancesCommand
    implements
    Command<Collection<ProcessInstance>> {

    public Collection<ProcessInstance> execute(ReteooWorkingMemory session) {
        Collection<org.drools.process.instance.ProcessInstance> instances = session.getProcessInstances();
        Collection<ProcessInstance> result = new ArrayList<ProcessInstance>();

        for ( ProcessInstance instance : instances ) {
            result.add( instance );
        }

        return result;
    }

    public String toString() {
        return "session.getProcessInstances();";
    }

}
