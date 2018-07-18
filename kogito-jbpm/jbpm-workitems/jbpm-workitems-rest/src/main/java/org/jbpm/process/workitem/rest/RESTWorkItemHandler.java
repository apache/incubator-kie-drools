/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.rest;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.xml.bind.JAXBContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
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
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.drools.core.util.StringUtils;
import org.jbpm.process.workitem.core.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * WorkItemHandler that is capable of interacting with REST service. Supports both types of services
 * secured (that requires authentication) and open (no authentication). Authentication methods currently supported:
 * <ul>
 * <li>BASIC</li>
 * <li>FORM BASED</li>
 * </ul>
 * Authentication information can be given on handler initialization and can be overridden via work item parameters.
 * All other configuration options must be given via work item parameters map:
 * <ul>
 * <li>Url - resource location to be invoked - mandatory</li>
 * <li>Method - HTTP method that will be executed - defaults to GET</li>
 * <li>ContentType - data type in case of sending data - mandatory for POST,PUT</li>
 * <li>ContentData - actual data to be sent - mandatory for POST,PUT</li>
 * <li>ConnectTimeout - connection time out - default to 60 seconds</li>
 * <li>ReadTimeout - read time out - default to 60 seconds</li>
 * <li>Username - user name for authentication - overrides one given on handler initialization)</li>
 * <li>Password - password for authentication - overrides one given on handler initialization)</li>
 * <li>AuthUrl - url that is handling authentication (usually j_security_check url)</li>
 * <li>HandleResponseErrors - optional parameter that instructs handler to throw errors in case
 * of non successful response codes (other than 2XX)</li>
 * <li>ResultClass - fully qualified class name of the class that response should be transformed to,
 * if not given string format will be returned</li>
 * <li>AcceptHeader - accept header value</li>
 * <li>Headers - additional HTTP Headers in format: header1=val1;header2=val2</li>
 * </ul>
 */
