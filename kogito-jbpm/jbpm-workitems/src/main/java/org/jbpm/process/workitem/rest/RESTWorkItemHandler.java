/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.drools.core.process.instance.WorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

public class RESTWorkItemHandler implements WorkItemHandler {
	
	private String username;
	private String password;
	private AuthenticationType type;
	private String authUrl;
	
	public RESTWorkItemHandler() {
	}
	
	public RESTWorkItemHandler(String username, String password) {
		this.username = username;
		this.password = password;
		this.type = AuthenticationType.BASIC;
	}

	public RESTWorkItemHandler(String username, String password, String authUrl) {
		this.username = username;
		this.password = password;
		this.type = AuthenticationType.FORM_BASED;
		this.authUrl = authUrl;
	}

    public String getAuthUrl() {
		return authUrl;
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    	// extract required parameters
        String urlStr = (String) workItem.getParameter("Url");
        String method = (String) workItem.getParameter("Method");
        if (urlStr == null) {
            throw new IllegalArgumentException("Url is a required parameter");
        }
        if (method == null || method.trim().length() == 0) {
        	method = "GET";
        }
        Map<String,Object> params = workItem.getParameters();

        // optional timeout config parameters, defaulted to 60 seconds
        Integer connectTimeout = (Integer) params.get("ConnectTimeout");
        if (connectTimeout==null) connectTimeout = 60000;
        Integer readTimeout = (Integer) params.get("ReadTimeout");
        if (readTimeout==null) readTimeout = 60000;

        HttpClient httpclient = new HttpClient();
        httpclient.setConnectionTimeout(connectTimeout);
        httpclient.setTimeout(readTimeout);
	        
        HttpMethod theMethod = null;
        if ("GET".equals(method)) {
        	theMethod = new GetMethod(urlStr); 
        } else if ("POST".equals(method)) {
        	theMethod = new PostMethod(urlStr);
        }
        doAuthorization(httpclient, theMethod, params);
        try {
        	int responseCode = httpclient.executeMethod(theMethod);
	        Map<String, Object> results = new HashMap<String, Object>();
	        if (responseCode >= 200 && responseCode < 300) {
	        	theMethod.getResponseBody();
	            postProcessResult(theMethod.getResponseBodyAsString(), results);
	        } else {
	            results.put("Status", responseCode);
	            results.put("StatusMsg", "endpoint " + urlStr + " could not be reached: " + theMethod.getResponseBodyAsString());
	        }
	        // notify manager that work item has been completed
	        manager.completeWorkItem(workItem.getId(), results);
    	} catch (Exception e) {
    		e.printStackTrace();
    		manager.abortWorkItem(workItem.getId());
    	} finally {
    		theMethod.releaseConnection();
    	}
    }
    
    protected void postProcessResult(String result, Map<String, Object> results) {
        results.put("Result", result);
    }

    protected void doAuthorization(HttpClient httpclient, HttpMethod method, Map<String, Object> params) {
    	if (type == null) {
    		return;
    	}
    	String u = (String) params.get("Username");
    	String p = (String) params.get("Password");
    	if (u == null || p == null) {
    		u = this.username;
    		p = this.password;
    	}
        if (u == null) {
        	throw new IllegalArgumentException("Could not find username");
        }
        if (p == null) {
        	throw new IllegalArgumentException("Could not find password");
        }
        if (type == AuthenticationType.BASIC) {
        	httpclient.getState().setCredentials(
    			new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM),
    			new UsernamePasswordCredentials(u, p)
        	);
        	method.setDoAuthentication(true);
        } else if (type == AuthenticationType.FORM_BASED) {
        	String authUrlStr = (String) params.get("AuthUrl");
        	if (authUrlStr == null) {
        		authUrlStr = authUrl;
        	}
        	if (authUrlStr == null) {
                throw new IllegalArgumentException("Could not find authentication url");
            }
        	try {
        		httpclient.executeMethod(method);
        	} catch (IOException e) {
        		throw new RuntimeException("Could not execute request for form-based authentication", e);
            } finally {
            	method.releaseConnection();
            }
            PostMethod authMethod = new PostMethod(authUrlStr);
            NameValuePair[] data = {
        		new NameValuePair("j_username", u),
                new NameValuePair("j_password", p)
    		};
            authMethod.setRequestBody(data);
            try {
                httpclient.executeMethod(authMethod);
            } catch (IOException e) {
        		throw new RuntimeException("Could not initialize form-based authentication", e);
            } finally {
                authMethod.releaseConnection();
            }
        } else {
        	throw new RuntimeException("Unknown AuthenticationType " + type);
        }
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // Do nothing, this work item cannot be aborted
    }
    
    public enum AuthenticationType {
    	BASIC,
    	FORM_BASED
    }

}
