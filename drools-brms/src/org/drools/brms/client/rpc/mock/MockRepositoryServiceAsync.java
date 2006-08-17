package org.drools.brms.client.rpc.mock;

import org.drools.brms.client.rpc.RepositoryServiceAsync;

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
        t.schedule( 1000 );
        
    }
    
    
    
    private void log(String serviceName,
                     String message) {
        System.out.println("[" + serviceName + "] " + message);
        
    }

}
