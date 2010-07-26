/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        parameterDefinitions.add(new ParameterDefinitionImpl("Content", new StringDataType()));
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
