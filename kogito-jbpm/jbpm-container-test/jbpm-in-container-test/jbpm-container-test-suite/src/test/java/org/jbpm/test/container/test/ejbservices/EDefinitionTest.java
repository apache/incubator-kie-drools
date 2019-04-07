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

package org.jbpm.test.container.test.ejbservices;

import java.util.Collection;
import java.util.Map;

import javax.ejb.EJB;

import org.assertj.core.api.Assertions;
import org.jbpm.test.container.AbstractEJBServicesTest;
import org.jbpm.test.container.groups.EAP;
import org.jbpm.test.container.groups.WAS;
import org.jbpm.test.container.groups.WLS;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.UserTaskDefinition;
import org.jbpm.services.ejb.api.DefinitionServiceEJBLocal;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.KieServices;

@Category({EAP.class, WAS.class, WLS.class})
public class EDefinitionTest extends AbstractEJBServicesTest {

    @EJB
    private DefinitionServiceEJBLocal definitionService;

    @EJB
    protected ProcessServiceEJBLocal processService;

    @Test
    public void testBuildProcessDefinition() {
        String deploymentId = "org.jboss.bpms.qa:ejb-services-single:1.0";
        String bpmn2Content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.omg.org/bpmn20\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:drools=\"http://www.jboss.org/drools\" id=\"_IN4SENQhEeKMjMb8Niyi_Q\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd\" expressionLanguage=\"http://www.mvel.org/2.0\" targetNamespace=\"http://www.omg.org/bpmn20\" typeLanguage=\"http://www.java.com/javaTypes\">   <bpmn2:process id=\"org.jboss.qa.bpms.ScriptTask\" drools:version=\"1.0\" drools:packageName=\"defaultPackage\" name=\"ScriptTask\" isExecutable=\"true\">     <bpmn2:startEvent id=\"_A3185DDF-23A7-48B7-A2FE-7C0FE39F6691\" drools:bgcolor=\"#9acd32\" drools:selectable=\"true\" name=\"\">       <bpmn2:outgoing>_DA0951B7-5BC8-49AE-9E35-AA855177BBB6</bpmn2:outgoing>     </bpmn2:startEvent>     <bpmn2:scriptTask id=\"_3C8F4385-5348-479C-83EE-0C2DC2004F1A\" drools:selectable=\"true\" name=\"Script Task\" scriptFormat=\"http://www.java.com/java\">       <bpmn2:incoming>_DA0951B7-5BC8-49AE-9E35-AA855177BBB6</bpmn2:incoming>       <bpmn2:outgoing>_6BF9DBC4-39E3-48DF-ABBC-896E9E534ECC</bpmn2:outgoing>       <bpmn2:script><![CDATA[System.out.println(\"Hello World!!!\");]]></bpmn2:script>     </bpmn2:scriptTask>     <bpmn2:sequenceFlow id=\"_DA0951B7-5BC8-49AE-9E35-AA855177BBB6\" drools:bgcolor=\"#000000\" drools:selectable=\"true\" sourceRef=\"_A3185DDF-23A7-48B7-A2FE-7C0FE39F6691\" targetRef=\"_3C8F4385-5348-479C-83EE-0C2DC2004F1A\"/>     <bpmn2:endEvent id=\"_DC07735C-FA99-414C-AEAC-9F4CE0CF24F9\" drools:bgcolor=\"#ff6347\" drools:selectable=\"true\" name=\"\">       <bpmn2:incoming>_6BF9DBC4-39E3-48DF-ABBC-896E9E534ECC</bpmn2:incoming>     </bpmn2:endEvent>     <bpmn2:sequenceFlow id=\"_6BF9DBC4-39E3-48DF-ABBC-896E9E534ECC\" drools:bgcolor=\"#000000\" drools:selectable=\"true\" sourceRef=\"_3C8F4385-5348-479C-83EE-0C2DC2004F1A\" targetRef=\"_DC07735C-FA99-414C-AEAC-9F4CE0CF24F9\"/>   </bpmn2:process>   <bpmndi:BPMNDiagram id=\"_IN4SEdQhEeKMjMb8Niyi_Q\">     <bpmndi:BPMNPlane id=\"_IN45INQhEeKMjMb8Niyi_Q\" bpmnElement=\"org.jboss.qa.bpms.ScriptTask\">       <bpmndi:BPMNShape id=\"_IN45IdQhEeKMjMb8Niyi_Q\" bpmnElement=\"_A3185DDF-23A7-48B7-A2FE-7C0FE39F6691\">         <dc:Bounds height=\"30.0\" width=\"30.0\" x=\"122.0\" y=\"178.0\"/>       </bpmndi:BPMNShape>       <bpmndi:BPMNShape id=\"_IN45ItQhEeKMjMb8Niyi_Q\" bpmnElement=\"_3C8F4385-5348-479C-83EE-0C2DC2004F1A\">         <dc:Bounds height=\"80.0\" width=\"100.0\" x=\"180.0\" y=\"153.0\"/>       </bpmndi:BPMNShape>       <bpmndi:BPMNEdge id=\"_IN45I9QhEeKMjMb8Niyi_Q\" bpmnElement=\"_DA0951B7-5BC8-49AE-9E35-AA855177BBB6\">         <di:waypoint xsi:type=\"dc:Point\" x=\"137.0\" y=\"193.0\"/>         <di:waypoint xsi:type=\"dc:Point\" x=\"230.0\" y=\"193.0\"/>       </bpmndi:BPMNEdge>       <bpmndi:BPMNShape id=\"_IN45JNQhEeKMjMb8Niyi_Q\" bpmnElement=\"_DC07735C-FA99-414C-AEAC-9F4CE0CF24F9\">         <dc:Bounds height=\"28.0\" width=\"28.0\" x=\"320.0\" y=\"178.0\"/>       </bpmndi:BPMNShape>       <bpmndi:BPMNEdge id=\"_IN45JdQhEeKMjMb8Niyi_Q\" bpmnElement=\"_6BF9DBC4-39E3-48DF-ABBC-896E9E534ECC\">         <di:waypoint xsi:type=\"dc:Point\" x=\"230.0\" y=\"193.0\"/>         <di:waypoint xsi:type=\"dc:Point\" x=\"334.0\" y=\"192.0\"/>       </bpmndi:BPMNEdge>     </bpmndi:BPMNPlane>   </bpmndi:BPMNDiagram>   <bpmn2:relationship id=\"_IN45JtQhEeKMjMb8Niyi_Q\" type=\"BPSimData\">     <bpmn2:extensionElements>       <bpsim:BPSimData>         <bpsim:Scenario xsi:type=\"bpsim:Scenario\" id=\"default\" name=\"Simulationscenario\">           <bpsim:ScenarioParameters xsi:type=\"bpsim:ScenarioParameters\" baseTimeUnit=\"s\"/>           <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_3C8F4385-5348-479C-83EE-0C2DC2004F1A\" id=\"_IN45J9QhEeKMjMb8Niyi_Q\">             <bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\">               <bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\">                 <bpsim:NormalDistribution mean=\"0.0\" standardDeviation=\"0.0\"/>               </bpsim:ProcessingTime>             </bpsim:TimeParameters>             <bpsim:CostParameters xsi:type=\"bpsim:CostParameters\">               <bpsim:UnitCost xsi:type=\"bpsim:Parameter\">                 <bpsim:FloatingParameter value=\"0.0\"/>               </bpsim:UnitCost>             </bpsim:CostParameters>           </bpsim:ElementParameters>           <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_6BF9DBC4-39E3-48DF-ABBC-896E9E534ECC\" id=\"_IN45KNQhEeKMjMb8Niyi_Q\">             <bpsim:ControlParameters xsi:type=\"bpsim:ControlParameters\">               <bpsim:Probability xsi:type=\"bpsim:Parameter\">                 <bpsim:FloatingParameter value=\"100.0\"/>               </bpsim:Probability>             </bpsim:ControlParameters>           </bpsim:ElementParameters>           <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_A3185DDF-23A7-48B7-A2FE-7C0FE39F6691\" id=\"_IN45KdQhEeKMjMb8Niyi_Q\">             <bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\">               <bpsim:WaitTime xsi:type=\"bpsim:Parameter\">                 <bpsim:FloatingParameter value=\"0.0\"/>               </bpsim:WaitTime>             </bpsim:TimeParameters>           </bpsim:ElementParameters>           <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_DC07735C-FA99-414C-AEAC-9F4CE0CF24F9\" id=\"_IN45KtQhEeKMjMb8Niyi_Q\">             <bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\">               <bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\">                 <bpsim:NormalDistribution mean=\"0.0\" standardDeviation=\"0.0\"/>               </bpsim:ProcessingTime>             </bpsim:TimeParameters>           </bpsim:ElementParameters>           <bpsim:ElementParameters xsi:type=\"bpsim:ElementParameters\" elementRef=\"_DA0951B7-5BC8-49AE-9E35-AA855177BBB6\" id=\"_IN45K9QhEeKMjMb8Niyi_Q\">             <bpsim:ControlParameters xsi:type=\"bpsim:ControlParameters\">               <bpsim:Probability xsi:type=\"bpsim:Parameter\">                 <bpsim:FloatingParameter value=\"100.0\"/>               </bpsim:Probability>             </bpsim:ControlParameters>           </bpsim:ElementParameters>         </bpsim:Scenario>       </bpsim:BPSimData>     </bpmn2:extensionElements>     <bpmn2:source>_IN4SENQhEeKMjMb8Niyi_Q</bpmn2:source>     <bpmn2:target>_IN4SENQhEeKMjMb8Niyi_Q</bpmn2:target>   </bpmn2:relationship> </bpmn2:definitions>";
        ProcessDefinition pd = definitionService.buildProcessDefinition(deploymentId, bpmn2Content, KieServices.Factory.get().getKieClasspathContainer(this.getClass().getClassLoader()), false);

        Assertions.assertThat(pd).isNotNull();

        System.out.println(pd.getId());                     //value from process definition
        System.out.println(pd.getName());                   //value from process definition
        System.out.println(pd.getKnowledgeType());          //PROCESS - always
        System.out.println(pd.getOriginalPath());           //Never called so far, maybe in future
        System.out.println(pd.getPackageName());            //value from process definition
        System.out.println(pd.getType());                   //RuleFlow always
        System.out.println(pd.getVersion());                //value from process definition
        System.out.println(pd.getAssociatedEntities());     //all remaining are empty for this process
        System.out.println(pd.getProcessVariables());
        System.out.println(pd.getReusableSubProcesses());
        System.out.println(pd.getServiceTasks());

        Assertions.assertThat(pd.getId()).isEqualTo("org.jboss.qa.bpms.ScriptTask");
        Assertions.assertThat(pd.getName()).isEqualTo("ScriptTask");
        Assertions.assertThat(pd.getKnowledgeType()).isEqualTo("PROCESS");
        Assertions.assertThat(pd.getOriginalPath()).isNull();
        Assertions.assertThat(pd.getPackageName()).isEqualTo("defaultPackage");
        Assertions.assertThat(pd.getType()).isEqualTo("RuleFlow");
        Assertions.assertThat(pd.getVersion()).isEqualTo("1.0");

        Assertions.assertThat(pd.getAssociatedEntities()).isEmpty();
        Assertions.assertThat(pd.getProcessVariables()).isEmpty();
        Assertions.assertThat(pd.getReusableSubProcesses()).isEmpty();
        Assertions.assertThat(pd.getServiceTasks()).isEmpty();
    }

