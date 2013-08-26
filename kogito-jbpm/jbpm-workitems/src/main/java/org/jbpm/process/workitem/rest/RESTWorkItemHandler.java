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
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WorkItemHandler that is capable of interacting with REST service. Supports both types of services
 * secured (that requires authentication) and open (no authentication). Authentication methods currently supported:
 * <ul>
 *  <li>BASIC</li>
 *  <li>FORM BASED</li>
 * </ul>
 * Authentication information can be given on handler initialization and can be overridden via work item parameters.
 * All other configuration options must be given via work item parameters map:
 * <ul>
 *  <li>Url - resource location to be invoked - mandatory</li>
 *  <li>Method - HTTP method that will be executed - defaults to GET</li>
 *  <li>ContentType - data type in case of sending data - mandatory for POST,PUT</li>
 *  <li>Content - actual data to be sent - mandatory for POST,PUT</li>
 *  <li>ConnectTimeout - connection time out - default to 60 seconds</li>
 *  <li>ReadTimeout - read time out - default to 60 seconds</li>
 *  <li>Username - user name for authentication - overrides one given on handler initialization)</li>
 *  <li>Password - password for authentication - overrides one given on handler initialization)</li>
 *  <li>AuthUrl - url that is handling authentication (usually j_security_check url)</li>
 * </ul>
 */
public class RESTWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(RESTWorkItemHandler.class);
	
	private String username;
	private String password;
	private AuthenticationType type;
	private String authUrl;
	
	/**
	 * Used when no authentication is required
	 */
	public RESTWorkItemHandler() {
	}
	
	/**
	 * Dedicated constructor when BASIC authentication method shall be used
	 * @param username - user name to be used for authentication
	 * @param password - password to be used for authentication
	 */
	public RESTWorkItemHandler(String username, String password) {
		this.username = username;
		this.password = password;
		this.type = AuthenticationType.BASIC;
	}

	/**
	 * Dedicated constructor when FORM BASED authentication method shall be used
	 * @param username - user name to be used for authentication
	 * @param password - password to be used for authentication
	 * @param authUrl
	 */
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
        httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(connectTimeout);
        httpclient.getHttpConnectionManager().getParams().setSoTimeout(readTimeout);
	        
        HttpMethod theMethod = null;
        if ("GET".equals(method)) {
        	theMethod = new GetMethod(urlStr); 
        } else if ("POST".equals(method)) {
        	theMethod = new PostMethod(urlStr);
        	setBody(theMethod, params);
        } else if ("PUT".equals(method)) {
            theMethod = new PutMethod(urlStr);
            setBody(theMethod, params);
        } else if ("DELETE".equals(method)) {
            theMethod = new DeleteMethod(urlStr);            
        } else {
            throw new IllegalArgumentException("Method " + method + " is not supported");
        }
        doAuthorization(httpclient, theMethod, params);
        try {
        	int responseCode = httpclient.executeMethod(theMethod);
	        Map<String, Object> results = new HashMap<String, Object>();
	        if (responseCode >= 200 && responseCode < 300) {
	        	theMethod.getResponseBody();
	            postProcessResult(theMethod.getResponseBodyAsString(), results);
	        } else {
	            logger.warn("Unsuccessful response from REST server (status {}, endpoint {}, response {}", 
	                    responseCode, urlStr, theMethod.getResponseBodyAsString());
	            results.put("Status", responseCode);
	            results.put("StatusMsg", "endpoint " + urlStr + " could not be reached: " + theMethod.getResponseBodyAsString());
	        }
	        // notify manager that work item has been completed
	        manager.completeWorkItem(workItem.getId(), results);
    	} catch (Exception e) {
    		handleException(e);
    	} finally {
    		theMethod.releaseConnection();
    	}
    }
    
	protected void setBody(HttpMethod theMethod, Map<String, Object> params) {
        if (params.containsKey("Content")) {
            try {
                ((EntityEnclosingMethod)theMethod).setRequestEntity(new StringRequestEntity((String)params.get("Content"), (String)params.get("ContentType"), null));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Cannot set body for REST request " + theMethod, e);
            }
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
