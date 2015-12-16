/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.io;

import java.util.Properties;

import org.kie.api.io.ResourceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceTypeImpl {
    public static final String                    KIE_RESOURCE_CONF_CLASS = "kie.resource.conf.class";
    
    private static final Logger                    logger                  = LoggerFactory.getLogger( ResourceTypeImpl.class );

    public static Properties toProperties(ResourceConfiguration conf) {
        Properties prop = conf.toProperties();
        prop.setProperty( KIE_RESOURCE_CONF_CLASS, conf.getClass().getName() );
        return prop;
    }

    public static ResourceConfiguration fromProperties(Properties prop) {
        String className = prop.getProperty( KIE_RESOURCE_CONF_CLASS );
        try {
            // not sure how to get the proper classloader here, but the resource configurations
            // should be accessible from the current classloader
            ClassLoader cl = ResourceConfiguration.class.getClassLoader();
            Class<ResourceConfiguration> confClass = (Class<ResourceConfiguration>) cl.loadClass(className);
            ResourceConfiguration conf = confClass.newInstance();
            conf.fromProperties( prop );
            return conf;
        } catch ( Exception e ) {
            logger.error( "Error loading resource configuration from properties", e );
        }
        return null;
    }

    

}
