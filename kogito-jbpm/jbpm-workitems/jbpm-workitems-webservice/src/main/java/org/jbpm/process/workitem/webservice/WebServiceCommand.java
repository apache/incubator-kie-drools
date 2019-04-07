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

package org.jbpm.process.workitem.webservice;

import java.lang.reflect.Array;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.namespace.QName;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.dynamic.DynamicClientFactory;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.cxf.message.Message;
import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.runtime.Cacheable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.configuration.security.AuthorizationPolicy;

/**
 * Web Service executor command that executes web service call using Apache CXF.
 * It expects following parameters to be able to operate:
 * <ul>
 * <li>Interface - valid interface/service name of the web service (port type name from wsdl)</li>
 * <li>Operation - valid operation name</li>
 * <li>Parameter - object that is going to be used as web service message</li>
 * <li>Url - location of the wsdl file used to look up service definition</li>
 * <li>Namespace - name space of the web service</li>
 * <li>Endpoint - overrides the endpoint address defined in the referenced WSDL.</li>
 * <li>Username - username for authentication (optional)</li>
 * <li>Password - password for authentication (optional)</li>
 * </ul>
 * <p>
 * Web service call is synchronous but since it's executor command it will be invoked as asynchronous task any way.
 */
public class WebServiceCommand implements Command,
                                          Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(WebServiceCommand.class);
    private volatile static ConcurrentHashMap<String, Client> clients = new ConcurrentHashMap<String, Client>();
    private DynamicClientFactory dcf = null;

    @Override
    public ExecutionResults execute(CommandContext ctx) throws Exception {
        // since JaxWsDynamicClientFactory will change the TCCL we need to restore it after creating client
        ClassLoader origClassloader = Thread.currentThread().getContextClassLoader();
        try {
            Object[] parameters = null;
            WorkItem workItem = (WorkItem) ctx.getData("workItem");

            String interfaceRef = (String) workItem.getParameter("Interface");
            String operationRef = (String) workItem.getParameter("Operation");
            String endpointAddress = (String) workItem.getParameter("Endpoint");
            String username = (String) workItem.getParameter("Username");
            String password = (String) workItem.getParameter("Password");

            if (workItem.getParameter("Parameter") instanceof Object[]) {
                parameters = (Object[]) workItem.getParameter("Parameter");
            } else if (workItem.getParameter("Parameter") != null && workItem.getParameter("Parameter").getClass().isArray()) {
                int length = Array.getLength(workItem.getParameter("Parameter"));
                parameters = new Object[length];
                for (int i = 0; i < length; i++) {
                    parameters[i] = Array.get(workItem.getParameter("Parameter"),
                                              i);
                }
            } else {
                parameters = new Object[]{workItem.getParameter("Parameter")};
            }

            Client client = getWSClient(workItem,
                                        interfaceRef,
                                        ctx);

            //Override endpoint address if configured.
            if (endpointAddress != null && !"".equals(endpointAddress)) {
                client.getRequestContext().put(Message.ENDPOINT_ADDRESS,
                                               endpointAddress);
            }

            // apply authorization if needed
            applyAuthorization(username, password, client);

            Object[] result = client.invoke(operationRef,
                                            parameters);

            ExecutionResults results = new ExecutionResults();

            if (result == null || result.length == 0) {
                results.setData("Result",
                                null);
            } else {
                results.setData("Result",
                                result[0]);
            }
            logger.debug("Received sync response {}",
                         result);

            return results;
        } finally {
            Thread.currentThread().setContextClassLoader(origClassloader);
        }
    }

    protected synchronized Client getWSClient(WorkItem workItem,
                                              String interfaceRef,
                                              CommandContext ctx) {
        if (clients.containsKey(interfaceRef)) {
            return clients.get(interfaceRef);
        }

        String importLocation = (String) workItem.getParameter("Url");
        String importNamespace = (String) workItem.getParameter("Namespace");
        if (importLocation != null && importLocation.trim().length() > 0
                && importNamespace != null && importNamespace.trim().length() > 0) {

            Client client = getDynamicClientFactory(ctx).createClient(importLocation,
                                                                      new QName(importNamespace,
                                                                                interfaceRef),
                                                                      Thread.currentThread().getContextClassLoader(),
                                                                      null);
            clients.put(interfaceRef,
                        client);
            return client;
        }

        return null;
    }

    protected synchronized DynamicClientFactory getDynamicClientFactory(CommandContext ctx) {
        if (this.dcf == null) {
            this.dcf = JaxWsDynamicClientFactory.newInstance();
        }
        return this.dcf;
    }

    protected void applyAuthorization(String userName, String password, Client client) {
        if(userName != null && password != null) {
            HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
            AuthorizationPolicy authorizationPolicy = new AuthorizationPolicy();
            authorizationPolicy.setUserName(userName);
            authorizationPolicy.setPassword(password);

            authorizationPolicy.setAuthorizationType("Basic");
            httpConduit.setAuthorization(authorizationPolicy);
        } else {
            logger.warn("UserName and Password must be provided to set the authorization policy.");
        }
    }

    @Override
    public void close() {
        if (clients != null) {
            for (Client client : clients.values()) {
                client.destroy();
            }
        }
    }

    public void setClients(ConcurrentHashMap<String, Client> clients) {
        this.clients = clients;
    }
}