package org.drools.brms.client.rpc.mock;

import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.TableConfig;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This is a repository back end simulator. 
 */
public class MockRepositoryServiceAsync
    implements
    RepositoryServiceAsync {


    public void loadChildCategories(String categoryPath,
                                 AsyncCallback callback) {

        final AsyncCallback cb = callback;
        final String cat = categoryPath;
        Timer t = new Timer() {
            public void run() {
                log("loadChildCategories", "loading cat path: " + cat);
                String[] result = new String[] { "Cat 1", "Cat 2", "Cat 3"};
                cb.onSuccess( result );                
            }            
        };        
        t.schedule( 500 );
        
    }
    
    
    
    private void log(String serviceName,
                     String message) {
        System.out.println("[" + serviceName + "] " + message);
    }



    public void loadRuleListForCategories(String categoryPath,
                                          String status,
                                          AsyncCallback callback) {
        log("loading rule list", "for cat path: " + categoryPath);
        String[][] data = { { "Rule 1", "Production", "mark", "2" },
                            { "Rule 2", "Production", "mark", "2" },
                            { "Rule 3", "Production", "mark", "2" }};
        callback.onSuccess( data );
        
    }



    public void loadTableConfig(String listName,
                                AsyncCallback callback) {
        log("loading table config", listName);
        final TableConfig config = new TableConfig();
        final AsyncCallback cb = callback;
        Timer t = new Timer() {

            public void run() {
                config.headers = new String[] {"name", "status", "last updated by", "version"};
                config.rowsPerPage = 30;
                cb.onSuccess( config );
            }
            
        };
        t.schedule( 300 );

        
    }

}
