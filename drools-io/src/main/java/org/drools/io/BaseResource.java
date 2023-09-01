/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.io;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.util.PortablePath;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

import static org.drools.util.IoUtils.readBytesFromInputStream;

public abstract class BaseResource
        implements
        InternalResource,
        Externalizable {
    private ResourceType              resourceType;
    private ResourceConfigurationImpl configuration;

    private PortablePath sourcePath;
    private PortablePath targetPath;
    private String                description;

    private List<String>          categories;

    protected byte[] bytes;

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        resourceType = (ResourceType) in.readObject();
        configuration = (ResourceConfigurationImpl) in.readObject();
        sourcePath = (PortablePath) in.readObject();
        targetPath = (PortablePath) in.readObject();
        description = (String) in.readObject();
        categories = (List<String>) in.readObject();
        bytes = (byte[]) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( resourceType );
        out.writeObject( configuration );
        out.writeObject( sourcePath );
        out.writeObject( targetPath );
        out.writeObject( description );
        out.writeObject( categories );
        out.writeObject( bytes );
    }

    public ResourceConfiguration getConfiguration() {
        return configuration;
    }

    public InternalResource setConfiguration(ResourceConfiguration conf) {
        if (this.configuration != null) {
            this.configuration = this.configuration.merge((ResourceConfigurationImpl)conf);
        } else {
            this.configuration = (ResourceConfigurationImpl)conf;
        }
        return this;
    }

    public InternalResource setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
        if (this.configuration == null) {
            this.configuration = new ResourceConfigurationImpl();
        }
        this.configuration.setResourceType(resourceType);
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

    public String getSourcePath() {
        return sourcePath == null ? null : sourcePath.asString();
    }

    public String getTargetPath() {
        return targetPath == null ? null : targetPath.asString();
    }

    public InternalResource setSourcePath(String path) {
        this.sourcePath = path == null ? null : PortablePath.of( path );
        return this;
    }

    public InternalResource setTargetPath(String path) {
        this.targetPath = path == null ? null : PortablePath.of( path );
        return this;
    }

    public List<String> getCategories() {
        if ( categories == null ) {
            categories = new ArrayList<>();
        }
        return categories;
    }

    public void setCategories(String categories) {
        List<String> list = getCategories();
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

    public byte[] getBytes() {
        if (bytes == null) {
            try {
                bytes = readBytesFromInputStream( getInputStream() );
            } catch (IOException e) {
                throw new RuntimeException( e );
            }
        }
        return bytes;
    }

    @Override
    public String toString() {
        return getSourcePath();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Resource)) {
            return false;
        }
        Resource that = (Resource) o;
        return getSourcePath() != null ? getSourcePath().equals(that.getSourcePath()) : that.getSourcePath() == null;

    }

    @Override
    public int hashCode() {
        return sourcePath != null ? sourcePath.hashCode() : 0;
    }
}
