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

package org.jbpm.integration.console.forms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class ConfigurationUtils {

    private final static String UNDEFINED_HOSTNAME = "undefined.host";

    private String webServiceHost;
    private int webServicePort;
    private MBeanServer mbeanServer;

    public String getWebServiceHost() {
        return webServiceHost;
    }

    public void setWebServiceHost(String host) throws UnknownHostException {
        if (host == null || host.trim().length() == 0) {
            System.out.println("Using undefined host: " + UNDEFINED_HOSTNAME);
            host = UNDEFINED_HOSTNAME;
        }
        if ("0.0.0.0".equals(host)) {
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println("Using local host: " + localHost.getHostName());
            host = localHost.getHostName();
        }
        this.webServiceHost = host;
    }

    public int getWebServicePort() {
        if (webServicePort <= 0)
            webServicePort = getConnectorPort("HTTP/1.1", false);

        int localPort = webServicePort;
        if (localPort <= 0) {
            // Do not initialize webServicePort with the default, the connector
            // port may become available later
            System.out.println("Unable to calculate 'WebServicePort', using default '8080'");
            localPort = 8080;
        }

        return localPort;
    }

    private int getConnectorPort(final String protocol, final boolean secure) {
        int port = -1;

        try {
            ObjectName connectors = new ObjectName("jboss.web:type=Connector,*");

            Set<?> connectorNames = getMbeanServer().queryNames(connectors, null);
            for (Object current : connectorNames) {
                ObjectName currentName = (ObjectName) current;

                try {
                    int connectorPort = (Integer) getMbeanServer()
                            .getAttribute(currentName, "port");
                    boolean connectorSecure = (Boolean) getMbeanServer()
                            .getAttribute(currentName, "secure");
                    String connectorProtocol = (String) getMbeanServer()
                            .getAttribute(currentName, "protocol");

                    if (protocol.equals(connectorProtocol)
                            && secure == connectorSecure) {
                        if (port > -1) {
                            System.out.println("Found multiple connectors for protocol='"
                                    + protocol + "' and secure='" + secure
                                    + "', using first port found '" + port + "'");
                        } else {
                            port = connectorPort;
                        }
                    }
                } catch (AttributeNotFoundException ignored) {
                }
            }

            return port;
        } catch (JMException e) {
            return -1;
        }
    }

    public MBeanServer getMbeanServer() {
        return mbeanServer;
    }

    public void setMbeanServer(MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
    }

}
