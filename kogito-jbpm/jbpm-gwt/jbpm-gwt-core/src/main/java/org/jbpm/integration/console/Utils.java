/**
 * Copyright 2012 JBoss Inc
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
package org.jbpm.integration.console;

import java.util.Properties;

public class Utils {
    
    public static final String DEFAULT_TASK_SERVICE_STRATEGY = "HornetQ";
    public static final String DEFAULT_IP_ADDRESS = "127.0.0.1";
    public static final int DEFAULT_PORT = 5153;

    public static String getTaskServiceHost(Properties properties) {
        String host = properties.getProperty("jbpm.console.task.service.host", DEFAULT_IP_ADDRESS);
        
        return host;
    }
    
    public static int getTaskServicePort(Properties properties) {
        String strategy = properties.getProperty("jbpm.console.task.service.strategy", DEFAULT_TASK_SERVICE_STRATEGY);
        String defaultPort = "-1";
        if ("Mina".equalsIgnoreCase(strategy)) {
            defaultPort = "9123";
            
        } else if ("HornetQ".equalsIgnoreCase(strategy)) {
            defaultPort = "5153";
        }
        String port = properties.getProperty("jbpm.console.task.service.port", defaultPort);
        
        return Integer.parseInt(port);
    }
}
