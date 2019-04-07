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

package org.jbpm.test.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;

public class SplitNodeAfterStartTest {

    @Test
    public void testSplitNodeAfterStart() throws Exception {
        String drl = "package org.drools.droolsjbpm_integration_testmgt\n" +
                     "rule \"FindStrings\"\n" +
                     "ruleflow-group \"find-strings\"\n" +
                     "when\n" +
                     "    java.lang.String()\n" +
                     "then\n" +
                     "    System.out.println(\"Found a String.\");\n" +
                     "end\n";

        String process = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                         "<!-- origin at X=0.0 Y=0.0 -->\n" +
                         "<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmn20=\"http://www.omg.org/bpmn20\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:bpsim=\"http://www.bpsim.org/schemas/1.0\" xmlns:color=\"http://www.omg.org/spec/BPMN/non-normative/color\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:drools=\"http://www.jboss.org/drools\" xmlns=\"http://www.jboss.org/drools\" xmlns:ns=\"http://www.w3.org/2001/XMLSchema\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd http://www.jboss.org/drools drools.xsd http://www.bpsim.org/schemas/1.0 bpsim.xsd\" id=\"_48zvoePFEeaTLY-yoiQrFA\" exporter=\"org.eclipse.bpmn2.modeler.core\" exporterVersion=\"1.3.2.Final-v20161020-1541-B59\" targetNamespace=\"http://www.omg.org/bpmn20\">\n" +
                         "  <bpmn2:process id=\"test-process\" drools:packageName=\"org.jboss.ddoyle.ruleflow.test\" drools:version=\"1.0\" name=\"test-process\" isExecutable=\"true\">\n" +
                         "    <bpmn2:startEvent id=\"_C4B63A0B-D378-4649-91B4-A38F8D25456C\" drools:selectable=\"true\" color:background-color=\"#00FF00\" color:border-color=\"#000000\" color:color=\"#000000\" name=\"\">\n" +
                         "      <bpmn2:extensionElements>\n" +
                         "        <drools:metaData name=\"elementname\">\n" +
                         "          <drools:metaValue><![CDATA[]]></drools:metaValue>\n" +
                         "        </drools:metaData>\n" +
                         "      </bpmn2:extensionElements>\n" +
                         "      <bpmn2:outgoing>_99F37141-3039-4E1B-BA3A-9DCBDC3DA610</bpmn2:outgoing>\n" +
                         "    </bpmn2:startEvent>\n" +
                         "    <bpmn2:endEvent id=\"_F29B1AE8-261A-4F91-92FE-226FA90EA552\" drools:selectable=\"true\" color:background-color=\"#FF0000\" color:border-color=\"#000000\" color:color=\"#000000\" name=\"\">\n" +
                         "      <bpmn2:extensionElements>\n" +
                         "        <drools:metaData name=\"elementname\">\n" +
                         "          <drools:metaValue><![CDATA[]]></drools:metaValue>\n" +
                         "        </drools:metaData>\n" +
                         "      </bpmn2:extensionElements>\n" +
                         "      <bpmn2:incoming>SequenceFlow_1</bpmn2:incoming>\n" +
                         "    </bpmn2:endEvent>\n" +
                         "    <bpmn2:businessRuleTask id=\"_A88545FB-A4BE-4E3B-B3D0-C1B310E46069\" drools:selectable=\"true\" drools:ruleFlowGroup=\"find-strings\" drools:scriptFormat=\"http://www.java.com/java\" color:background-color=\"#00FF00\" color:border-color=\"#000000\" color:color=\"#000000\" name=\"Find Strings\">\n" +
                         "      <bpmn2:extensionElements>\n" +
                         "        <drools:metaData name=\"elementname\">\n" +
                         "          <drools:metaValue><![CDATA[Find Strings]]></drools:metaValue>\n" +
                         "        </drools:metaData>\n" +
                         "      </bpmn2:extensionElements>\n" +
                         "      <bpmn2:incoming>_0AF69240-8379-46B8-8619-989344A7987E</bpmn2:incoming>\n" +
                         "      <bpmn2:outgoing>SequenceFlow_1</bpmn2:outgoing>\n" +
                         "    </bpmn2:businessRuleTask>\n" +
                         "    <bpmn2:exclusiveGateway id=\"_CE98852E-DA3D-4D20-A6C8-8F144670B5C9\" drools:selectable=\"true\" drools:dg=\"\" color:background-color=\"#f0e68c\" color:border-color=\"#a67f00\" color:color=\"#000000\" name=\"Has Strings?\" gatewayDirection=\"Diverging\">\n" +
                         "      <bpmn2:extensionElements>\n" +
                         "        <drools:metaData name=\"elementname\">\n" +
                         "          <drools:metaValue><![CDATA[Has items?]]></drools:metaValue>\n" +
                         "        </drools:metaData>\n" +
                         "      </bpmn2:extensionElements>\n" +
                         "      <bpmn2:incoming>_99F37141-3039-4E1B-BA3A-9DCBDC3DA610</bpmn2:incoming>\n" +
                         "      <bpmn2:outgoing>_0AF69240-8379-46B8-8619-989344A7987E</bpmn2:outgoing>\n" +
                         "      <bpmn2:outgoing>_1A4ADBD8-F086-4C69-9203-970354B06D21</bpmn2:outgoing>\n" +
                         "    </bpmn2:exclusiveGateway>\n" +
                         "    <bpmn2:sequenceFlow id=\"_99F37141-3039-4E1B-BA3A-9DCBDC3DA610\" drools:selectable=\"true\" color:background-color=\"#000000\" color:border-color=\"#000000\" color:color=\"#000000\" sourceRef=\"_C4B63A0B-D378-4649-91B4-A38F8D25456C\" targetRef=\"_CE98852E-DA3D-4D20-A6C8-8F144670B5C9\"/>\n" +
                         "    <bpmn2:sequenceFlow id=\"_0AF69240-8379-46B8-8619-989344A7987E\" drools:selectable=\"true\" color:background-color=\"#000000\" color:border-color=\"#000000\" color:color=\"#000000\" name=\"Yes\" sourceRef=\"_CE98852E-DA3D-4D20-A6C8-8F144670B5C9\" targetRef=\"_A88545FB-A4BE-4E3B-B3D0-C1B310E46069\">\n" +
                         "      <bpmn2:extensionElements>\n" +
                         "        <drools:metaData name=\"elementname\">\n" +
                         "          <drools:metaValue><![CDATA[Yes]]></drools:metaValue>\n" +
                         "        </drools:metaData>\n" +
                         "      </bpmn2:extensionElements>\n" +
                         "      <bpmn2:conditionExpression xsi:type=\"bpmn2:tFormalExpression\" id=\"_481k0OPFEeaTLY-yoiQrFA\" language=\"http://www.jboss.org/drools/rule\">exists java.lang.String()</bpmn2:conditionExpression>\n" +
                         "    </bpmn2:sequenceFlow>\n" +
                         "    <bpmn2:endEvent id=\"_B8B6A7DC-21BF-41C3-975D-FFB096932C88\" drools:selectable=\"true\" color:background-color=\"#ff6347\" color:border-color=\"#000000\" color:color=\"#000000\" name=\"\">\n" +
                         "      <bpmn2:extensionElements>\n" +
                         "        <drools:metaData name=\"elementname\">\n" +
                         "          <drools:metaValue><![CDATA[]]></drools:metaValue>\n" +
                         "        </drools:metaData>\n" +
                         "      </bpmn2:extensionElements>\n" +
                         "      <bpmn2:incoming>_1A4ADBD8-F086-4C69-9203-970354B06D21</bpmn2:incoming>\n" +
                         "    </bpmn2:endEvent>\n" +
                         "    <bpmn2:sequenceFlow id=\"_1A4ADBD8-F086-4C69-9203-970354B06D21\" drools:selectable=\"true\" color:background-color=\"#000000\" color:border-color=\"#000000\" color:color=\"#000000\" name=\"No\" sourceRef=\"_CE98852E-DA3D-4D20-A6C8-8F144670B5C9\" targetRef=\"_B8B6A7DC-21BF-41C3-975D-FFB096932C88\">\n" +
                         "      <bpmn2:extensionElements>\n" +
                         "        <drools:metaData name=\"elementname\">\n" +
                         "          <drools:metaValue><![CDATA[No]]></drools:metaValue>\n" +
                         "        </drools:metaData>\n" +
                         "      </bpmn2:extensionElements>\n" +
                         "      <bpmn2:conditionExpression xsi:type=\"bpmn2:tFormalExpression\" id=\"_481k0ePFEeaTLY-yoiQrFA\" language=\"http://www.jboss.org/drools/rule\">not java.lang.String()</bpmn2:conditionExpression>\n" +
                         "    </bpmn2:sequenceFlow>\n" +
                         "    <bpmn2:sequenceFlow id=\"SequenceFlow_1\" drools:priority=\"1\" sourceRef=\"_A88545FB-A4BE-4E3B-B3D0-C1B310E46069\" targetRef=\"_F29B1AE8-261A-4F91-92FE-226FA90EA552\"/>\n" +
                         "  </bpmn2:process>\n" +
                         "  <bpmndi:BPMNDiagram id=\"_481k0uPFEeaTLY-yoiQrFA\">\n" +
                         "    <bpmndi:BPMNPlane id=\"_481k0-PFEeaTLY-yoiQrFA\" bpmnElement=\"test-process\">\n" +
                         "      <bpmndi:BPMNShape id=\"_481k1OPFEeaTLY-yoiQrFA\" bpmnElement=\"_C4B63A0B-D378-4649-91B4-A38F8D25456C\">\n" +
                         "        <dc:Bounds height=\"30.0\" width=\"30.0\" x=\"15.0\" y=\"183.0\"/>\n" +
                         "        <bpmndi:BPMNLabel/>\n" +
                         "      </bpmndi:BPMNShape>\n" +
                         "      <bpmndi:BPMNShape id=\"_481k1ePFEeaTLY-yoiQrFA\" bpmnElement=\"_F29B1AE8-261A-4F91-92FE-226FA90EA552\">\n" +
                         "        <dc:Bounds height=\"28.0\" width=\"28.0\" x=\"530.0\" y=\"184.0\"/>\n" +
                         "        <bpmndi:BPMNLabel/>\n" +
                         "      </bpmndi:BPMNShape>\n" +
                         "      <bpmndi:BPMNShape id=\"_481k2uPFEeaTLY-yoiQrFA\" bpmnElement=\"_A88545FB-A4BE-4E3B-B3D0-C1B310E46069\">\n" +
                         "        <dc:Bounds height=\"80.0\" width=\"100.0\" x=\"285.0\" y=\"159.0\"/>\n" +
                         "        <bpmndi:BPMNLabel>\n" +
                         "          <dc:Bounds height=\"11.0\" width=\"49.0\" x=\"310.0\" y=\"193.0\"/>\n" +
                         "        </bpmndi:BPMNLabel>\n" +
                         "      </bpmndi:BPMNShape>\n" +
                         "      <bpmndi:BPMNShape id=\"_481k2-PFEeaTLY-yoiQrFA\" bpmnElement=\"_CE98852E-DA3D-4D20-A6C8-8F144670B5C9\" isMarkerVisible=\"true\">\n" +
                         "        <dc:Bounds height=\"40.0\" width=\"40.0\" x=\"150.0\" y=\"179.0\"/>\n" +
                         "        <bpmndi:BPMNLabel>\n" +
                         "          <dc:Bounds height=\"11.0\" width=\"53.0\" x=\"144.0\" y=\"219.0\"/>\n" +
                         "        </bpmndi:BPMNLabel>\n" +
                         "      </bpmndi:BPMNShape>\n" +
                         "      <bpmndi:BPMNShape id=\"_481k3OPFEeaTLY-yoiQrFA\" bpmnElement=\"_B8B6A7DC-21BF-41C3-975D-FFB096932C88\">\n" +
                         "        <dc:Bounds height=\"28.0\" width=\"28.0\" x=\"156.0\" y=\"291.0\"/>\n" +
                         "        <bpmndi:BPMNLabel/>\n" +
                         "      </bpmndi:BPMNShape>\n" +
                         "      <bpmndi:BPMNEdge id=\"_482L4uPFEeaTLY-yoiQrFA\" bpmnElement=\"_99F37141-3039-4E1B-BA3A-9DCBDC3DA610\" sourceElement=\"_481k1OPFEeaTLY-yoiQrFA\" targetElement=\"_481k2-PFEeaTLY-yoiQrFA\">\n" +
                         "        <di:waypoint xsi:type=\"dc:Point\" x=\"30.0\" y=\"198.0\"/>\n" +
                         "        <di:waypoint xsi:type=\"dc:Point\" x=\"170.0\" y=\"199.0\"/>\n" +
                         "        <bpmndi:BPMNLabel/>\n" +
                         "      </bpmndi:BPMNEdge>\n" +
                         "      <bpmndi:BPMNEdge id=\"_482L4-PFEeaTLY-yoiQrFA\" bpmnElement=\"_0AF69240-8379-46B8-8619-989344A7987E\" sourceElement=\"_481k2-PFEeaTLY-yoiQrFA\" targetElement=\"_481k2uPFEeaTLY-yoiQrFA\">\n" +
                         "        <di:waypoint xsi:type=\"dc:Point\" x=\"170.0\" y=\"199.0\"/>\n" +
                         "        <di:waypoint xsi:type=\"dc:Point\" x=\"335.0\" y=\"199.0\"/>\n" +
                         "        <bpmndi:BPMNLabel>\n" +
                         "          <dc:Bounds height=\"11.0\" width=\"16.0\" x=\"231.0\" y=\"200.0\"/>\n" +
                         "        </bpmndi:BPMNLabel>\n" +
                         "      </bpmndi:BPMNEdge>\n" +
                         "      <bpmndi:BPMNEdge id=\"_482L5OPFEeaTLY-yoiQrFA\" bpmnElement=\"_1A4ADBD8-F086-4C69-9203-970354B06D21\" sourceElement=\"_481k2-PFEeaTLY-yoiQrFA\" targetElement=\"_481k3OPFEeaTLY-yoiQrFA\">\n" +
                         "        <di:waypoint xsi:type=\"dc:Point\" x=\"170.0\" y=\"199.0\"/>\n" +
                         "        <di:waypoint xsi:type=\"dc:Point\" x=\"170.0\" y=\"305.0\"/>\n" +
                         "        <bpmndi:BPMNLabel>\n" +
                         "          <dc:Bounds height=\"11.0\" width=\"11.0\" x=\"165.0\" y=\"256.0\"/>\n" +
                         "        </bpmndi:BPMNLabel>\n" +
                         "      </bpmndi:BPMNEdge>\n" +
                         "      <bpmndi:BPMNEdge id=\"BPMNEdge_SequenceFlow_1\" bpmnElement=\"SequenceFlow_1\" sourceElement=\"_481k2uPFEeaTLY-yoiQrFA\" targetElement=\"_481k1ePFEeaTLY-yoiQrFA\">\n" +
                         "        <di:waypoint xsi:type=\"dc:Point\" x=\"385.0\" y=\"199.0\"/>\n" +
                         "        <di:waypoint xsi:type=\"dc:Point\" x=\"457.0\" y=\"198.0\"/>\n" +
                         "        <di:waypoint xsi:type=\"dc:Point\" x=\"530.0\" y=\"198.0\"/>\n" +
                         "        <bpmndi:BPMNLabel/>\n" +
                         "      </bpmndi:BPMNEdge>\n" +
                         "    </bpmndi:BPMNPlane>\n" +
                         "  </bpmndi:BPMNDiagram>\n" +
                         "</bpmn2:definitions>";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel = kproj.newKieBaseModel( "kbase" ).setDefault( true );
        KieSessionModel ksessionModel = kieBaseModel.newKieSessionModel( "stateless" ).setDefault( true )
                                                    .setType( KieSessionModel.KieSessionType.STATELESS );

        ReleaseId releaseId1 = ks.newReleaseId( "org.kie.test", "ruleflow-split", "1.0.0" );

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML( kproj.toXML() );
        kfs.generateAndWritePomXML( releaseId1 );
        kfs.write( "src/main/resources/r" + 1 + ".drl", drl );
        kfs.write( "src/main/resources/p" + 1 + ".bpmn2", process );
        KieBuilder kb = ks.newKieBuilder( kfs ).buildAll();
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository().getKieModule( releaseId1 );

        KieContainer kc = ks.newKieContainer( releaseId1 );

        Properties ksessionConfigProps = new Properties();
        ksessionConfigProps.setProperty("drools.processSignalManagerFactory"   , DefaultSignalManagerFactory.class.getName() );
        ksessionConfigProps.setProperty("drools.processInstanceManagerFactory" , DefaultProcessInstanceManagerFactory.class.getName() );
        KieSessionConfiguration sessionConf = ks.newKieSessionConfiguration( ksessionConfigProps );
        StatelessKieSession kieSession = kc.newStatelessKieSession( "stateless", sessionConf );

        KieCommands commandFactory = ks.getCommands();

        List<Command<?>> commands = new ArrayList<>();
        commands.add(commandFactory.newInsert("Hello rules!"));
        commands.add(commandFactory.newStartProcess("test-process"));
        commands.add(commandFactory.newFireAllRules());

        kieSession.execute(commandFactory.newBatchExecution(commands));
    }
}
