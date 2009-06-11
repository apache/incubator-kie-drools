package org.drools.workflow.instance.impl;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.Serializable;

import org.drools.common.InternalAgenda;
import org.drools.definition.process.Connection;
import org.drools.process.instance.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.Node;
import org.drools.workflow.instance.node.SplitInstance;

/**
 * Default implementation of a constraint.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class RuleConstraintEvaluator implements Constraint,
        ConstraintEvaluator, Serializable {

    private static final long  serialVersionUID = 400L;

    private String             name;
    private String             constraint;
    private int                priority;
    private String             dialect;
    private String             type;       
    
    public String getConstraint() {
        return this.constraint;
    }

    public void setConstraint(final String constraint) {
        this.constraint = constraint;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(final int priority) {
        this.priority = priority;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public boolean evaluate(SplitInstance instance,
                            Connection connection,
                            Constraint constraint) {
        WorkflowProcessInstance processInstance = instance.getProcessInstance();
        InternalAgenda agenda = (InternalAgenda) ((ProcessInstance) processInstance).getAgenda();
        String rule = "RuleFlow-Split-" + processInstance.getProcessId() + "-" + 
        	((Node) instance.getNode()).getUniqueId() + "-" + 
        	((Node) connection.getTo()).getId() + "-" + connection.getToType();

        boolean isActive = agenda.isRuleActiveInRuleFlowGroup( "DROOLS_SYSTEM", rule, processInstance.getId() );
        return isActive;
    }

	public Object getMetaData(String name) {
		return null;
	}

	public void setMetaData(String name, Object value) {
		// Do nothing
	}    

}
