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
package org.jbpm.integration.console.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyLoader {
    
    private static final Logger logger = LoggerFactory.getLogger(PropertyLoader.class);
    private static Properties jbpmConsoleProperties = new Properties();

    /**
     * This method loads the jbpm console properties, if they haven't been loaded already. 
     * It allows to provide custom configuration that is outside of the application archive. 
     * If not given it will use one that is embedded in the console as default one. 
     */
    public static Properties getJbpmConsoleProperties() { 

        if( ! jbpmConsoleProperties.isEmpty() ) { 
            return jbpmConsoleProperties;
        }
        
        InputStream consolePropertiesStream = null;
        try {
            consolePropertiesStream = getStreamForConfigFile("/jbpm.console.properties", "/default.jbpm.console.properties");
            
            
            jbpmConsoleProperties.load(consolePropertiesStream);
        } catch (IOException e) {
            throw new RuntimeException("Could not load jbpm.console.properties", e);
        } finally {
            if (consolePropertiesStream != null) {
                try {
                    consolePropertiesStream.close();
                } catch (IOException e) {
                    logger.error("Error closing console properties stream, e");
                }
            }
        }
        
        return jbpmConsoleProperties;
    }
    
    public static InputStream getStreamForConfigFile(String file, String defaultFile) throws IOException {
        InputStream consolePropertiesStream = null;
        String consolePropertiesPath = System.getProperty("jbpm.conf.dir");
        if (consolePropertiesPath == null) {
            consolePropertiesPath = System.getProperty("jboss.server.config.dir");
        }
        
        if (consolePropertiesPath == null) {
            consolePropertiesPath = defaultFile;
            consolePropertiesStream = PropertyLoader.class.getResourceAsStream(consolePropertiesPath);
        } else {
            consolePropertiesPath += file;
            File configFile = new File(consolePropertiesPath);
            if (configFile.exists()) {
                consolePropertiesStream = new FileInputStream(configFile);
            } else {
                consolePropertiesPath = defaultFile;
                consolePropertiesStream = PropertyLoader.class.getResourceAsStream(consolePropertiesPath);
            }
        }
        
        return consolePropertiesStream;
    }
}
