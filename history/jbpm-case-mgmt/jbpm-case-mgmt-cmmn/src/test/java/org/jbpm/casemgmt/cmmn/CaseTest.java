/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.cmmn;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.jbpm.process.core.context.variable.Variable;
import org.jbpm.process.core.datatype.impl.type.BooleanDataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.node.DynamicNode;
import org.jbpm.workflow.core.node.HumanTaskNode;
import org.jbpm.workflow.core.node.MilestoneNode;
import org.jbpm.workflow.core.node.RuleSetNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.Process;

public class CaseTest extends AbstractCmmnBaseTest {

    @Test
    public void testLoadMinimalCase() throws Exception {
        
        KieBase kbase = createKnowledgeBase("CMMN-MinimalCase.cmmn");
        assertThat(kbase).isNotNull();
        
        Process process = kbase.getProcess("loan_application");
        assertThat(process).isNotNull();
        assertThat(process).isInstanceOf(RuleFlowProcess.class);
        RuleFlowProcess caseProcess = (RuleFlowProcess) process;
        assertThat(caseProcess.isDynamic()).isTrue();
        assertThat(caseProcess.getId()).isEqualTo("loan_application");
        assertThat(caseProcess.getName()).isEqualTo("Loan Application");
    }
    
    @Test
    public void testLoadMinimalCaseWithRoles() throws Exception {
        
        KieBase kbase = createKnowledgeBase("CMMN-MinimalCaseWithRoles.cmmn");
        assertThat(kbase).isNotNull();
        
        Process process = kbase.getProcess("loan_application");
        assertThat(process).isNotNull();
        assertThat(process).isInstanceOf(RuleFlowProcess.class);
        RuleFlowProcess caseProcess = (RuleFlowProcess) process;
        assertThat(caseProcess.isDynamic()).isTrue();
        assertThat(caseProcess.getId()).isEqualTo("loan_application");
        assertThat(caseProcess.getName()).isEqualTo("Loan Application");
        
        String roles = (String) caseProcess.getMetaData("customCaseRoles");
        assertThat(roles).isNotNull();
        assertThat(roles).isEqualTo("owner,participant,manager");
    }
    
    @Test
    public void testLoadMinimalCaseHumanTasks() throws Exception {
        
        KieBase kbase = createKnowledgeBase("CMMN-HumanTaskCase.cmmn");
        assertThat(kbase).isNotNull();
        
        Process process = kbase.getProcess("loan_application");
        assertThat(process).isNotNull();
        assertThat(process).isInstanceOf(RuleFlowProcess.class);
        RuleFlowProcess caseProcess = (RuleFlowProcess) process;
        assertThat(caseProcess.isDynamic()).isTrue();
        assertThat(caseProcess.getId()).isEqualTo("loan_application");
        assertThat(caseProcess.getName()).isEqualTo("Loan Application");
        
        String roles = (String) caseProcess.getMetaData("customCaseRoles");
        assertThat(roles).isNotNull();
        assertThat(roles).isEqualTo("owner,manager");
        
        Node[] nodes = caseProcess.getNodes();
        assertThat(nodes).hasSize(2);
        assertThat(nodes).allSatisfy(n -> assertThat(n).isInstanceOf(HumanTaskNode.class));
        
        HumanTaskNode humanTaskOne = (HumanTaskNode) nodes[0];
        assertThat(humanTaskOne.getName()).isEqualTo("Check Application");
        assertThat(humanTaskOne.getWork().getName()).isEqualTo("Human Task");
        assertThat(humanTaskOne.getWork().getParameter("ActorId")).isEqualTo("owner");
        assertThat(humanTaskOne.getWork().getParameter("TaskName")).isEqualTo("CheckApplication");
        
        HumanTaskNode humanTaskTwo = (HumanTaskNode) nodes[1];
        assertThat(humanTaskTwo.getName()).isEqualTo("Provide Customer Rating");
        assertThat(humanTaskTwo.getWork().getName()).isEqualTo("Human Task");
        assertThat(humanTaskTwo.getWork().getParameter("ActorId")).isEqualTo("manager");
        assertThat(humanTaskTwo.getWork().getParameter("TaskName")).isEqualTo("ProvideCustomerRating");
    }
    
