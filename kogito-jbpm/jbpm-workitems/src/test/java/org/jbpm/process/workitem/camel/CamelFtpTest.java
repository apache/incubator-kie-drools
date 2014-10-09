package org.jbpm.process.workitem.camel;

import org.apache.commons.io.FileUtils;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItemManager;

import java.io.IOException;

/**
 * Uploading a file to FTP endpoint.
 * */
public class CamelFtpTest extends CamelFtpBaseTest {

    @Test
    public void testFtp() throws IOException {
        CamelHandler handler = CamelHandlerFactory.ftpHandler();

        final String testData = "test-data";

        final WorkItem workItem = new WorkItemImpl();
        workItem.setParameter("username", USER);
        workItem.setParameter("password", PASSWD);
        workItem.setParameter("hostname", HOST);
        workItem.setParameter("port", PORT.toString());
        workItem.setParameter("directoryname", testFile.getParentFile().getName());
        workItem.setParameter("CamelFileName", testFile.getName());
        workItem.setParameter("payload", testData);

        WorkItemManager manager = new DefaultWorkItemManager(null);
        handler.executeWorkItem(workItem, manager);

        Assert.assertTrue("Expected file does not exist.", testFile.exists());

        String resultText = FileUtils.readFileToString(testFile);
        Assert.assertEquals(resultText, testData);
    }

    @Override
    protected FtpServer configureFtpServer(CamelFtpBaseTest.FtpServerBuilder builder) throws FtpException {
        ListenerFactory listenerFactory = new ListenerFactory();
        listenerFactory.setServerAddress(HOST);
        listenerFactory.setPort(PORT);

        return builder.addUser(USER, PASSWD, ftpRoot, true)
               .registerDefaultListener(listenerFactory.createListener())
               .build();
    }
}
