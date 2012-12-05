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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.io.internal.InternalResource;
import org.kie.io.ResourceConfiguration;
import org.kie.io.ResourceType;

public abstract class BaseResource
        implements
        InternalResource,
        Externalizable {
    private ResourceType          resourceType;
    private ResourceConfiguration configuration;

    private String                name;
    private String                description;

    private List<String>          categories;
    
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        resourceType = (ResourceType) in.readObject();
        configuration = (ResourceConfiguration) in.readObject();
        name = (String) in.readObject();
        description = (String) in.readObject();
        categories = (List<String>) in.readObject();
    }
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( resourceType );
        out.writeObject( configuration );
        out.writeObject( name );
        out.writeObject( description );
        out.writeObject( categories );
    }

    public ResourceConfiguration getConfiguration() {
        return configuration;
    }

    public InternalResource setConfiguration(ResourceConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    public InternalResource setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
        return this;
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

    public InternalResource setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getCategories() {
        if ( categories == null ) {
            categories = new ArrayList<String>();
        }
        return categories;
    }

    public void setCategories(String categories) {
        List list = getCategories();
        list.clear();
        if ( categories != null ) {
            StringTokenizer tok = new StringTokenizer( categories, "," );
            while ( tok.hasMoreTokens() ) {
                list.add( tok.nextToken() );
            }
        }
    }

    public void addCategory(String tag) {
        getCategories().add( tag );
    }

}
