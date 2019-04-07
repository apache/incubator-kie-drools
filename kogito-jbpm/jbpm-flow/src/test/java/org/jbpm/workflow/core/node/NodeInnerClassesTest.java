/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.workflow.core.node;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.test.TestProcessEventListener;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.slf4j.LoggerFactory;

public class NodeInnerClassesTest extends AbstractBaseTest {

	@Override
	public void addLogger() {
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	@Test
	public void testNodeReading() {

		RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.event");
        process.setName("Event Process");
        
        List<Variable> variables = new ArrayList<Variable>();
        Variable variable = new Variable();
        variable.setName("event");
        ObjectDataType personDataType = new ObjectDataType();
        personDataType.setClassName("org.drools.Person");
        variable.setType(personDataType);
        variables.add(variable);
        process.getVariableScope().setVariables(variables);

        process.setDynamic(true);
        CompositeNode compositeNode = new CompositeNode();
        compositeNode.setName("CompositeNode");
        compositeNode.setId(2);
        
        ForEachNode forEachNode = new ForEachNode();
        ForEachNode.ForEachSplitNode split = new ForEachNode.ForEachSplitNode();
        split.setName("ForEachSplit");
        split.setMetaData("hidden", true);
        split.setMetaData("UniqueId", forEachNode.getMetaData("Uniqueid") + ":foreach:split");
        forEachNode.internalAddNode(split);
        forEachNode.linkIncomingConnections(
            org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE, 
            new CompositeNode.NodeAndType(split, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE));

        process.addNode(forEachNode);
        KieSession ksession = createKieSession(process); 
        TestProcessEventListener procEventListener = new TestProcessEventListener();
        ksession.addEventListener(procEventListener);
        
        ProcessInstance processInstance = ksession.startProcess("org.drools.core.process.event");
        Assert.assertNotNull(processInstance);
	}

}
