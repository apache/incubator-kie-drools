package org.jbpm.kie.services.test;

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
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.scanner.AbstractKieCiTest;
import org.kie.scanner.MavenRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.scanner.MavenRepository.getMavenRepository;

@Ignore
public class KieScannerTest extends AbstractKieCiTest {

    private FileManager fileManager;
    private File kPom;

    private KieServices ks = KieServices.Factory.get();
    private ReleaseId releaseId = ks.newReleaseId("org.kie", "scanner-test", "1.0-SNAPSHOT");

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

        MavenRepository repository = getMavenRepository();
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
}
