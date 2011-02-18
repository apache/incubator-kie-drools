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

package org.drools.examples.conway;

import java.util.ResourceBundle;

/**
 * A utility class for retrieving application properties
 * 
 * @author <a href="mailto:brown_j@ociweb.com">Jeff Brown</a>
 */
public class ConwayApplicationProperties {
    private static final ConwayApplicationProperties ourInstance = new ConwayApplicationProperties();

    public static ConwayApplicationProperties getInstance() {
        return ConwayApplicationProperties.ourInstance;
    }

    private final ResourceBundle resources;

    private ConwayApplicationProperties() {
        this.resources = ResourceBundle.getBundle( "org.drools.examples.conway.conway" );
    }

    public static String getProperty(final String propertyName) {
        return ConwayApplicationProperties.ourInstance.resources.getString( propertyName );
    }
}
