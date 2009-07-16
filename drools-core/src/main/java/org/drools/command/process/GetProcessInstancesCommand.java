package org.drools.command.process;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

public class GetProcessInstancesCommand
    implements
    GenericCommand<Collection<ProcessInstance>> {

    public Collection<ProcessInstance> execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        Collection<ProcessInstance> instances = ksession.getProcessInstances();
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