    @Test
    public void testLoadMilestoneAndHumanTasksCase() throws Exception {
        
        KieBase kbase = createKnowledgeBase("CMMN-MilestoneAndHumanTasksCase.cmmn");
        assertThat(kbase).isNotNull();
        
        Process process = kbase.getProcess("Case_517ac74c-fba4-4728-bcaf-e9853c864710");
        assertThat(process).isNotNull();
        assertThat(process).isInstanceOf(RuleFlowProcess.class);
        RuleFlowProcess caseProcess = (RuleFlowProcess) process;
        assertThat(caseProcess.isDynamic()).isTrue();
        assertThat(caseProcess.getId()).isEqualTo("Case_517ac74c-fba4-4728-bcaf-e9853c864710");
        assertThat(caseProcess.getName()).isEqualTo("First case");
        
        List<Variable> variables = caseProcess.getVariableScope().getVariables();
        assertThat(variables).hasSize(1);
        
        Variable caseFileItem = variables.get(0);
        assertThat(caseFileItem).isNotNull();
        assertThat(caseFileItem.getName()).isEqualTo("caseFile_document");
        assertThat(caseFileItem.getType()).isInstanceOf(ObjectDataType.class);
        
        Node[] nodes = caseProcess.getNodes();
        assertThat(nodes).hasSize(3);
        
        HumanTaskNode humanTaskOne = (HumanTaskNode) nodes[0];
        assertThat(humanTaskOne.getName()).isEqualTo("First task");
        assertThat(humanTaskOne.getWork().getName()).isEqualTo("Human Task");
        
        assertThat(humanTaskOne.getInAssociations()).hasSize(1);
        assertThat(humanTaskOne.getInAssociations().get(0).getSources()).hasSize(1);
        assertThat(humanTaskOne.getInAssociations().get(0).getSources().get(0)).isEqualTo("caseFile_document");
        assertThat(humanTaskOne.getInAssociations().get(0).getTarget()).isEqualTo("test"); 
        assertThat(humanTaskOne.getOutAssociations()).hasSize(0);
  
        MilestoneNode humanTaskTwo = (MilestoneNode) nodes[1];
        assertThat(humanTaskTwo.getName()).isEqualTo("Milestone");
        assertThat(humanTaskTwo.getConstraint()).isEqualTo("org.kie.api.runtime.process.CaseData(data.get(\"shipped\") !=null)");
        
        DynamicNode stage = (DynamicNode) nodes[2];
        assertThat(stage.getName()).isEqualTo("First stage");
        assertThat(stage.getActivationExpression()).isNull();
        assertThat(stage.getCompletionExpression()).isNull();
        
    }
    
