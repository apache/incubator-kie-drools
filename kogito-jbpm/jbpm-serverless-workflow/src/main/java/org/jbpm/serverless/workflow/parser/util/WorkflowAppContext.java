/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.serverless.workflow.parser.util;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowAppContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowAppContext.class);

    private static final String APP_PROPERTIES_FILE_NAME = "application.properties";
    private static final String DEFAULT_PROP_VALUE = "";

    private Properties applicationProperties;

    public static WorkflowAppContext ofAppResources() {
        Properties properties = new Properties();

        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES_FILE_NAME)) {
            properties.load(is);
        } catch (Exception e) {
            LOGGER.debug("Unable to load {}", APP_PROPERTIES_FILE_NAME);
        }
        return new WorkflowAppContext(properties);
    }

    public static WorkflowAppContext ofProperties(Properties props) {
        return new WorkflowAppContext(props);
    }

    private WorkflowAppContext(Properties properties) {
        this.applicationProperties = properties;
    }

    public String getApplicationProperty(String key) {
        if (applicationProperties != null && applicationProperties.containsKey(key)) {
            return applicationProperties.getProperty(key);
        } else {
            return DEFAULT_PROP_VALUE;
        }
    }

    public Properties getApplicationProperties() {
        return this.applicationProperties;
    }

}
