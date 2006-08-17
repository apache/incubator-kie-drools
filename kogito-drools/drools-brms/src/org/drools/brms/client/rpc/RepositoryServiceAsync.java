package org.drools.brms.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

/** 
 * This is the interface that the front end talks to.
 * 
 * As per the GWT standard, this follows the signatures of <code>RespositoryService</code>
 * with the AsyncCallback banged on the end of the method sigs (rather then return a value).
 * 
 * @author Michael Neale
 */
public interface RepositoryServiceAsync {

    /**
     * @param categoryPath A "/" delimited 
     * @param callback
     */
    public void loadChildCategories(String categoryPath, AsyncCallback callback);
    
}