    @Test
    public void testLoadMilestoneAndHumanTasksSentriesCase() throws Exception {
        
        KieBase kbase = createKnowledgeBase("CMMN-HumanTaskMilestoneSentries.cmmn");
        assertThat(kbase).isNotNull();
        
        Process process = kbase.getProcess("Case_517ac74c-fba4-4728-bcaf-e9853c864710");
        assertThat(process).isNotNull();
        assertThat(process).isInstanceOf(RuleFlowProcess.class);
        RuleFlowProcess caseProcess = (RuleFlowProcess) process;
        assertThat(caseProcess.isDynamic()).isTrue();
        assertThat(caseProcess.getId()).isEqualTo("Case_517ac74c-fba4-4728-bcaf-e9853c864710");
        assertThat(caseProcess.getName()).isEqualTo("First case");
        
        String roles = (String) caseProcess.getMetaData("customCaseRoles");
        assertThat(roles).isNotNull();
        assertThat(roles).isEqualTo("owner,manager");
        
        Node[] nodes = caseProcess.getNodes();
        assertThat(nodes).hasSize(2);
        
        HumanTaskNode humanTaskOne = (HumanTaskNode) nodes[0];
        assertThat(humanTaskOne.getName()).isEqualTo("First task");
        assertThat(humanTaskOne.getWork().getName()).isEqualTo("Human Task");
        assertThat(humanTaskOne.getMetaData("customAutoStart")).isEqualTo("true");
        assertThat(humanTaskOne.getWork().getParameter("ActorId")).isEqualTo("owner");
        
        MilestoneNode milestone = (MilestoneNode) nodes[1];
        assertThat(milestone.getName()).isEqualTo("All work done");
        assertThat(milestone.getConstraint()).isEqualTo("org.kie.api.runtime.process.CaseData(data.get(\"shipped\") !=null)");
        assertThat(milestone.getMetaData("customAutoStart")).isEqualTo("true");
    }
    
    
    @Test
    public void testLoadStageAndHumanTasksCase() throws Exception {
        
        KieBase kbase = createKnowledgeBase("CMMN-StageWithHumanTaskCase.cmmn");
        assertThat(kbase).isNotNull();
        
        Process process = kbase.getProcess("Case_517ac74c-fba4-4728-bcaf-e9853c864710");
        assertThat(process).isNotNull();
        assertThat(process).isInstanceOf(RuleFlowProcess.class);
        RuleFlowProcess caseProcess = (RuleFlowProcess) process;
        assertThat(caseProcess.isDynamic()).isTrue();
        assertThat(caseProcess.getId()).isEqualTo("Case_517ac74c-fba4-4728-bcaf-e9853c864710");
        assertThat(caseProcess.getName()).isEqualTo("First case");
        
        String roles = (String) caseProcess.getMetaData("customCaseRoles");
        assertThat(roles).isNotNull();
        assertThat(roles).isEqualTo("owner,manager");
        
        Node[] nodes = caseProcess.getNodes();
        assertThat(nodes).hasSize(2);
        
        HumanTaskNode humanTaskOne = (HumanTaskNode) nodes[0];
        assertThat(humanTaskOne.getName()).isEqualTo("First task");
        assertThat(humanTaskOne.getWork().getName()).isEqualTo("Human Task");
        assertThat(humanTaskOne.getMetaData("customAutoStart")).isEqualTo("true");
        assertThat(humanTaskOne.getWork().getParameter("ActorId")).isEqualTo("owner");
        
        DynamicNode stage = (DynamicNode) nodes[1];
        assertThat(stage.getName()).isEqualTo("First stage");
        assertThat(stage.getActivationExpression()).isEqualTo("org.kie.api.runtime.process.CaseData(data.get(\"ordered\") !=null)");
        assertThat(stage.getCompletionExpression()).isEqualTo("org.kie.api.runtime.process.CaseData(data.get(\"shipped\") !=null)");
        
        Node[] stageNodes = stage.getNodes();
        assertThat(stageNodes).hasSize(1);
        
        HumanTaskNode humanTaskTwo = (HumanTaskNode) stageNodes[0];
        assertThat(humanTaskTwo.getName()).isEqualTo("Stage task");
        assertThat(humanTaskTwo.getWork().getName()).isEqualTo("Human Task");
        assertThat(humanTaskTwo.getMetaData("customAutoStart")).isNull();
        assertThat(humanTaskTwo.getWork().getParameter("ActorId")).isEqualTo("manager");
    }
    