public class RESTWorkItemHandler extends AbstractLogOrThrowWorkItemHandler implements Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(RESTWorkItemHandler.class);
    private static final int DEFAULT_TOTAL_POOL_CONNECTIONS = 500;
    private static final int DEFAULT_MAX_POOL_CONNECTIONS_PER_ROUTE = 50;

    public static final String PARAM_AUTH_TYPE = "AuthType";
    public static final String PARAM_CONNECT_TIMEOUT = "ConnectTimeout";
    public static final String PARAM_READ_TIMEOUT = "ReadTimeout";
    public static final String PARAM_CONTENT_TYPE = "ContentType";
    public static final String PARAM_HEADERS = "Headers";
    public static final String PARAM_CONTENT = "Content";
    public static final String PARAM_CONTENT_DATA = "ContentData";
    public static final String PARAM_USERNAME = "Username";
    public static final String PARAM_PASSWORD = "Password";
    public static final String PARAM_AUTHURL = "AuthUrl";
    public static final String PARAM_RESULT = "Result";
    public static final String PARAM_STATUS = "Status";
    public static final String PARAM_STATUS_MSG = "StatusMsg";

    private String username;
    private String password;
    private AuthenticationType type;
    private String authUrl;
    // default caching to false
    private boolean doCacheClient = false;

    private ClassLoader classLoader;

    protected static PoolingHttpClientConnectionManager connectionManager;
    protected static CloseableHttpClient cachedClient;

    // protected for test purpose
    protected static boolean HTTP_CLIENT_API_43 = true;

    static {
        try {
            Class.forName("org.apache.http.client.methods.RequestBuilder");
            HTTP_CLIENT_API_43 = true;
        } catch (ClassNotFoundException e) {
            HTTP_CLIENT_API_43 = false;
        }

        // setup pooling connection manager with default connections totals
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(DEFAULT_TOTAL_POOL_CONNECTIONS);
        connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_POOL_CONNECTIONS_PER_ROUTE);
    }

    /**
     * Used when no authentication is required
     */
    public RESTWorkItemHandler() {
        logger.debug("REST work item handler will use http client 4.3 api " + HTTP_CLIENT_API_43);
        this.type = AuthenticationType.NONE;
        this.classLoader = this.getClass().getClassLoader();
    }

    /**
     * Used when no authentication is required
     * @param doCacheClient - - true/false setting for client cache
     */
    public RESTWorkItemHandler(boolean doCacheClient) {
        this();
        this.doCacheClient = doCacheClient;
    }

    /**
     * Used when no authentication is required
     * @param doCacheClient - - true/false setting for client cache
     * @param totalPoolConnections - max pool connections if caching client is used
     * @param maxPoolConnectionsPerRoute - max pool connections per route if caching client is used
     */
    public RESTWorkItemHandler(boolean doCacheClient,
                               int totalPoolConnections,
                               int maxPoolConnectionsPerRoute) {
        this();
        this.doCacheClient = doCacheClient;
        if (doCacheClient) {
            connectionManager.setMaxTotal(totalPoolConnections);
            connectionManager.setDefaultMaxPerRoute(maxPoolConnectionsPerRoute);
        }
    }

    /**
     * Dedicated constructor when BASIC authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     */
    public RESTWorkItemHandler(String username,
                               String password) {
        this(username,
             password,
             false);
    }

    /**
     * Dedicated constructor when BASIC authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     * @param doCacheClient - true/false setting for client cache
     */
    public RESTWorkItemHandler(String username,
                               String password,
                               boolean doCacheClient) {
        this(username,
             password,
             doCacheClient,
             DEFAULT_TOTAL_POOL_CONNECTIONS,
             DEFAULT_MAX_POOL_CONNECTIONS_PER_ROUTE);
    }

    /**
     * Dedicated constructor when BASIC authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     * @param doCacheClient - true/false setting for client cache
     * @param totalPoolConnections - max pool connections if caching client is used
     * @param maxPoolConnectionsPerRoute - max pool connections per route if caching client is used
     */
    public RESTWorkItemHandler(String username,
                               String password,
                               boolean doCacheClient,
                               int totalPoolConnections,
                               int maxPoolConnectionsPerRoute) {
        this();
        this.username = username;
        this.password = password;
        this.type = AuthenticationType.BASIC;
        this.classLoader = this.getClass().getClassLoader();
        this.doCacheClient = doCacheClient;
        if (doCacheClient) {
            connectionManager.setMaxTotal(totalPoolConnections);
            connectionManager.setDefaultMaxPerRoute(maxPoolConnectionsPerRoute);
        }
    }

    /**
     * Dedicated constructor when FORM BASED authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     * @param authUrl - authentication url to be used
     */
    public RESTWorkItemHandler(String username,
                               String password,
                               String authUrl) {
        this(username,
             password,
             authUrl,
             false);
    }

    /**
     * Dedicated constructor when FORM BASED authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     * @param authUrl - authentication url to be used
     * @param doCacheClient - true/false setting for client cache
     */
    public RESTWorkItemHandler(String username,
                               String password,
                               String authUrl,
                               boolean doCacheClient) {
        this(username,
             password,
             authUrl,
             doCacheClient,
             DEFAULT_TOTAL_POOL_CONNECTIONS,
             DEFAULT_MAX_POOL_CONNECTIONS_PER_ROUTE);
    }

    /**
     * Dedicated constructor when FORM BASED authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     * @param authUrl - authentication url to be used
     * @param doCacheClient - true/false setting for client cache
     * @param totalPoolConnections - max pool connections if caching client is used
     * @param maxPoolConnectionsPerRoute - max pool connections per route if caching client is used
     */
    public RESTWorkItemHandler(String username,
                               String password,
                               String authUrl,
                               boolean doCacheClient,
                               int totalPoolConnections,
                               int maxPoolConnectionsPerRoute) {
        this();
        this.username = username;
        this.password = password;
        this.type = AuthenticationType.FORM_BASED;
        this.authUrl = authUrl;
        this.classLoader = this.getClass().getClassLoader();
        this.doCacheClient = doCacheClient;
        if (doCacheClient) {
            connectionManager.setMaxTotal(totalPoolConnections);
            connectionManager.setDefaultMaxPerRoute(maxPoolConnectionsPerRoute);
        }
    }

    /**
     * Used when no authentication is required
     * @param classLoader - given classloader
     */
    public RESTWorkItemHandler(ClassLoader classLoader) {
        this(classLoader,
             false);
    }

    /**
     * Used when no authentication is required
     * @param classLoader - given classloader
     * @param doCacheClient - true/false setting for client cache
     */
    public RESTWorkItemHandler(ClassLoader classLoader,
                               boolean doCacheClient) {
        logger.debug("REST work item handler will use http client 4.3 api " + HTTP_CLIENT_API_43);
        this.type = AuthenticationType.NONE;
        this.classLoader = classLoader;
        this.doCacheClient = doCacheClient;
    }

    /**
     * Used when no authentication is required
     * @param classLoader - given classloader
     * @param doCacheClient - true/false setting for client cache
     * @param totalPoolConnections - max pool connections if caching client is used
     * @param maxPoolConnectionsPerRoute - max pool connections per route if caching client is used
     */
    public RESTWorkItemHandler(ClassLoader classLoader,
                               boolean doCacheClient,
                               int totalPoolConnections,
                               int maxPoolConnectionsPerRoute) {
        logger.debug("REST work item handler will use http client 4.3 api " + HTTP_CLIENT_API_43);
        this.type = AuthenticationType.NONE;
        this.classLoader = classLoader;
        this.doCacheClient = doCacheClient;
        if (doCacheClient) {
            connectionManager.setMaxTotal(totalPoolConnections);
            connectionManager.setDefaultMaxPerRoute(maxPoolConnectionsPerRoute);
        }
    }

    /**
     * Dedicated constructor when BASIC authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     * @param classLoader - given classloader
     */
    public RESTWorkItemHandler(String username,
                               String password,
                               ClassLoader classLoader) {
        this(username,
             password,
             classLoader,
             false);
    }

    /**
     * Dedicated constructor when BASIC authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     * @param classLoader - given classloader
     * @param doCacheClient - true/false setting for client cache
     */
    public RESTWorkItemHandler(String username,
                               String password,
                               ClassLoader classLoader,
                               boolean doCacheClient) {
        this(username,
             password,
             classLoader,
             doCacheClient,
             DEFAULT_TOTAL_POOL_CONNECTIONS,
             DEFAULT_MAX_POOL_CONNECTIONS_PER_ROUTE);
    }

    /**
     * Dedicated constructor when BASIC authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     * @param classLoader - given classloader
     * @param doCacheClient - true/false setting for client cache
     * @param totalPoolConnections - max pool connections if caching client is used
     * @param maxPoolConnectionsPerRoute - max pool connections per route if caching client is used
     */
    public RESTWorkItemHandler(String username,
                               String password,
                               ClassLoader classLoader,
                               boolean doCacheClient,
                               int totalPoolConnections,
                               int maxPoolConnectionsPerRoute) {
        this();
        this.username = username;
        this.password = password;
        this.type = AuthenticationType.BASIC;
        this.classLoader = classLoader;
        this.doCacheClient = doCacheClient;
        if (doCacheClient) {
            connectionManager.setMaxTotal(totalPoolConnections);
            connectionManager.setDefaultMaxPerRoute(maxPoolConnectionsPerRoute);
        }
    }

    /**
     * Dedicated constructor when FORM BASED authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     * @param authUrl - authentication url to be used
     * @param classLoader - given classloader
     */
    public RESTWorkItemHandler(String username,
                               String password,
                               String authUrl,
                               ClassLoader classLoader) {
        this(username,
             password,
             authUrl,
             classLoader,
             false);
    }

    /**
     * Dedicated constructor when FORM BASED authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     * @param authUrl - authentication url to be used
     * @param classLoader - given classloader
     * @param doCacheClient - true/false setting for client cache
     */
    public RESTWorkItemHandler(String username,
                               String password,
                               String authUrl,
                               ClassLoader classLoader,
                               boolean doCacheClient) {
        this(username,
             password,
             authUrl,
             classLoader,
             doCacheClient,
             DEFAULT_TOTAL_POOL_CONNECTIONS,
             DEFAULT_MAX_POOL_CONNECTIONS_PER_ROUTE);
    }

    /**
     * Dedicated constructor when FORM BASED authentication method shall be used
     * @param username - user name to be used for authentication
     * @param password - password to be used for authentication
     * @param authUrl - authentication url to be used
     * @param classLoader - given classloader
     * @param doCacheClient - true/false setting for client cache
     * @param totalPoolConnections - max pool connections if caching client is used
     * @param maxPoolConnectionsPerRoute - max pool connections per route if caching client is used
     */
    public RESTWorkItemHandler(String username,
                               String password,
                               String authUrl,
                               ClassLoader classLoader,
                               boolean doCacheClient,
                               int totalPoolConnections,
                               int maxPoolConnectionsPerRoute) {
        this();
        this.username = username;
        this.password = password;
        this.type = AuthenticationType.FORM_BASED;
        this.authUrl = authUrl;
        this.classLoader = classLoader;
        this.doCacheClient = doCacheClient;
        if (doCacheClient) {
            connectionManager.setMaxTotal(totalPoolConnections);
            connectionManager.setDefaultMaxPerRoute(maxPoolConnectionsPerRoute);
        }
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public boolean getDoCacheClient() {
        return doCacheClient;
    }

    public void executeWorkItem(WorkItem workItem,
                                WorkItemManager manager) {
        boolean handleException = false;
        // extract required parameters
        String urlStr = (String) workItem.getParameter("Url");
        String method = (String) workItem.getParameter("Method");
        String handleExceptionStr = (String) workItem.getParameter("HandleResponseErrors");
        String resultClass = (String) workItem.getParameter("ResultClass");
        String acceptHeader = (String) workItem.getParameter("AcceptHeader");
        String acceptCharset = (String) workItem.getParameter("AcceptCharset");
        String headers = (String) workItem.getParameter(PARAM_HEADERS);

        if (urlStr == null) {
            throw new IllegalArgumentException("Url is a required parameter");
        }
        if (method == null || method.trim().length() == 0) {
            method = "GET";
        }
        if (handleExceptionStr != null) {
            handleException = Boolean.parseBoolean(handleExceptionStr);
        }
        Map<String, Object> params = workItem.getParameters();

        // authentication type from parameters
        AuthenticationType authType = type;
        if (params.get(PARAM_AUTH_TYPE) != null) {
            authType = AuthenticationType.valueOf((String) params.get(PARAM_AUTH_TYPE));
        }

        // optional timeout config parameters, defaulted to 60 seconds
        Integer connectTimeout = getParamAsInt(params.get(PARAM_CONNECT_TIMEOUT));
        if (connectTimeout == null) {
            connectTimeout = 60000;
        }
        Integer readTimeout = getParamAsInt(params.get(PARAM_READ_TIMEOUT));
        if (readTimeout == null) {
            readTimeout = 60000;
        }
        if(headers == null) {
            headers = "";
        }

        HttpClient httpClient = getHttpClient(readTimeout,
                                              connectTimeout);

        Object methodObject = configureRequest(method,
                                               urlStr,
                                               params,
                                               acceptHeader,
                                               acceptCharset,
                                               headers);
        try {
            HttpResponse response = doRequestWithAuthorization(httpClient,
                                                               methodObject,
                                                               params,
                                                               authType);
            StatusLine statusLine = response.getStatusLine();
            int responseCode = statusLine.getStatusCode();
            Map<String, Object> results = new HashMap<String, Object>();
            HttpEntity respEntity = response.getEntity();
            String responseBody = null;
            String contentType = null;
            if (respEntity != null) {
                responseBody = EntityUtils.toString(respEntity, acceptCharset);

                if (respEntity.getContentType() != null) {
                    contentType = respEntity.getContentType().getValue();
                }
            }
            if (responseCode >= 200 && responseCode < 300) {
                postProcessResult(responseBody,
                                  resultClass,
                                  contentType,
                                  results);
                results.put(PARAM_STATUS_MSG,
                            "request to endpoint " + urlStr + " successfully completed " + statusLine.getReasonPhrase());
            } else {
                if (handleException) {
                    handleException(new RESTServiceException(responseCode,
                                                             responseBody,
                                                             urlStr));
                } else {
                    logger.warn("Unsuccessful response from REST server (status: {}, endpoint: {}, response: {}",
                                responseCode,
                                urlStr,
                                responseBody);
                    results.put(PARAM_STATUS_MSG,
                                "endpoint " + urlStr + " could not be reached: " + responseBody);
                }
            }
            results.put(PARAM_STATUS,
                        responseCode);

            // notify manager that work item has been completed
            manager.completeWorkItem(workItem.getId(),
                                     results);
        } catch (Exception e) {
            handleException(e);
        } finally {
            try {
                close(httpClient,
                      methodObject);
            } catch (Exception e) {
                handleException(e);
            }
        }
    }

    protected Integer getParamAsInt(Object param) {
        if (param == null) {
            return null;
        }
        if (param instanceof String && !((String) param).isEmpty()) {
            return Integer.parseInt((String) param);
        }
        if (param instanceof Number) {
            return ((Number) param).intValue();
        }

        return null;
    }

    protected void setCharset(RequestBuilder builder,
                              String charset) {
        if (charset != null) {
            builder.addHeader(HttpHeaders.ACCEPT_CHARSET, charset);
        }
    }

    protected void addAcceptHeader(RequestBuilder builder,
                                   String value) {
        if (value != null) {
            builder.addHeader(HttpHeaders.ACCEPT,
                              value);
        }
    }

    protected void setCharset(HttpRequestBase theMethod,
                              String charset) {
        if (charset != null) {
            theMethod.addHeader(HttpHeaders.ACCEPT_CHARSET, 
                                charset);
        }
    }

    protected void addAcceptHeader(HttpRequestBase theMethod,
                                   String value) {
        if (value != null) {
            theMethod.addHeader(HttpHeaders.ACCEPT,
                                value);
        }
    }

    protected void addCustomHeaders(String headers,
            BiConsumer<String, String> consumer) {
        for(String h : headers.split(";")) {
             String[] headerParts = h.split("=");
             if(headerParts.length == 2) {
                 consumer.accept(headerParts[0], headerParts[1]);
             } 
        }
    } 
    
    protected void setBody(RequestBuilder builder,
                           Map<String, Object> params) {
        // backwards compat to "Content" parameter
        if (params.containsKey(PARAM_CONTENT_DATA) || params.containsKey(PARAM_CONTENT)) {
            try {
                Object content = params.get(PARAM_CONTENT_DATA) != null ? params.get(PARAM_CONTENT_DATA) : params.get(PARAM_CONTENT);
                if (!(content instanceof String)) {
                    content = transformRequest(content,
                                               (String) params.get(PARAM_CONTENT_TYPE));
                }
                StringEntity entity = new StringEntity((String) content,
                                                       ContentType.parse((String) params.get(PARAM_CONTENT_TYPE)));
                builder.setEntity(entity);
            } catch (UnsupportedCharsetException e) {
                throw new RuntimeException("Cannot set body for REST request [" + builder.getMethod() + "] " + builder.getUri(),
                                           e);
            }
        }
    }

    protected void setBody(HttpRequestBase theMethod,
                           Map<String, Object> params) {
        // backwards compat to "Content" parameter
        if (params.containsKey(PARAM_CONTENT_DATA) || params.containsKey(PARAM_CONTENT)) {
            Object content = params.get(PARAM_CONTENT_DATA) != null ? params.get(PARAM_CONTENT_DATA) : params.get(PARAM_CONTENT);
            if (!(content instanceof String)) {
                content = transformRequest(content,
                                           (String) params.get(PARAM_CONTENT_TYPE));
            }
            ((HttpEntityEnclosingRequestBase) theMethod).setEntity(new StringEntity((String) content,
                                                                                    ContentType.parse((String) params.get(PARAM_CONTENT_TYPE))));
        }
    }

    protected void postProcessResult(String result,
                                     String resultClass,
                                     String contentType,
                                     Map<String, Object> results) {
        if (!StringUtils.isEmpty(resultClass) && !StringUtils.isEmpty(contentType)) {
            try {
                Class<?> clazz = Class.forName(resultClass,
                                               true,
                                               classLoader);

                Object resultObject = transformResult(clazz,
                                                      contentType,
                                                      result);

                results.put(PARAM_RESULT,
                            resultObject);
            } catch (Throwable e) {
                throw new RuntimeException("Unable to transform respose to object",
                                           e);
            }
        } else {

            results.put(PARAM_RESULT,
                        result);
        }
    }

    protected String transformRequest(Object data,
                                      String contentType) {
        try {
            if (contentType.toLowerCase().contains("application/json")) {
                ObjectMapper mapper = new ObjectMapper();

                return mapper.writeValueAsString(data);
            } else if (contentType.toLowerCase().contains("application/xml")) {
                StringWriter stringRep = new StringWriter();
                JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{data.getClass()});

                jaxbContext.createMarshaller().marshal(data,
                                                       stringRep);

                return stringRep.toString();
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to transform request to object",
                                       e);
        }
        throw new IllegalArgumentException("Unable to find transformer for content type '" + contentType + "' to handle data " + data);
    }

    protected Object transformResult(Class<?> clazz,
                                     String contentType,
                                     String content) throws Exception {

        if (contentType.toLowerCase().contains("application/json")) {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(content,
                                    clazz);
        } else if (contentType.toLowerCase().contains("application/xml")) {
            StringReader result = new StringReader(content);
            JAXBContext jaxbContext = JAXBContext.newInstance(new Class[]{clazz});

            return jaxbContext.createUnmarshaller().unmarshal(result);
        }
        logger.warn("Unable to find transformer for content type '{}' to handle for content '{}'",
                    contentType,
                    content);
        // unknown content type, returning string representation
        return content;
    }

    protected HttpResponse doRequestWithAuthorization(HttpClient httpclient,
                                                      Object method,
                                                      Map<String, Object> params,
                                                      AuthenticationType authType) {
        if (HTTP_CLIENT_API_43) {
            return doRequestWithAuthorization(httpclient,
                                              (RequestBuilder) method,
                                              params,
                                              authType);
        } else {
            return doRequestWithAuthorization(httpclient,
                                              (HttpRequestBase) method,
                                              params,
                                              authType);
        }
    }

    /**
     * This method does the actual request, including the setup for authorization.
     * </p>
     * It is <b>not</b> responsible for cleaning up after the last request that it does.
     * </p>
     * It <i>is</i> responsible for cleaning up after all previous request, such as for form-based authentication, that happen.
     * @param httpclient The {@link HttpClient} instance
     * @param requestBuilder The {@link RequestBuilder} instance
     * @param params The parameters that may be needed for authentication
     * @return A {@link HttpResponse} instance from which we can extract the content
     */
    protected HttpResponse doRequestWithAuthorization(HttpClient httpclient,
                                                      RequestBuilder requestBuilder,
                                                      Map<String, Object> params,
                                                      AuthenticationType type) {
        // no authorization
        if (type == null || type == AuthenticationType.NONE) {
            HttpUriRequest request = requestBuilder.build();
            try {
                return httpclient.execute(request);
            } catch (Exception e) {
                throw new RuntimeException("Could not execute request [" + request.getMethod() + "] " + request.getURI(),
                                           e);
            }
        }

        // user/password
        String u = (String) params.get(PARAM_USERNAME);
        String p = (String) params.get(PARAM_PASSWORD);
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

            HttpHost targetHost = new HttpHost(requestUri.getHost(),
                                               requestUri.getPort(),
                                               requestUri.getScheme());

            // Create AuthCache instance and add it: so that HttpClient thinks that it has already queried (as per the HTTP spec)
            // - generate BASIC scheme object and add it to the local auth cache
            AuthCache authCache = new BasicAuthCache();
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost,
                          basicAuth);

            // - add AuthCache to the execution context:
            HttpClientContext clientContext = HttpClientContext.create();
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                    // specify host and port, since that is safer/more secure
                    new AuthScope(requestUri.getHost(),
                                  requestUri.getPort(),
                                  AuthScope.ANY_REALM),
                    new UsernamePasswordCredentials(u,
                                                    p)
            );
            clientContext.setCredentialsProvider(credsProvider);
            clientContext.setAuthCache(authCache);

            // - execute request
            HttpUriRequest request = requestBuilder.build();
            try {
                return httpclient.execute(targetHost,
                                          request,
                                          clientContext);
            } catch (Exception e) {
                throw new RuntimeException("Could not execute request with preemptive authentication [" + request.getMethod() + "] " + request.getURI(),
                                           e);
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
                throw new RuntimeException("Could not execute request for form-based authentication",
                                           e);
            } finally {
                // weird, but this is the method that releases resources, including the connection
                request.abort();
            }

            // 1b. form authentication requests should have a status of 401
            // See: www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2
            if (statusCode != HttpStatus.SC_UNAUTHORIZED) {
                logger.error("Expected form authentication request with status {} but status on response is {}: proceeding anyways",
                             HttpStatus.SC_UNAUTHORIZED,
                             statusCode);
            }

            // 2. do POST form request to authentiate
            String authUrlStr = (String) params.get(PARAM_AUTHURL);
            if (authUrlStr == null) {
                authUrlStr = authUrl;
            }
            if (authUrlStr == null) {
                throw new IllegalArgumentException("Could not find authentication url");
            }
            HttpPost authMethod = new HttpPost(authUrlStr);
            List<NameValuePair> formParams = new ArrayList<NameValuePair>(2);
            formParams.add(new BasicNameValuePair("j_username",
                                                  u));
            formParams.add(new BasicNameValuePair("j_password",
                                                  p));
            UrlEncodedFormEntity formEntity;
            try {
                formEntity = new UrlEncodedFormEntity(formParams);
            } catch (UnsupportedEncodingException uee) {
                throw new RuntimeException("Could not encode authentication parameters into request body",
                                           uee);
            }
            authMethod.setEntity(formEntity);
            try {
                httpclient.execute(authMethod);
            } catch (IOException e) {
                throw new RuntimeException("Could not initialize form-based authentication",
                                           e);
            } finally {
                authMethod.releaseConnection();
            }

            // 3. rebuild request and execute
            request = requestBuilder.build();
            try {
                return httpclient.execute(request);
            } catch (Exception e) {
                throw new RuntimeException("Could not execute request [" + request.getMethod() + "] " + request.getURI(),
                                           e);
            }
        } else {
            throw new RuntimeException("Unknown AuthenticationType " + type);
        }
    }

    protected HttpResponse doRequestWithAuthorization(HttpClient httpclient,
                                                      HttpRequestBase httpMethod,
                                                      Map<String, Object> params,
                                                      AuthenticationType type) {
        if (type == null || type == AuthenticationType.NONE) {
            try {
                return httpclient.execute(httpMethod);
            } catch (Exception e) {
                throw new RuntimeException("Could not execute request [" + httpMethod.getMethod() + "] " + httpMethod.getURI(),
                                           e);
            }
        }
        String u = (String) params.get(PARAM_USERNAME);
        String p = (String) params.get(PARAM_PASSWORD);
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

            HttpHost targetHost = new HttpHost(httpMethod.getURI().getHost(),
                                               httpMethod.getURI().getPort(),
                                               httpMethod.getURI().getScheme());
            ((DefaultHttpClient) httpclient).getCredentialsProvider().setCredentials(
                    new AuthScope(targetHost.getHostName(),
                                  targetHost.getPort()),
                    new UsernamePasswordCredentials(u,
                                                    p));

            // Create AuthCache instance
            AuthCache authCache = new BasicAuthCache();
            // Generate BASIC scheme object and add it to the local
            // auth cache
            BasicScheme basicAuth = new BasicScheme();
            authCache.put(targetHost,
                          basicAuth);

            // Add AuthCache to the execution context
            BasicHttpContext localcontext = new BasicHttpContext();
            localcontext.setAttribute(ClientContext.AUTH_CACHE,
                                      authCache);

            try {
                return httpclient.execute(targetHost,
                                          httpMethod,
                                          localcontext);
            } catch (Exception e) {
                throw new RuntimeException("Could not execute request [" + httpMethod.getMethod() + "] " + httpMethod.getURI(),
                                           e);
            }
        } else if (type == AuthenticationType.FORM_BASED) {
            String authUrlStr = (String) params.get(PARAM_AUTHURL);
            if (authUrlStr == null) {
                authUrlStr = authUrl;
            }
            if (authUrlStr == null) {
                throw new IllegalArgumentException("Could not find authentication url");
            }
            try {
                httpclient.execute(httpMethod);
            } catch (IOException e) {
                throw new RuntimeException("Could not execute request for form-based authentication",
                                           e);
            } finally {
                httpMethod.releaseConnection();
            }
            HttpPost authMethod = new HttpPost(authUrlStr);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("j_username",
                                            u));
            nvps.add(new BasicNameValuePair("j_password",
                                            p));

            authMethod.setEntity(new UrlEncodedFormEntity(nvps,
                                                          Consts.UTF_8));
            try {
                httpclient.execute(authMethod);
            } catch (IOException e) {
                throw new RuntimeException("Could not initialize form-based authentication",
                                           e);
            } finally {
                authMethod.releaseConnection();
            }

            try {
                return httpclient.execute(httpMethod);
            } catch (Exception e) {
                throw new RuntimeException("Could not execute request [" + httpMethod.getMethod() + "] " + httpMethod.getURI(),
                                           e);
            }
        } else {
            throw new RuntimeException("Unknown AuthenticationType " + type);
        }
    }

    public void abortWorkItem(WorkItem workItem,
                              WorkItemManager manager) {
        // Do nothing, this work item cannot be aborted
    }

    public enum AuthenticationType {
        NONE,
        BASIC,
        FORM_BASED
    }

    protected HttpClient getHttpClient(Integer readTimeout,
                                       Integer connectTimeout) {
        if (getDoCacheClient() && HTTP_CLIENT_API_43) {
            if (cachedClient == null) {
                cachedClient = getNewPooledHttpClient(readTimeout,
                                                      connectTimeout);
            }

            return cachedClient;
        }

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
            httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT,
                                                   readTimeout);
            httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
                                                   connectTimeout);

            return httpClient;
        }
    }

    protected CloseableHttpClient getNewPooledHttpClient(Integer readTimeout,
                                                         Integer connectTimeout) {

        RequestConfig config = RequestConfig.custom()
                .setSocketTimeout(readTimeout)
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(connectTimeout)
                .build();

        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config)
                .build();
    }

    protected void close(HttpClient httpClient,
                         Object httpMethod) throws IOException {
        if (HTTP_CLIENT_API_43) {
            // dont close if we are using pooled connections
            if (cachedClient == null) {
                ((CloseableHttpClient) httpClient).close();
            }
        } else {
            ((HttpRequestBase) httpMethod).releaseConnection();
        }
    }

    protected Object configureRequest(String method,
                                      String urlStr,
                                      Map<String, Object> params,
                                      String acceptHeaderValue,
                                      String acceptCharset,
                                      String httpHeaders) {

        if (HTTP_CLIENT_API_43) {
            RequestBuilder builder = null;
            if ("GET".equals(method)) {
                builder = RequestBuilder.get().setUri(urlStr);
                addAcceptHeader(builder,
                                acceptHeaderValue);
                setCharset(builder,
                           acceptCharset);
            } else if ("POST".equals(method)) {
                builder = RequestBuilder.post().setUri(urlStr);
                setBody(builder,
                        params);
                addAcceptHeader(builder,
                                acceptHeaderValue);
            } else if ("PUT".equals(method)) {
                builder = RequestBuilder.put().setUri(urlStr);
                setBody(builder,
                        params);
                addAcceptHeader(builder,
                                acceptHeaderValue);
            } else if ("DELETE".equals(method)) {
                builder = RequestBuilder.delete().setUri(urlStr);
            } else {
                throw new IllegalArgumentException("Method " + method + " is not supported");
            }
            addCustomHeaders(httpHeaders, builder::addHeader);
            return builder;
        } else {
            HttpRequestBase theMethod = null;
            if ("GET".equals(method)) {
                theMethod = new HttpGet(urlStr);
                addAcceptHeader(theMethod,
                                acceptHeaderValue);
                setCharset(theMethod, acceptCharset);
            } else if ("POST".equals(method)) {
                theMethod = new HttpPost(urlStr);
                setBody(theMethod,
                        params);
                addAcceptHeader(theMethod,
                                acceptHeaderValue);
            } else if ("PUT".equals(method)) {
                theMethod = new HttpPut(urlStr);
                setBody(theMethod,
                        params);
                addAcceptHeader(theMethod,
                                acceptHeaderValue);
            } else if ("DELETE".equals(method)) {
                theMethod = new HttpDelete(urlStr);
            } else {
                throw new IllegalArgumentException("Method " + method + " is not supported");
            }
            addCustomHeaders(httpHeaders, theMethod::addHeader);
            return theMethod;
        }
    }

    @Override
    public void close() {
        try {
            if (cachedClient != null) {
                cachedClient.close();
            }
        } catch (Exception e) {
            logger.error("Unable to close cached client connection: " + e.getMessage());
        }
    }
}