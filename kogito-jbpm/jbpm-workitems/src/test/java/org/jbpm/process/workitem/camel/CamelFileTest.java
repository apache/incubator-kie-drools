package org.jbpm.process.workitem.camel;

import org.apache.commons.io.FileUtils;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.camel.request.RequestPayloadMapper;
import org.jbpm.process.workitem.camel.uri.FileURIMapper;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.utils.KieHelper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Uploading a file via File endpoint.
 * */
public class CamelFileTest extends AbstractBaseTest {

    private static final String PROCESS_DEFINITION = "/BPMN2-CamelFileProcess.bpmn2";

    private static File tempDir;
    private static File testDir;
    private static File testFile;

    @BeforeClass
    public static void initialize() {
        tempDir = new File(System.getProperty("java.io.tmpdir"));
        testDir = new File(tempDir, "test_dir");
        String fileName = "test_file_" + CamelFileTest.class.getName() + "_" + UUID.randomUUID().toString();
        testFile = new File(tempDir, fileName);
    }

    @AfterClass
    public static void clean() throws IOException {
        FileUtils.deleteDirectory(testDir);
    }

    /**
     * Test with entire BPMN process.
     * */
    @Test
    public void testSingleFileProcess() throws IOException {
        final String testData = "test-data";

        CamelHandler handler = CamelHandlerFactory.fileHandler();

        KieHelper kieHelper = new KieHelper();
        kieHelper.addResource(new ClassPathResource(PROCESS_DEFINITION, getClass()), ResourceType.BPMN2);
        KieBase kbase = kieHelper.build();
        KieSession kieSession = kbase.newKieSession();

        kieSession.getWorkItemManager().registerWorkItemHandler("CamelFile", handler);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("payloadVar", testData);
        params.put("pathVar", tempDir.getAbsolutePath());
        params.put("fileNameVar", testFile.getName());

        ProcessInstance pi = kieSession.startProcess("camelFileProcess", params);

        ProcessInstance result = kieSession.getProcessInstance(pi.getId());
        Assert.assertNull(result);

        Assert.assertTrue("Expected file does not exist.", testFile.exists());

        String resultText = FileUtils.readFileToString(testFile);
        Assert.assertEquals(resultText, testData);
    }

    /**
     * File to upload has been specified by Camel header.
     * */
    @Test
    public void testSingleFileWithHeaders() throws IOException {
        Set<String> headers = new HashSet<String>();
        headers.add("CamelFileName");
        CamelHandler handler = new CamelHandler(new FileURIMapper(), new RequestPayloadMapper("payload", headers));

        final String testData = "test-data";
        final WorkItem workItem = new WorkItemImpl();
        workItem.setParameter("path", tempDir.getAbsolutePath());
        workItem.setParameter("payload", testData);
        workItem.setParameter("CamelFileName", testFile.getName());

        WorkItemManager manager = new DefaultWorkItemManager(null);
        handler.executeWorkItem(workItem, manager);

        Assert.assertTrue("Expected file does not exist.", testFile.exists());

        String resultText = FileUtils.readFileToString(testFile);
        Assert.assertEquals(resultText, testData);
    }
}