    @Test
    public void testLoadDataInputsAndOutputsHumanTasksCase() throws Exception {
        
        KieBase kbase = createKnowledgeBase("CMMN-DataInputsAndOutputsCase.cmmn");
        assertThat(kbase).isNotNull();
        
        Process process = kbase.getProcess("Case_517ac74c-fba4-4728-bcaf-e9853c864710");
        assertThat(process).isNotNull();
        assertThat(process).isInstanceOf(RuleFlowProcess.class);
        RuleFlowProcess caseProcess = (RuleFlowProcess) process;
        assertThat(caseProcess.isDynamic()).isTrue();
        assertThat(caseProcess.getId()).isEqualTo("Case_517ac74c-fba4-4728-bcaf-e9853c864710");
        assertThat(caseProcess.getName()).isEqualTo("First case");
        
        List<Variable> variables = caseProcess.getVariableScope().getVariables();
        assertThat(variables).hasSize(2);
        
        Variable caseFileItem = variables.get(0);
        assertThat(caseFileItem).isNotNull();
        assertThat(caseFileItem.getName()).isEqualTo("caseFile_shipped");
        assertThat(caseFileItem.getType()).isInstanceOf(BooleanDataType.class);
        
        caseFileItem = variables.get(1);
        assertThat(caseFileItem).isNotNull();
        assertThat(caseFileItem.getName()).isEqualTo("caseFile_invoice");
        assertThat(caseFileItem.getType()).isInstanceOf(ObjectDataType.class);
        
        Node[] nodes = caseProcess.getNodes();
        assertThat(nodes).hasSize(2);
        
        HumanTaskNode humanTaskOne = (HumanTaskNode) nodes[0];
        assertThat(humanTaskOne.getName()).isEqualTo("First task");
        assertThat(humanTaskOne.getWork().getName()).isEqualTo("Human Task");
        
        assertThat(humanTaskOne.getInAssociations()).hasSize(1);
        assertThat(humanTaskOne.getInAssociations().get(0).getSources()).hasSize(1);
        assertThat(humanTaskOne.getInAssociations().get(0).getSources().get(0)).isEqualTo("caseFile_invoice");
        assertThat(humanTaskOne.getInAssociations().get(0).getTarget()).isEqualTo("invoice");        
        
        DynamicNode stage = (DynamicNode) nodes[1];
        assertThat(stage.getName()).isEqualTo("First stage");
        assertThat(stage.getActivationExpression()).isEqualTo("org.kie.api.runtime.process.CaseData(data.get(\"ordered\") !=null)");
        assertThat(stage.getCompletionExpression()).isEqualTo("org.kie.api.runtime.process.CaseData(data.get(\"shipped\") !=null)");
        
        Node[] stageNodes = stage.getNodes();
        assertThat(stageNodes).hasSize(1);
        
        humanTaskOne = (HumanTaskNode) stageNodes[0];
        assertThat(humanTaskOne.getName()).isEqualTo("Stage task");
        assertThat(humanTaskOne.getWork().getName()).isEqualTo("Human Task");
        
        assertThat(humanTaskOne.getInAssociations()).hasSize(2);
        assertThat(humanTaskOne.getInAssociations().get(0).getSources()).hasSize(1);
        assertThat(humanTaskOne.getInAssociations().get(0).getSources().get(0)).isEqualTo("caseFile_shipped");
        assertThat(humanTaskOne.getInAssociations().get(0).getTarget()).isEqualTo("shipped");
        
        assertThat(humanTaskOne.getInAssociations().get(1).getSources()).hasSize(1);
        assertThat(humanTaskOne.getInAssociations().get(1).getSources().get(0)).isEqualTo("caseFile_invoice");
        assertThat(humanTaskOne.getInAssociations().get(1).getTarget()).isEqualTo("invoiceApproved");
                
        assertThat(humanTaskOne.getOutAssociations()).hasSize(1);
        assertThat(humanTaskOne.getOutAssociations().get(0).getSources()).hasSize(1);
        assertThat(humanTaskOne.getOutAssociations().get(0).getSources().get(0)).isEqualTo("shippedConfirmed");
        assertThat(humanTaskOne.getOutAssociations().get(0).getTarget()).isEqualTo("caseFile_shipped");        
    }
    
