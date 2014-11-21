/*
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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
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
 *  <li>HandleResponseErrors - optional parameter that instructs handler to throw errors in case 
 *  of non successful response codes (other than 2XX)</li>
 * </ul>
 */
public class RESTWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(RESTWorkItemHandler.class);
	
	private String username;
	private String password;
	private AuthenticationType type;
	private String authUrl;	
	// protected for test purpose
	protected static boolean HTTP_CLIENT_API_43 = true;
	
	static {
		try {
			Class.forName("org.apache.http.client.methods.RequestBuilder");
			HTTP_CLIENT_API_43 = true;
		} catch (ClassNotFoundException e) {
			HTTP_CLIENT_API_43 = false;
		}		
	}
	
	/**
	 * Used when no authentication is required
	 */
	public RESTWorkItemHandler() {
		logger.debug("REST work item handler will use http client 4.3 api " + HTTP_CLIENT_API_43);
	}
	
	/**
	 * Dedicated constructor when BASIC authentication method shall be used
	 * @param username - user name to be used for authentication
	 * @param password - password to be used for authentication
	 */
	public RESTWorkItemHandler(String username, String password) {
		this();
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
		this();
		this.username = username;
		this.password = password;
		this.type = AuthenticationType.FORM_BASED;
		this.authUrl = authUrl;
	}

    public String getAuthUrl() {
		return authUrl;
	}

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		boolean handleException = false;
    	// extract required parameters
        String urlStr = (String) workItem.getParameter("Url");
        String method = (String) workItem.getParameter("Method");
        String handleExceptionStr = (String) workItem.getParameter("HandleResponseErrors");
        if (urlStr == null) {
            throw new IllegalArgumentException("Url is a required parameter");
        }
        if (method == null || method.trim().length() == 0) {
        	method = "GET";
        }
        if (handleExceptionStr != null) {
        	handleException = Boolean.parseBoolean(handleExceptionStr);
        }
        Map<String,Object> params = workItem.getParameters();

        // optional timeout config parameters, defaulted to 60 seconds
        Integer connectTimeout = getParamAsInt(params.get("ConnectTimeout"));
        if (connectTimeout==null) connectTimeout = 60000;
        Integer readTimeout = getParamAsInt(params.get("ReadTimeout"));
        if (readTimeout==null) readTimeout = 60000;

        HttpClient httpClient = getHttpClient(readTimeout, connectTimeout);
	        
        Object methodObject = configureRequest(method, urlStr, params);
        try {
            HttpResponse response = doRequestWithAuthorization(httpClient, methodObject, params);
        	StatusLine statusLine = response.getStatusLine();
        	int responseCode = statusLine.getStatusCode();
	        Map<String, Object> results = new HashMap<String, Object>();
	        HttpEntity respEntity = response.getEntity();
	        String responseBody = null;
	        if( respEntity != null ) { 
	            responseBody = EntityUtils.toString(respEntity);
	        }
	        if (responseCode >= 200 && responseCode < 300) {
	            postProcessResult(responseBody, results);
	            results.put("StatusMsg", "request to endpoint " + urlStr + " successfully completed " + statusLine.getReasonPhrase());
	        } else {
	        	if (handleException) {
	        		handleException(new RESTServiceException(responseCode, responseBody, urlStr));
	        	} else {
		            logger.warn("Unsuccessful response from REST server (status: {}, endpoint: {}, response: {}", 
		                    responseCode, urlStr, responseBody);
		            results.put("StatusMsg", "endpoint " + urlStr + " could not be reached: " + responseBody);
	        	}
	        }
            results.put("Status", responseCode);
            
	        // notify manager that work item has been completed
	        manager.completeWorkItem(workItem.getId(), results);
    	} catch (Exception e) {
    		handleException(e);
    	} finally {
    	    try { 
    	        close(httpClient, methodObject);
    	    } catch( Exception e ) { 
    	        // no idea if this throws something, but we still don't care!
    	    }
    	}
    }
	
	protected Integer getParamAsInt(Object param) {
		if (param == null) {
			return null;
		}
		if (param instanceof String && !((String) param).isEmpty()) {
			return Integer.parseInt((String) param);
		} if (param instanceof Number) {
			return ((Number) param).intValue();
		}
		
		return null;
	}
    
	protected void setBody(RequestBuilder builder, Map<String, Object> params) {
        if (params.containsKey("Content")) {
            try {
                String content = (String) params.get("Content");
                StringEntity entity = new StringEntity(content);
                String contentType = (String)params.get("ContentType");
                entity.setContentType(contentType);
                builder.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Cannot set body for REST request [" + builder.getMethod() + "] " + builder.getUri(), e);
            }
        }
    }
	
	protected void setBody(HttpRequestBase theMethod, Map<String, Object> params) {
        if (params.containsKey("Content")) {
            ((HttpEntityEnclosingRequestBase)theMethod).setEntity(new StringEntity((String)params.get("Content"), ContentType.create((String)params.get("ContentType"))));
        }
	}

    protected void postProcessResult(String result, Map<String, Object> results) {
        results.put("Result", result);
    }
    
    protected HttpResponse doRequestWithAuthorization(HttpClient httpclient, Object method, Map<String, Object> params) {
    	if (HTTP_CLIENT_API_43) {
    		return doRequestWithAuthorization(httpclient, (RequestBuilder) method, params);
    	} else {
    		return doRequestWithAuthorization(httpclient, (HttpRequestBase) method, params);
    	}
    }

    /**
     * This method does the actual request, including the setup for authorization. 
     * </p>
     * It is <b>not</b> responsible for cleaning up after the last request that it does.
     * </p>
     * It <i>is</i> responsible for cleaning up after all previous request, such as for form-based authentication, that happen.
     * 
     * @param httpclient The {@link HttpClient} instance
     * @param requestBuilder The {@link RequestBuilder} instance
     * @param params The parameters that may be needed for authentication
     * @return A {@link HttpResponse} instance from which we can extract the content
     */
    protected HttpResponse doRequestWithAuthorization(HttpClient httpclient, RequestBuilder requestBuilder, Map<String, Object> params) {
        // no authorization
    	if (type == null) {
    	    HttpUriRequest request = requestBuilder.build();
    	    try {
                return httpclient.execute(request);
            } catch( Exception e ) {
                throw new RuntimeException("Could not execute request [" + request.getMethod() + "] " + request.getURI(), e);
            }
    	}
    
    	// user/password
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
            // basic auth
            URI requestUri = requestBuilder.getUri();
            
        	// Create AuthCache instance and add it: so that HttpClient thinks that it has already queried (as per the HTTP spec) 
        	// - generate BASIC scheme object and add it to the local auth cache
        	AuthCache authCache = new BasicAuthCache();
        	BasicScheme basicAuth = new BasicScheme();
        	authCache.put(new HttpHost(requestUri.getHost()), basicAuth);

        	// - add AuthCache to the execution context:
        	HttpClientContext clientContext = HttpClientContext.create();
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
        	credsProvider.setCredentials(
        	    // specify host and port, since that is safer/more secure
    			new AuthScope(requestUri.getHost(), requestUri.getPort(), AuthScope.ANY_REALM),
    			new UsernamePasswordCredentials(u, p)
        	);
        	clientContext.setCredentialsProvider(credsProvider);
        	clientContext.setAuthCache(authCache);
      
        	// - execute request
        	HttpUriRequest request = requestBuilder.build();
        	try {
                return httpclient.execute(request, clientContext);
            } catch( Exception e ) {
                throw new RuntimeException("Could not execute request with preemptive authentication [" + request.getMethod() + "] " + request.getURI(), e);
            } 
        } else if (type == AuthenticationType.FORM_BASED) {
            // form auth
            
            // 1. do initial request to trigger authentication 
        	HttpUriRequest request = requestBuilder.build();
        	int statusCode = -1;
        	try {
        	    HttpResponse initialResponse = httpclient.execute(request);
        	    statusCode = initialResponse.getStatusLine().getStatusCode();
        	} catch (IOException e) {
        		throw new RuntimeException("Could not execute request for form-based authentication", e);
            } finally {
                // weird, but this is the method that releases resources, including the connection
                request.abort();
            }
 
        	// 1b. form authentication requests should have a status of 401
        	// See: www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2
        	if( statusCode != HttpStatus.SC_UNAUTHORIZED ) { 
        	    logger.error("Expected form authentication request with status {} but status on response is {}: proceeding anyways", 
        	            HttpStatus.SC_UNAUTHORIZED, statusCode);
        	}
        	
        	// 2. do POST form request to authentiate
        	String authUrlStr = (String) params.get("AuthUrl");
        	if (authUrlStr == null) {
        		authUrlStr = authUrl;
        	}
        	if (authUrlStr == null) {
                throw new IllegalArgumentException("Could not find authentication url");
            }
            HttpPost authMethod = new HttpPost(authUrlStr);
            List<NameValuePair> formParams = new ArrayList<NameValuePair>(2);
            formParams.add(new BasicNameValuePair("j_username", u));
            formParams.add(new BasicNameValuePair("j_password", p));
            UrlEncodedFormEntity formEntity;
            try {
                formEntity = new UrlEncodedFormEntity(formParams);
            } catch( UnsupportedEncodingException uee ) {
                throw new RuntimeException("Could not encode authentication parameters into request body", uee);
            }
            authMethod.setEntity(formEntity);
            try {
                httpclient.execute(authMethod);
            } catch (IOException e) {
        		throw new RuntimeException("Could not initialize form-based authentication", e);
            } finally {
                authMethod.releaseConnection();
            }
           
            // 3. rebuild request and execute
            request = requestBuilder.build();
            try {
                return httpclient.execute(request);
            } catch( Exception e ) {
                throw new RuntimeException("Could not execute request [" + request.getMethod() + "] " + request.getURI(), e);
            }
        } else {
        	throw new RuntimeException("Unknown AuthenticationType " + type);
        }
    }
    
    protected HttpResponse doRequestWithAuthorization(HttpClient httpclient, HttpRequestBase httpMethod, Map<String, Object> params) {
    	if (type == null) {
    		try {
                return httpclient.execute(httpMethod);
            } catch( Exception e ) {
                throw new RuntimeException("Could not execute request [" + httpMethod.getMethod() + "] " + httpMethod.getURI(), e);
            }
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
        	
        	HttpHost targetHost = new HttpHost(httpMethod.getURI().getHost(), httpMethod.getURI().getPort(), httpMethod.getURI().getScheme());
            ((DefaultHttpClient)httpclient).getCredentialsProvider().setCredentials(
                    new AuthScope(targetHost.getHostName(), targetHost.getPort()),
                    new UsernamePasswordCredentials(u, p));

            
            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local
            // auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost, basicAuth);

            // Add AuthCache to the execution context
            BasicHttpContext localcontext = new BasicHttpContext();
            localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);            
            
            try {
                return httpclient.execute(targetHost, httpMethod, localcontext);
            } catch( Exception e ) {
                throw new RuntimeException("Could not execute request [" + httpMethod.getMethod() + "] " + httpMethod.getURI(), e);
            }
        } else if (type == AuthenticationType.FORM_BASED) {
        	String authUrlStr = (String) params.get("AuthUrl");
        	if (authUrlStr == null) {
        		authUrlStr = authUrl;
        	}
        	if (authUrlStr == null) {
                throw new IllegalArgumentException("Could not find authentication url");
            }
        	try {
        		httpclient.execute(httpMethod);
        	} catch (IOException e) {
        		throw new RuntimeException("Could not execute request for form-based authentication", e);
            } finally {
            	httpMethod.releaseConnection();
            }
        	HttpPost authMethod = new HttpPost(authUrlStr);

            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("j_username", u));
            nvps.add(new BasicNameValuePair("j_password", p));

            authMethod.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            try {
                httpclient.execute(authMethod);
            } catch (IOException e) {
        		throw new RuntimeException("Could not initialize form-based authentication", e);
            } finally {
                authMethod.releaseConnection();
            }
            
            try {
                return httpclient.execute(httpMethod);
            } catch( Exception e ) {
                throw new RuntimeException("Could not execute request [" + httpMethod.getMethod() + "] " + httpMethod.getURI(), e);
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

    
    protected HttpClient getHttpClient(Integer readTimeout, Integer connectTimeout) {
    	
    	if (HTTP_CLIENT_API_43) {
            RequestConfig config = RequestConfig.custom()
                    .setSocketTimeout(readTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setConnectionRequestTimeout(connectTimeout)
                    .build();

            HttpClientBuilder clientBuilder = HttpClientBuilder.create()
                    .setDefaultRequestConfig(config);
            
            HttpClient httpClient = clientBuilder.build();
            
            return httpClient;

    	} else {
	        DefaultHttpClient httpClient = new DefaultHttpClient();
	        httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, readTimeout);
	        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectTimeout);
	        
	        return httpClient;
    	}
    }
    
    protected void close(HttpClient httpClient, Object httpMethod) throws IOException {
    	if (HTTP_CLIENT_API_43) {
    		((CloseableHttpClient) httpClient).close();
    	} else {
    		((HttpRequestBase)httpMethod).releaseConnection();
    	}
    }
    
    protected Object configureRequest(String method, String urlStr, Map<String, Object> params) {
    	
    	if (HTTP_CLIENT_API_43) {
            RequestBuilder builder = null;
            if ("GET".equals(method)) {
            	builder = RequestBuilder.get().setUri(urlStr);
            } else if ("POST".equals(method)) {
            	builder = RequestBuilder.post().setUri(urlStr);
            	setBody(builder, params);
            } else if ("PUT".equals(method)) {
            	builder = RequestBuilder.put().setUri(urlStr);
                setBody(builder, params);
            } else if ("DELETE".equals(method)) {
            	builder = RequestBuilder.delete().setUri(urlStr);
            } else {
                throw new IllegalArgumentException("Method " + method + " is not supported");
            }
            
            return builder;
    	} else {
    		HttpRequestBase theMethod = null;
            if ("GET".equals(method)) {
            	theMethod = new HttpGet(urlStr); 
            } else if ("POST".equals(method)) {
            	theMethod = new HttpPost(urlStr);
            	setBody(theMethod, params);
            } else if ("PUT".equals(method)) {
                theMethod = new HttpPut(urlStr);
                setBody(theMethod, params);
            } else if ("DELETE".equals(method)) {
                theMethod = new HttpDelete(urlStr);            
            } else {
                throw new IllegalArgumentException("Method " + method + " is not supported");
            }
            
            return theMethod;
    	}
    }
}
