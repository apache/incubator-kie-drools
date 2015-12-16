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

package org.drools.core.io.impl;

import org.drools.core.io.internal.InternalResource;
import org.drools.core.util.IoUtils;
import org.kie.api.io.Resource;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;

public class EncodedResource  extends BaseResource implements InternalResource, Externalizable {
    private InternalResource resource;

    private String encoding;


    /**
     * Create a new EncodedResource for the given Resource,
     * not specifying a specific encoding.
     * @param resource the Resource to hold
     */
    public EncodedResource(Resource resource) {
        this(resource, null);
    }

    /**
     * Create a new EncodedResource for the given Resource,
     * using the specified encoding.
     * @param resource the Resource to hold
     * @param encoding the encoding to use for reading from the resource
     */
    public EncodedResource(Resource resource, String encoding) {
        if ( resource == null ) {
            throw new IllegalArgumentException( "resource cannot be null" );
        }
        this.resource = (InternalResource) resource;
        this.encoding = encoding;
        setSourcePath( resource.getSourcePath() );
        setResourceType( resource.getResourceType() );
        setConfiguration( resource.getConfiguration() );
    }
    
    @Override
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        resource = (InternalResource) in.readObject();
        encoding = (String) in.readObject();
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( resource );
        out.writeObject( encoding );
    }

    public URL getURL() throws IOException {
        return this.resource.getURL();
    }

    public boolean hasURL() {
        return this.resource.hasURL();
    }
    
    /**
     * Return the Resource held.
     */
    public final Resource getResource() {
        return this.resource;
    }

    /**
     * Return the encoding to use for reading from the resource,
     * or <code>null</code> if none specified.
     */
    public final String getEncoding() {
        return this.encoding;
    }
    
    /**
     * Open a <code>java.io.Reader</code> for the specified resource,
     * using the specified encoding (if any).
     * @throws IOException if opening the Reader failed
     */
    public Reader getReader() throws IOException {
        if (this.encoding != null) {
            return new InputStreamReader(this.resource.getInputStream(), this.encoding);
        }
        else {
            return new InputStreamReader(this.resource.getInputStream(), IoUtils.UTF8_CHARSET);
        }
    }
    
    public InputStream getInputStream() throws IOException {
        return this.resource.getInputStream();
    }
    
    public long getLastModified() {
        return this.resource.getLastModified();
    }
    
    public long getLastRead() {
        return this.resource.getLastRead();
    }
    
    public boolean isDirectory() {
        return this.resource.isDirectory();
    }

    public Collection<Resource> listResources() {
        return this.resource.listResources();
    }
    
    public String toString() {
        return "EncodedResource[resource=" + this.resource + ",encoding=" + this.encoding + "]";
    }

}