    @Test
    public void testGetProcessDefinition() {
        DeploymentUnit basicKieJar = archive.deployBasicKieJar();

        ProcessDefinition pd = definitionService.getProcessDefinition(basicKieJar.getIdentifier(), HUMAN_TASK_PROCESS_ID);

        Assertions.assertThat(pd).isNotNull();

        System.out.println(pd.getId());
        System.out.println(pd.getName());
        System.out.println(pd.getKnowledgeType());
        System.out.println(pd.getOriginalPath());
        System.out.println(pd.getPackageName());
        System.out.println(pd.getType());
        System.out.println(pd.getVersion());
        System.out.println(pd.getAssociatedEntities());
        System.out.println(pd.getProcessVariables());
        System.out.println(pd.getReusableSubProcesses());
        System.out.println(pd.getServiceTasks());

        Assertions.assertThat(pd.getId()).isNotNull().isEqualTo(HUMAN_TASK_PROCESS_ID);
        Assertions.assertThat(pd.getName()).isNotNull().isEqualTo("HumanTask");
        Assertions.assertThat(pd.getKnowledgeType()).isNotNull();
//        Assertions.assertThat(pd.getOriginalPath()).isNotNull(); // Related to asset deployment
        Assertions.assertThat(pd.getPackageName()).isNotNull();
        Assertions.assertThat(pd.getType()).isNotNull();
        Assertions.assertThat(pd.getVersion()).isNotNull();
        Assertions.assertThat(pd.getAssociatedEntities()).isNotNull();
        Assertions.assertThat(pd.getProcessVariables()).isNotNull();
        Assertions.assertThat(pd.getReusableSubProcesses()).isNotNull();
        Assertions.assertThat(pd.getServiceTasks()).isNotNull();
    }

