/*
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

package org.drools.io.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.builder.ResourceType;
import org.drools.builder.ResourceConfiguration;
import org.drools.io.internal.InternalResource;

public abstract class BaseResource
    implements
    InternalResource {
    private ResourceType         resourceType;
    private ResourceConfiguration configuration;

    private String name;
    private String description;

    private List<String> categories;
    
    public ResourceConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ResourceConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getCategories() {
        if ( categories == null ) {
            categories = new ArrayList<String>();
        }
        return categories;
    }

    public void setCategories( String categories ) {
        List list = getCategories();
        list.clear();
        if ( categories != null ) {
            StringTokenizer tok = new StringTokenizer( categories, "," );
            while ( tok.hasMoreTokens() ) {
                list.add( tok.nextToken() );
            }
        }
    }

    public void addCategory( String tag ) {
        getCategories().add( tag );
    }

}
