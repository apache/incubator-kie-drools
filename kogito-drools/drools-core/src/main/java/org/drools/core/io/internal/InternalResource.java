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

package org.drools.core.io.internal;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

public interface InternalResource extends Resource {
    InternalResource setResourceType(ResourceType resourceType);
    
    ResourceType getResourceType();
    
    ResourceConfiguration getConfiguration();

    InternalResource setConfiguration(ResourceConfiguration configuration);
    
    URL getURL() throws IOException;
    
    boolean hasURL();
    
    boolean isDirectory();
    
    Collection<Resource> listResources();
    
    long getLastModified();
    
    long getLastRead();
    
    /**
     * Returns the description of the resource. This is just a text description
     * of the resource used to add more information about it.
     * This is not a mandatory attribute
     * 
     * @return the name of the resource, or null if is not set.
     */
    String getDescription();

    void setDescription(String description); 

    List<String> getCategories();

    void setCategories( String categories );

    void addCategory( String category );

    byte[] getBytes();

    String getEncoding();
}