    @Test
    public void testProcessVariables() {
        DeploymentUnit variableKieJar = archive.deployVariableKieJar();

        Map<String, String> variables = definitionService.getProcessVariables(variableKieJar.getIdentifier(), OBJECT_VARIABLE_PROCESS_ID);
        Assertions.assertThat(variables).isNotNull().hasSize(2).containsEntry("type", "String").containsEntry("myobject", "Object");
    }

    @Test
    public void testServiceTasks() {
        DeploymentUnit serviceKieJar = archive.deployServiceKieJar();

        Map<String, String> services = definitionService.getServiceTasks(serviceKieJar.getIdentifier(), REST_WORK_ITEM_PROCESS_ID);
        Assertions.assertThat(services).isNotNull().hasSize(1).containsEntry("Any REST operation", "Rest");
    }

    @Test
    public void testAssociatedEntities() {
        DeploymentUnit basicKieJar = archive.deployBasicKieJar();

        Map<String, Collection<String>> entities = definitionService.getAssociatedEntities(basicKieJar.getIdentifier(), HUMAN_TASK_PROCESS_ID);
        Assertions.assertThat(entities).isNotNull().hasSize(1);
        Assertions.assertThat(entities.get("Hello")).contains("ibek");
    }

    @Test
    public void testTasksDefinitions() {
        DeploymentUnit basicKieJar = archive.deployBasicKieJar();

        Collection<UserTaskDefinition> tasks = definitionService.getTasksDefinitions(basicKieJar.getIdentifier(), HUMAN_TASK_PROCESS_ID);
        Assertions.assertThat(tasks).isNotNull().hasSize(1);
        UserTaskDefinition task = tasks.iterator().next();
        Assertions.assertThat(task.getName()).isEqualTo("Hello");
        Assertions.assertThat(task.getAssociatedEntities()).contains("ibek");
        Assertions.assertThat(task.getTaskInputMappings()).containsEntry("TaskName", "java.lang.String");
    }

}
