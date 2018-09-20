/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.verifier;

import java.util.HashMap;
import java.util.Map;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

public class VerifierConfigurationImpl
    implements
    VerifierConfiguration {

    protected Map<Resource, ResourceType> verifyingResources = new HashMap<Resource, ResourceType>();
    private Map<String, String>           properties         = new HashMap<String, String>();

    public String getProperty(String name) {
        return properties.get( name );
    }

    public void setProperty(String name,
                            String value) {
        properties.put( name,
                        value );
    }

    public Map<Resource, ResourceType> getVerifyingResources() {
        return verifyingResources;
    }

}
