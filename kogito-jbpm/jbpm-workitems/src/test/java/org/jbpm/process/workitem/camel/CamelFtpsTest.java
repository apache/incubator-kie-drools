package org.jbpm.process.workitem.camel;

import org.apache.commons.io.FileUtils;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfiguration;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItemManager;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Uploading a file to FTPS endpoint. Uses prepared key/trust stores for SSL.
 * */
public class CamelFtpsTest extends CamelFtpBaseTest {

    private static final String CLIENT_SSL_STORE_PASSWD = "passwd";
    private static final String SERVER_SSL_STORE_PASSWD = "passwd";
    private static final String CLIENT_SSL_KEY_PASSWD = "passwd";
    private static final String SERVER_SSL_KEY_PASSWD = "passwd";

    private static File SERVER_SSL_KEY_TRUST_STORE;
    private static File CLIENT_SSL_KEY_TRUST_STORE;

    @BeforeClass
    public static void setupKeyTrustStores() throws URISyntaxException {
        CLIENT_SSL_KEY_TRUST_STORE = new File(CamelFtpsTest.class.getResource("/ssl/client.jks").toURI());
        SERVER_SSL_KEY_TRUST_STORE = new File(CamelFtpsTest.class.getResource("/ssl/server.jks").toURI());
    }

    @Test
    public void testFtps() throws IOException {
        CamelHandler handler = CamelHandlerFactory.ftpsHandler();

        final String testData = "test-data";

        final WorkItem workItem = new WorkItemImpl();
        workItem.setParameter("username", USER);
        workItem.setParameter("password", PASSWD);
        workItem.setParameter("hostname", HOST);
        workItem.setParameter("port", PORT.toString());
        workItem.setParameter("directoryname", testFile.getParentFile().getName());
        workItem.setParameter("CamelFileName", testFile.getName());
        workItem.setParameter("payload", testData);
        workItem.setParameter("isImplicit", "true");
        workItem.setParameter("securityProtocol", "TLS");

        //needed for SSL
        workItem.setParameter("ftpClient.trustStore.file", CLIENT_SSL_KEY_TRUST_STORE.getAbsolutePath());
        workItem.setParameter("ftpClient.trustStore.password", CLIENT_SSL_STORE_PASSWD);
        workItem.setParameter("ftpClient.trustStore.keyPassword", CLIENT_SSL_KEY_PASSWD);

        workItem.setParameter("ftpClient.keyStore.file", CLIENT_SSL_KEY_TRUST_STORE.getAbsolutePath());
        workItem.setParameter("ftpClient.keyStore.password", CLIENT_SSL_STORE_PASSWD);
        workItem.setParameter("ftpClient.keyStore.keyPassword",  CLIENT_SSL_KEY_PASSWD);

        WorkItemManager manager = new DefaultWorkItemManager(null);
        handler.executeWorkItem(workItem, manager);

        //assertions
        Assert.assertTrue("Expected file does not exist.", testFile.exists());

        String resultText = FileUtils.readFileToString(testFile);
        Assert.assertEquals(resultText, testData);
    }

    @Override
    protected FtpServer configureFtpServer(CamelFtpBaseTest.FtpServerBuilder builder) throws FtpException {
        ListenerFactory listenerFactory = configureSSL();

        return builder.addUser(USER, PASSWD, ftpRoot, true)
                .registerDefaultListener(listenerFactory.createListener())
                .build();
    }

    private ListenerFactory configureSSL() {
        ListenerFactory listener = new ListenerFactory();
        listener.setServerAddress("127.0.0.1");
        listener.setPort(PORT);

        SslConfigurationFactory ssl = new SslConfigurationFactory();
        ssl.setKeystoreFile(SERVER_SSL_KEY_TRUST_STORE);
        ssl.setKeyPassword(SERVER_SSL_KEY_PASSWD);
        ssl.setKeystorePassword(SERVER_SSL_STORE_PASSWD);
        ssl.setTruststoreFile(SERVER_SSL_KEY_TRUST_STORE);
        ssl.setTruststorePassword(SERVER_SSL_STORE_PASSWD);
        ssl.setClientAuthentication("NEED");

        SslConfiguration sslConfig = ssl.createSslConfiguration();

        listener.setSslConfiguration(sslConfig);
        listener.setImplicitSsl(true);
        DataConnectionConfigurationFactory dataConfigFactory = new DataConnectionConfigurationFactory();
        dataConfigFactory.setImplicitSsl(true);

        listener.setDataConnectionConfiguration(dataConfigFactory.createDataConnectionConfiguration());

        return listener;
    }
}
