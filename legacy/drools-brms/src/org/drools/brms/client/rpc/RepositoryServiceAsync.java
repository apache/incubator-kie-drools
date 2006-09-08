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
     * @param categoryPath A "/" delimited path to a category. 
     * @param callback
     */
    public void loadChildCategories(String categoryPath, AsyncCallback callback);
 
    
    /**
     * Return a a 2d array/grid of results for rules.
     * @param A "/" delimited path to a category.
     * @param status The status flag. Leave blank to be all.
     */
    public void loadRuleListForCategories(String categoryPath, String status, AsyncCallback callback);
    
    /**
     * This will return a TableConfig of header names.
     * @param listName The name of the list that we are going to render.
     */
    public void loadTableConfig(String listName, AsyncCallback callback);
}
