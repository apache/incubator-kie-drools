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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;

import org.drools.definition.KnowledgeDescr;
import org.drools.io.Resource;
import org.drools.io.internal.InternalResource;

public class DescrResource extends BaseResource implements InternalResource {
    private static final long serialVersionUID = 3931132608413160031L;
    
    private KnowledgeDescr descr;
    
    public DescrResource(KnowledgeDescr descr ) {
        if ( descr == null ) {
            throw new IllegalArgumentException( "descr cannot be null" );
        }
        this.descr = descr;
    }
    
    public URL getURL() throws IOException {
        throw new FileNotFoundException( "descr cannot be resolved to URL");
    }

    public InputStream getInputStream() throws IOException {
        throw new IOException( "descr does not support input streams");
    }
    
    public Reader getReader() throws IOException {
        throw new IOException( "descr does not support readers");
    }

    public long getLastModified() {
        throw new IllegalStateException( "descr does not have a modified date" );
    }
    
    public long getLastRead() {
        throw new IllegalStateException( "descr does not have a modified date" );
    }
    
    public KnowledgeDescr getDescr() {
        return this.descr;
    }
    
    public boolean isDirectory() {
        return false;
    }

    public Collection<Resource> listResources() {
        throw new RuntimeException( "This Resource cannot be listed, or is not a directory" );
    }
    
    public boolean hasURL() {
        return false;
    }
    
    public String toString() {
        return "[DescrResource resource=" + this.descr + "']";
    }

}
