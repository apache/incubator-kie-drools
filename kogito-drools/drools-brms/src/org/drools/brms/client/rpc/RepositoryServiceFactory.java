package org.drools.brms.client.rpc;

import org.drools.brms.client.rpc.mock.MockRepositoryServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Creates instances of the repository service for the client code to use.
 * @author Michael Neale
 */
public class RepositoryServiceFactory {

    /**
     * Change this to switch between debug/mock mode (ie web front end only)
     * versus full RPC (which requires the back end be running).
     */
    public static boolean DEBUG = true;
    
    public static RepositoryServiceAsync getService() {
        if (DEBUG)
            return getMockService();
        return getRealService(); 
            
    }
    
    private static RepositoryServiceAsync getMockService() {
        return new MockRepositoryServiceAsync();
    }

    private static RepositoryServiceAsync getRealService() {
        // define the service you want to call        
        RepositoryServiceAsync svc =
            (RepositoryServiceAsync) GWT.create(RepositoryService.class);
        ServiceDefTarget endpoint = (ServiceDefTarget) svc;
        endpoint.setServiceEntryPoint("/jbrmsService");
        return svc;
    }
    
}
