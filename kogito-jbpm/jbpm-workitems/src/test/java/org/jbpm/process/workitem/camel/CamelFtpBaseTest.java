package org.jbpm.process.workitem.camel;

import org.apache.commons.io.FileUtils;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.usermanager.UserFactory;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.ftpserver.listener.Listener;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class CamelFtpBaseTest extends AbstractBaseTest {

    protected final static String USER = "testUser";
    protected final static String PASSWD = "testPasswd";
    protected final static String HOST = "localhost";
    protected final static Integer PORT = 2221;

    protected File ftpRoot;
    protected File testFile;

    protected FtpServer server;

    /**
     * Start the FTP server, create & clean home directory
     * */
    @Before
    public void initialize() throws FtpException, IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        ftpRoot = new File(tempDir, "ftp");

        if(ftpRoot.exists()) {
            FileUtils.deleteDirectory(ftpRoot);
        }

        boolean created = ftpRoot.mkdir();
        if(! created) {
            throw new IllegalArgumentException("FTP root directory has not been created, " +
                    "check system property java.io.tmp");
        }
        String fileName = "test_file_" + CamelFtpTest.class.getName() + "_" + UUID.randomUUID().toString();

        File testDir = new File(ftpRoot, "testDirectory");
        testFile = new File(testDir, fileName);

        server = configureFtpServer(new FtpServerBuilder());
        server.start();
    }

    @After
    public void clean() throws IOException {
        if (server != null && !server.isStopped()) {
            server.stop();
        }
    }

    protected abstract FtpServer configureFtpServer(FtpServerBuilder builder) throws FtpException;

    /**
     * Builder to ease the FTP server configuration.
     * */
    protected class FtpServerBuilder {
        private FtpServerFactory ftpServerFactory;

        public FtpServerBuilder() {
            ftpServerFactory = new FtpServerFactory();
        }

        public FtpServerBuilder registerListener(final String listenerName, final Listener listener) {
            ftpServerFactory.addListener(listenerName, listener);
            return this;
        }

        public FtpServerBuilder registerDefaultListener(final Listener listener) {
            return registerListener("default", listener);
        }

        public FtpServerBuilder addUser(final String username, final String password, final File home,
                                        final boolean write) throws FtpException {
            UserFactory userFactory = new UserFactory();
            userFactory.setHomeDirectory(home.getAbsolutePath());
            userFactory.setName(username);
            userFactory.setPassword(password);

            if(write) {
                List<Authority> authorities = new ArrayList<Authority>();
                Authority writePermission = new WritePermission();
                authorities.add(writePermission);
                userFactory.setAuthorities(authorities);
            }
            User user = userFactory.createUser();
            ftpServerFactory.getUserManager().save(user);

            return this;
        }

        public FtpServer build() {
            return ftpServerFactory.createServer();
        }

        public FtpServerFactory getFtpServerFactory() {
            return ftpServerFactory;
        }
    }
}
