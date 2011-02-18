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
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.Date;

import org.drools.io.Resource;
import org.drools.io.internal.InternalResource;

public class ReaderResource  extends BaseResource implements InternalResource {
    private Reader reader;
    private String encoding;
    
    public ReaderResource(Reader reader) {
        this(reader, null);
    }
    
    public ReaderResource(Reader reader, String encoding) {
        if ( reader == null ) {
            throw new IllegalArgumentException( "reader cannot be null" );
        }
        if ( encoding == null && reader instanceof InputStreamReader ) {
            this.encoding = ((InputStreamReader)reader).getEncoding();
        }
        this.reader = reader;

        this.encoding = encoding;
    }
    
    public URL getURL() throws IOException {
        throw new FileNotFoundException( "reader cannot be resolved to URL");
    }

    public InputStream getInputStream() throws IOException {
        if ( this.encoding != null ) {
            return new ReaderInputStream( this.reader, this.encoding);         
        } else {
            return new ReaderInputStream( this.reader);
        }
    } 
    
    public long getLastModified() {
        throw new IllegalStateException( "reader does have a modified date" );
    }    
    
    public long getLastRead() {
        throw new IllegalStateException( "reader does have a modified date" );
    }        
    
    public Reader getReader() {
        return this.reader;
    }
    
    public String getEncoding() {
        return this.encoding;
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
        return "[ReaderResource resource=" + this.reader + " encoding='" + this.encoding + "']";
    }
}