    @Test
    public void testLoadDifferentTasksCase() throws Exception {
        
        KieBase kbase = createKnowledgeBase("CMMN-DifferentTaskTypesCase.cmmn");
        assertThat(kbase).isNotNull();
        
        Process process = kbase.getProcess("Case_517ac74c-fba4-4728-bcaf-e9853c864710");
        assertThat(process).isNotNull();
        assertThat(process).isInstanceOf(RuleFlowProcess.class);
        RuleFlowProcess caseProcess = (RuleFlowProcess) process;
        assertThat(caseProcess.isDynamic()).isTrue();
        assertThat(caseProcess.getId()).isEqualTo("Case_517ac74c-fba4-4728-bcaf-e9853c864710");
        assertThat(caseProcess.getName()).isEqualTo("First case");
        
        List<Variable> variables = caseProcess.getVariableScope().getVariables();
        assertThat(variables).hasSize(1);
        
        Variable caseFileItem = variables.get(0);
        assertThat(caseFileItem).isNotNull();
        assertThat(caseFileItem.getName()).isEqualTo("caseFile_invoice");
        assertThat(caseFileItem.getType()).isInstanceOf(ObjectDataType.class);
        
        Node[] nodes = caseProcess.getNodes();
        assertThat(nodes).hasSize(5);
        
        HumanTaskNode humanTaskOne = (HumanTaskNode) nodes[0];
        assertThat(humanTaskOne.getName()).isEqualTo("First task");
        assertThat(humanTaskOne.getWork().getName()).isEqualTo("Human Task");
        
        assertThat(humanTaskOne.getInAssociations()).hasSize(1);
        assertThat(humanTaskOne.getInAssociations().get(0).getSources()).hasSize(1);
        assertThat(humanTaskOne.getInAssociations().get(0).getSources().get(0)).isEqualTo("caseFile_invoice");
        assertThat(humanTaskOne.getInAssociations().get(0).getTarget()).isEqualTo("invoice");   
        
        assertThat(humanTaskOne.getOutAssociations()).hasSize(1);
        assertThat(humanTaskOne.getOutAssociations().get(0).getSources()).hasSize(1);
        assertThat(humanTaskOne.getOutAssociations().get(0).getSources().get(0)).isEqualTo("approval");
        assertThat(humanTaskOne.getOutAssociations().get(0).getTarget()).isEqualTo("caseFile_invoice");
        
        SubProcessNode processNode = (SubProcessNode) nodes[1];
        assertThat(processNode.getName()).isEqualTo("Call subprocess");
        assertThat(processNode.getProcessId()).isEqualTo("subprocessId");
        assertThat(processNode.isWaitForCompletion()).isTrue();
        assertThat(processNode.isIndependent()).isFalse();
        assertThat(processNode.getInAssociations()).hasSize(1);
        assertThat(processNode.getInAssociations().get(0).getSources()).hasSize(1);
        assertThat(processNode.getInAssociations().get(0).getSources().get(0)).isEqualTo("caseFile_invoice");
        assertThat(processNode.getInAssociations().get(0).getTarget()).isEqualTo("invoiceDoc");   
        
        assertThat(processNode.getOutAssociations()).hasSize(1);
        assertThat(processNode.getOutAssociations().get(0).getSources()).hasSize(1);
        assertThat(processNode.getOutAssociations().get(0).getSources().get(0)).isEqualTo("approved");
        assertThat(processNode.getOutAssociations().get(0).getTarget()).isEqualTo("caseFile_invoice");
        
               
        humanTaskOne = (HumanTaskNode) nodes[2];
        assertThat(humanTaskOne.getName()).isEqualTo("non blocking HT");
        assertThat(humanTaskOne.getWork().getName()).isEqualTo("Human Task");
        
        assertThat(humanTaskOne.getInAssociations()).hasSize(1);
        assertThat(humanTaskOne.getInAssociations().get(0).getSources()).hasSize(1);
        assertThat(humanTaskOne.getInAssociations().get(0).getSources().get(0)).isEqualTo("caseFile_invoice");
        assertThat(humanTaskOne.getInAssociations().get(0).getTarget()).isEqualTo("invoice");
                
        assertThat(humanTaskOne.getOutAssociations()).hasSize(1);
        assertThat(humanTaskOne.getOutAssociations().get(0).getSources()).hasSize(1);
        assertThat(humanTaskOne.getOutAssociations().get(0).getSources().get(0)).isEqualTo("approved");
        assertThat(humanTaskOne.getOutAssociations().get(0).getTarget()).isEqualTo("caseFile_invoice");
        
        RuleSetNode decisionTask = (RuleSetNode) nodes[3];
        assertThat(decisionTask.getLanguage()).isEqualTo(RuleSetNode.DRL_LANG);
        assertThat(decisionTask.getName()).isEqualTo("Make a decision");
        assertThat(decisionTask.getRuleFlowGroup()).isEqualTo("decisionName");
        
        assertThat(decisionTask.getInAssociations()).hasSize(1);
        assertThat(decisionTask.getInAssociations().get(0).getSources()).hasSize(1);
        assertThat(decisionTask.getInAssociations().get(0).getSources().get(0)).isEqualTo("caseFile_invoice");
        assertThat(decisionTask.getInAssociations().get(0).getTarget()).isEqualTo("toApprove");   
        
        assertThat(decisionTask.getOutAssociations()).hasSize(1);
        assertThat(decisionTask.getOutAssociations().get(0).getSources()).hasSize(1);
        assertThat(decisionTask.getOutAssociations().get(0).getSources().get(0)).isEqualTo("decided");
        assertThat(decisionTask.getOutAssociations().get(0).getTarget()).isEqualTo("caseFile_invoice");
        
        
        WorkItemNode caseTask = (WorkItemNode) nodes[4];
        assertThat(caseTask.getName()).isEqualTo("Start another case");
        assertThat(caseTask.getWork().getName()).isEqualTo("StartCaseInstance");
        assertThat(caseTask.getWork().getParameter("CaseDefinitionId")).isEqualTo("caseDefId");
        
        assertThat(caseTask.getInAssociations()).hasSize(1);
        assertThat(caseTask.getInAssociations().get(0).getSources()).hasSize(1);
        assertThat(caseTask.getInAssociations().get(0).getSources().get(0)).isEqualTo("caseFile_invoice");
        assertThat(caseTask.getInAssociations().get(0).getTarget()).isEqualTo("Data_origin");   
        
        assertThat(caseTask.getOutAssociations()).hasSize(1);
        assertThat(caseTask.getOutAssociations().get(0).getSources()).hasSize(1);
        assertThat(caseTask.getOutAssociations().get(0).getSources().get(0)).isEqualTo("updated");
        assertThat(caseTask.getOutAssociations().get(0).getTarget()).isEqualTo("caseFile_invoice");
        
    }
    
