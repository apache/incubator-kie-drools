package org.drools.workflow.core.node;

import java.util.HashSet;
import java.util.Set;

import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.Work;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.drools.process.core.impl.WorkImpl;

public class HumanTaskNode extends WorkItemNode {

    private static final long serialVersionUID = 4L;

    private String swimlane;
    
    public HumanTaskNode() {
        Work work = new WorkImpl();
        work.setName("Human Task");
        Set<ParameterDefinition> parameterDefinitions = new HashSet<ParameterDefinition>();
        parameterDefinitions.add(new ParameterDefinitionImpl("TaskName", new StringDataType()));
        parameterDefinitions.add(new ParameterDefinitionImpl("ActorId", new StringDataType()));
        parameterDefinitions.add(new ParameterDefinitionImpl("Priority", new StringDataType()));
        parameterDefinitions.add(new ParameterDefinitionImpl("Comment", new StringDataType()));
        parameterDefinitions.add(new ParameterDefinitionImpl("Skippable", new StringDataType()));
        // TODO: initiator
        // TODO: attachments
        // TODO: deadlines
        // TODO: delegates
        // TODO: recipients
        // TODO: ...
        work.setParameterDefinitions(parameterDefinitions);
        setWork(work);
    }

    public String getSwimlane() {
        return swimlane;
    }

    public void setSwimlane(String swimlane) {
        this.swimlane = swimlane;
    }

}
