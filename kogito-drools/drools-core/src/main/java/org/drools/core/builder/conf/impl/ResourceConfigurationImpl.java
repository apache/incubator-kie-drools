/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.builder.conf.impl;

import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

import java.io.Serializable;
import java.util.Properties;

import static org.kie.api.io.ResourceType.determineResourceType;

public class ResourceConfigurationImpl implements Serializable, ResourceConfiguration {

    public static final String RESOURCE_TYPE = "resource.type";

    private ResourceType resourceType;

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceConfigurationImpl merge(ResourceConfigurationImpl other) {
        other.setResourceType(resourceType);
        return other;
    }

    public Properties toProperties() {
        Properties prop = new Properties();
        if (resourceType != null) {
            prop.setProperty( RESOURCE_TYPE, resourceType.getDefaultExtension() );
        }
        return prop;
    }

    public ResourceConfiguration fromProperties(Properties prop) {
        String extension = prop.getProperty( RESOURCE_TYPE );
        if (extension != null) {
            resourceType = determineResourceType("." + extension);
        }
        return this;
    }
}
