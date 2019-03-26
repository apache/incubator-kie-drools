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

package org.jbpm.kie.services.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.FileManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

@Ignore
public class KieScannerTest {

    private FileManager fileManager;
    private File kPom;

    private KieServices ks = KieServices.Factory.get();
    private ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0");

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();
        kPom = createKPom(fileManager, releaseId);
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
    }

    @Test
    public void testKScanner() throws Exception {
        List<String> results = new ArrayList<String>();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("results", results);

        InternalKieModule kJar1 = createJbpmKieJar("Hello");
        KieContainer kieContainer = ks.newKieContainer(releaseId);

        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, kPom);

        // create a ksesion and check it works as expected
        KieSession ksession = kieContainer.newKieSession("KSession1");
        ProcessInstance processInstance = ksession.startProcess("Minimal", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertEquals(1, results.size());
        assertEquals("Hello", results.get(0));
        results.clear();

        InternalKieModule kJar2 = createJbpmKieJar("Morning");

        repository.deployArtifact(releaseId, kJar2, kPom);

        KieScanner scanner = ks.newKieScanner(kieContainer);
        scanner.scanNow();

        // create a ksesion and check it works as expected
        KieSession ksession2 = kieContainer.newKieSession("KSession1");
        ProcessInstance processInstance2 = ksession2.startProcess("Minimal", params);
        assertTrue(processInstance2.getState() == ProcessInstance.STATE_COMPLETED);
        assertEquals(1, results.size());
        assertEquals("Morning", results.get(0));
        results.clear();

        ks.getRepository().removeKieModule(releaseId);
    }

    private InternalKieModule createJbpmKieJar(String message) {
        KieFileSystem kfs = createKieFileSystemWithKProject(ks, true);
        kfs.writePomXML(getPom(releaseId));

        String file = "org/test/process.bpmn2";
        kfs.write("src/main/resources/KBase1/" + file, createBPMN(message));

        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        assertTrue(kieBuilder.buildAll().getResults().getMessages().isEmpty());

        return (InternalKieModule) kieBuilder.getKieModule();
    }

    private String createBPMN(String message) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
               "<definitions id=\"Definition\"\n" +
               "             targetNamespace=\"http://www.example.org/MinimalExample\"\n" +
               "             typeLanguage=\"http://www.java.com/javaTypes\"\n" +
               "             expressionLanguage=\"http://www.mvel.org/2.0\"\n" +
               "             xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\"\n" +
               "             xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
               "             xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\"\n" +
               "             xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\"\n" +
               "             xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\"\n" +
               "             xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\"\n" +
               "             xmlns:tns=\"http://www.jboss.org/drools\">\n" +
               "\n" +
               "  <process processType=\"Private\" isExecutable=\"true\" id=\"Minimal\" name=\"Minimal Process\" >\n" +
               "\n" +
               "    <!-- process variables -->\n" +
               "    <dataObject id=\"results\"/>\n" +
               "\n" +
               "    <!-- nodes -->\n" +
               "    <startEvent id=\"_1\" name=\"StartProcess\" />\n" +
               "    <scriptTask id=\"_2\" name=\"Hello\" >\n" +
               "      <script>results.add(\"" + message + "\");</script>\n" +
               "    </scriptTask>\n" +
               "    <endEvent id=\"_3\" name=\"EndProcess\" >\n" +
               "        <terminateEventDefinition/>\n" +
               "    </endEvent>\n" +
               "\n" +
               "    <!-- connections -->\n" +
               "    <sequenceFlow id=\"_1-_2\" sourceRef=\"_1\" targetRef=\"_2\" />\n" +
               "    <sequenceFlow id=\"_2-_3\" sourceRef=\"_2\" targetRef=\"_3\" />\n" +
               "\n" +
               "  </process>\n" +
               "\n" +
               "  <bpmndi:BPMNDiagram>\n" +
               "    <bpmndi:BPMNPlane bpmnElement=\"Minimal\" >\n" +
               "      <bpmndi:BPMNShape bpmnElement=\"_1\" >\n" +
               "        <dc:Bounds x=\"15\" y=\"91\" width=\"48\" height=\"48\" />\n" +
               "      </bpmndi:BPMNShape>\n" +
               "      <bpmndi:BPMNShape bpmnElement=\"_2\" >\n" +
               "        <dc:Bounds x=\"95\" y=\"88\" width=\"83\" height=\"48\" />\n" +
               "      </bpmndi:BPMNShape>\n" +
               "      <bpmndi:BPMNShape bpmnElement=\"_3\" >\n" +
               "        <dc:Bounds x=\"258\" y=\"86\" width=\"48\" height=\"48\" />\n" +
               "      </bpmndi:BPMNShape>\n" +
               "      <bpmndi:BPMNEdge bpmnElement=\"_1-_2\" >\n" +
               "        <di:waypoint x=\"39\" y=\"115\" />\n" +
               "        <di:waypoint x=\"75\" y=\"46\" />\n" +
               "        <di:waypoint x=\"136\" y=\"112\" />\n" +
               "      </bpmndi:BPMNEdge>\n" +
               "      <bpmndi:BPMNEdge bpmnElement=\"_2-_3\" >\n" +
               "        <di:waypoint x=\"136\" y=\"112\" />\n" +
               "        <di:waypoint x=\"240\" y=\"240\" />\n" +
               "        <di:waypoint x=\"282\" y=\"110\" />\n" +
               "      </bpmndi:BPMNEdge>\n" +
               "    </bpmndi:BPMNPlane>\n" +
               "  </bpmndi:BPMNDiagram>\n" +
               "\n" +
               "</definitions>";
    }
    
    protected String getPom(ReleaseId releaseId, ReleaseId... dependencies) {
        String pom =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
                        "  <modelVersion>4.0.0</modelVersion>\n" +
                        "\n" +
                        "  <groupId>" + releaseId.getGroupId() + "</groupId>\n" +
                        "  <artifactId>" + releaseId.getArtifactId() + "</artifactId>\n" +
                        "  <version>" + releaseId.getVersion() + "</version>\n" +
                        "\n";
        if (dependencies != null && dependencies.length > 0) {
            pom += "<dependencies>\n";
            for (ReleaseId dep : dependencies) {
                pom += "<dependency>\n";
                pom += "  <groupId>" + dep.getGroupId() + "</groupId>\n";
                pom += "  <artifactId>" + dep.getArtifactId() + "</artifactId>\n";
                pom += "  <version>" + dep.getVersion() + "</version>\n";
                pom += "</dependency>\n";
            }
            pom += "</dependencies>\n";
        }
        pom += "</project>";
        return pom;
    }
    
    protected File createKPom(FileManager fileManager, ReleaseId releaseId, ReleaseId... dependencies) throws IOException {
        File pomFile = fileManager.newFile("pom.xml");
        fileManager.write(pomFile, getPom(releaseId, dependencies));
        return pomFile;
    }
    
    protected KieFileSystem createKieFileSystemWithKProject(KieServices ks, boolean isdefault) {
        KieModuleModel kproj = ks.newKieModuleModel();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1").setDefault(isdefault)
                .setEqualsBehavior(EqualityBehaviorOption.EQUALITY)
                .setEventProcessingMode(EventProcessingOption.STREAM);

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1").setDefault(isdefault)
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType(ClockTypeOption.get("realtime"));

        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML());
        return kfs;
    }
}
