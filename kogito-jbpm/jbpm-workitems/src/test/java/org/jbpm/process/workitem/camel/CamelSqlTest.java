package org.jbpm.process.workitem.camel;

import bitronix.tm.resource.jdbc.PoolingDataSource;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.drools.core.io.impl.ClassPathResource;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.RunScript;
import org.h2.tools.Server;
import org.jbpm.process.workitem.camel.request.RequestPayloadMapper;
import org.jbpm.process.workitem.camel.response.ResponsePayloadMapper;
import org.jbpm.process.workitem.camel.uri.SQLURIMapper;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.utils.KieHelper;

import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL endpoint test.
 * */
public class CamelSqlTest extends AbstractBaseTest {

    private static final String DB_USER = "sa";
    private static final String DB_PASSWD = "";
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/jbpm-db2;MVCC=TRUE";
    private static final String DB_DRIVER = "org.h2.Driver";
    private static final String PROCESS_DEFINITION = "/BPMN2-CamelSqlProcess.bpmn2";

    private static Server h2Server;
    private static CamelHandler handler;

    /**
     * Prepares Db and data source, which must be added to Camel registry.
     * */
    @BeforeClass
    public static void setup() throws Exception {
        DeleteDbFiles.execute("~", "jbpm-db-test", true);
        
        h2Server = Server.createTcpServer(new String[0]);
        h2Server.start();

        setupDb();

        DataSource ds = setupDataSource();

        SimpleRegistry simpleRegistry = new SimpleRegistry();
        simpleRegistry.put("myDs", ds);

        handler = new CamelHandler(new SQLURIMapper(), new RequestPayloadMapper("payload"),
                new ResponsePayloadMapper("queryResult"), new DefaultCamelContext(simpleRegistry));
    }

    /**
     * Prepares test table containing a single data row.
     * */
    private static void setupDb() throws SQLException, URISyntaxException {
        File script = new File(CamelSqlTest.class.getResource("/init-db.sql").toURI());
        RunScript.execute(DB_URL, DB_USER, DB_PASSWD, script.getAbsolutePath(), "utf-8", false);
    }

    public static PoolingDataSource setupDataSource() {
        PoolingDataSource pds = new PoolingDataSource();
        pds.setUniqueName("jdbc/jbpm-ds");
        pds.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
        pds.setMaxPoolSize(5);
        pds.setAllowLocalTransactions(true);
        pds.getDriverProperties().put("user", DB_USER);
        pds.getDriverProperties().put("password", DB_PASSWD);
        pds.getDriverProperties().put("url", DB_URL);
        pds.getDriverProperties().put("driverClassName", DB_DRIVER);
        pds.init();
        return pds;
    }

    @Test
    public void testSelect() throws Exception {
        KieHelper kieHelper = new KieHelper();
        kieHelper.addResource(new ClassPathResource(PROCESS_DEFINITION, getClass()), ResourceType.BPMN2);
        KieBase kbase = kieHelper.build();
        KieSession kieSession = kbase.newKieSession();

        kieSession.getWorkItemManager().registerWorkItemHandler("CamelSql", handler);

        String sqlQuery = "select * from TEST";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("Query", sqlQuery);
        params.put("DataSource", "myDs");

        WorkflowProcessInstance wpi =  (WorkflowProcessInstance)kieSession.startProcess("camelSqlProcess", params);
        List<Map<String, Object>> result = (List<Map<String, Object>>) wpi.getVariable("QueryResult");
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        int id = (Integer)result.get(0).get("ID");
        String name = (String) result.get(0).get("NAME");
        Assert.assertEquals(1, id);
        Assert.assertEquals("test", name);
    }


    @AfterClass
    public static void tearDown() {
        if (h2Server != null) {
            h2Server.stop();
        }
        DeleteDbFiles.execute("~", "jbpm-db-test", true);
    }
}
