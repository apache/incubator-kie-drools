package org.drools.brms.server;

import org.drools.brms.client.rpc.RepositoryService;
import org.drools.brms.client.rpc.TableConfig;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class JBRMSServiceServlet extends RemoteServiceServlet
    implements
    RepositoryService {

    private static final long serialVersionUID = 3150768417428383474L;

    public String[] loadChildCategories(String categoryPath) {
        log("loadChildCategories", "loading cat path: " + categoryPath);
        sleep( 500 );
        return new String[] { "Cat 1", "Cat 2", "Cat 3"};
    }

    public String[][] loadRuleListForCategories(String categoryPath,
                                                String status) {
        log("loading rule list", "for cat path: " + categoryPath);
        String[][] data = { { "Rule 1", "Production", "mark", "2" },
                            { "Rule 2", "Production", "mark", "2" },
                            { "Rule 3", "Production", "mark", "2" }};
        return data;
    }

    public TableConfig loadTableConfig(String listName) {
        log("loading table config", listName);
        sleep(300);        
        final TableConfig config = new TableConfig();

                config.headers = new String[] {"name", "status", "last updated by", "version"};
                config.rowsPerPage = 30;
        return config;
    }

    private void sleep(int ms)  {
        try {
            Thread.sleep( ms );
        } catch ( InterruptedException e ) {           
            e.printStackTrace();
        }
    }
    
    private void log(String serviceName,
                     String message) {
        System.out.println("[" + serviceName + "] " + message);
    }
    

}
