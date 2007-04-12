package org.drools.ruleflow.instance;

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

import java.util.Collection;

import org.drools.Agenda;
import org.drools.ruleflow.common.instance.IProcessInstance;
import org.drools.ruleflow.core.INode;
import org.drools.ruleflow.core.IRuleFlowProcess;

/**
 * A process instance for a RuleFlow process.
 * Contains a reference to all its node instances, and the agenda that
 * is controlling the RuleFlow process.
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public interface IRuleFlowProcessInstance
    extends
    IProcessInstance {

    IRuleFlowProcess getRuleFlowProcess();

    void addNodeInstance(IRuleFlowNodeInstance nodeInstance);

    void removeNodeInstance(IRuleFlowNodeInstance nodeInstance);

    Collection getNodeInstances();

    IRuleFlowNodeInstance getFirstNodeInstance(long nodeId);

    void setAgenda(Agenda agenda);

    Agenda getAgenda();

    IRuleFlowNodeInstance getNodeInstance(INode node);

    void start();

}