    @Test
    public void testLoadOrderITHardwareCase() throws Exception {
        
        KieBase kbase = createKnowledgeBase("CMMN-OrderITHardwareCase.cmmn");
        assertThat(kbase).isNotNull();
        
        Process process = kbase.getProcess("itorders.orderhardware");
        assertThat(process).isNotNull();
        assertThat(process).isInstanceOf(RuleFlowProcess.class);
        RuleFlowProcess caseProcess = (RuleFlowProcess) process;
        assertThat(caseProcess.isDynamic()).isTrue();
        assertThat(caseProcess.getId()).isEqualTo("itorders.orderhardware");
        assertThat(caseProcess.getName()).isEqualTo("Order IT hardware");
        
        Node[] nodes = caseProcess.getNodes();
        assertThat(nodes).hasSize(9);
        
        List<Variable> variables = caseProcess.getVariableScope().getVariables();
        assertThat(variables).hasSize(7);
    }
    
    @Test
    public void testLoadProcessTaskReferenceCase() throws Exception {
        
        KieBase kbase = createKnowledgeBase("CMMN-ProcessTaskReferenceCase.cmmn");
        assertThat(kbase).isNotNull();
        
        Process process = kbase.getProcess("Case_636d4bea-4d52-46fb-b1ad-9ceeddf1be69");
        assertThat(process).isNotNull();
        assertThat(process).isInstanceOf(RuleFlowProcess.class);
        RuleFlowProcess caseProcess = (RuleFlowProcess) process;
        assertThat(caseProcess.isDynamic()).isTrue();
        assertThat(caseProcess.getId()).isEqualTo("Case_636d4bea-4d52-46fb-b1ad-9ceeddf1be69");
        assertThat(caseProcess.getName()).isEqualTo("ProcessTaskCase");
        
        Node[] nodes = caseProcess.getNodes();
        assertThat(nodes).hasSize(1);
    
        SubProcessNode processNode = (SubProcessNode) nodes[0];
        assertThat(processNode.getName()).isEqualTo("call my process");
        assertThat(processNode.getProcessId()).isEqualTo("_4e0c5178-c886-4a14-ab6b-6ec6c940194b");
        assertThat(processNode.isWaitForCompletion()).isTrue();
        assertThat(processNode.isIndependent()).isFalse();
        assertThat(processNode.getInAssociations()).hasSize(0);        
        assertThat(processNode.getOutAssociations()).hasSize(0);
        
        
    }
    
