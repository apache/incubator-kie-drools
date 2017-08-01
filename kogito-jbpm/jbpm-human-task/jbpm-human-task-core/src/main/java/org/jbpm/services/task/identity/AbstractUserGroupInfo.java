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

package org.jbpm.services.task.identity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUserGroupInfo {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractUserGroupInfo.class);

	protected Properties readProperties(String propertiesLocation, String defaultProperties) {
		Properties config = null;
		URL locationUrl = null;
        if (propertiesLocation == null) {
            propertiesLocation = defaultProperties;
        }
        
        logger.debug("Callback properties will be loaded from {}", propertiesLocation);
        if (propertiesLocation.startsWith("classpath:")) {
			String stripedLocation = propertiesLocation.replaceFirst("classpath:", "");
			locationUrl = this.getClass().getResource(stripedLocation);
	        if (locationUrl == null) {
	        	locationUrl = Thread.currentThread().getContextClassLoader().getResource(stripedLocation);
	        }
		} else {
			try {
				locationUrl = new URL(propertiesLocation);
			} catch (MalformedURLException e) {
				locationUrl = this.getClass().getResource(propertiesLocation);
		        if (locationUrl == null) {
		        	locationUrl = Thread.currentThread().getContextClassLoader().getResource(propertiesLocation);
		        }
			}
		}
        if (locationUrl != null) {
            config = new Properties();
            try {
                config.load(locationUrl.openStream());
            } catch (IOException e) {
                logger.error("Error when loading properties for DB user group callback", e);
                config = null;
            }
        }
        
        return config;
	}
}