    @Test
    public void testLoadProcessAndDecisionTaskReferenceCase() throws Exception {
        
        KieBase kbase = createKnowledgeBase("CMMN-DecisionTaskReferenceCase.cmmn");
        assertThat(kbase).isNotNull();
        
        Process process = kbase.getProcess("Case_636d4bea-4d52-46fb-b1ad-9ceeddf1be69");
        assertThat(process).isNotNull();
        assertThat(process).isInstanceOf(RuleFlowProcess.class);
        RuleFlowProcess caseProcess = (RuleFlowProcess) process;
        assertThat(caseProcess.isDynamic()).isTrue();
        assertThat(caseProcess.getId()).isEqualTo("Case_636d4bea-4d52-46fb-b1ad-9ceeddf1be69");
        assertThat(caseProcess.getName()).isEqualTo("ProcessAndDecisionTaskCase");
        
        Node[] nodes = caseProcess.getNodes();
        assertThat(nodes).hasSize(2);
    
        SubProcessNode processNode = (SubProcessNode) nodes[0];
        assertThat(processNode.getName()).isEqualTo("call my process");
        assertThat(processNode.getProcessId()).isEqualTo("_4e0c5178-c886-4a14-ab6b-6ec6c940194b");
        assertThat(processNode.isWaitForCompletion()).isTrue();
        assertThat(processNode.isIndependent()).isFalse();
        assertThat(processNode.getInAssociations()).hasSize(0);        
        assertThat(processNode.getOutAssociations()).hasSize(0);
        
        RuleSetNode decisionTask = (RuleSetNode) nodes[1];
        assertThat(decisionTask.getName()).isEqualTo("call my decision");
        assertThat(decisionTask.getLanguage()).isEqualTo(RuleSetNode.DMN_LANG);
        assertThat(decisionTask.getRuleFlowGroup()).isNull();
        assertThat(decisionTask.getNamespace()).isEqualTo("http://www.trisotech.com/definitions/_0020_vacation_days");
        assertThat(decisionTask.getModel()).isEqualTo("_0020_vacation_days");
        assertThat(decisionTask.getDecision()).isEqualTo("Total Vacation Days");
        
        assertThat(decisionTask.getInAssociations()).hasSize(0);        
        assertThat(decisionTask.getOutAssociations()).hasSize(0);        
    }
}
