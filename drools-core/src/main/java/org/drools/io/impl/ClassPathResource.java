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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.util.StringUtils;
import org.drools.io.Resource;
import org.drools.io.internal.InternalResource;
import org.drools.util.ClassLoaderUtil;

/**
 * Borrowed gratuitously from Spring under ASL2.0.
 *
 *+
 */

public class ClassPathResource extends BaseResource
    implements
    InternalResource,
    Externalizable {

    private String      path;
    private String      encoding;
    private ClassLoader classLoader;
    private Class< ? >  clazz;
    private long        lastRead;

    public ClassPathResource() {

    }

    public ClassPathResource(String path) {
        this( path,
              null,
              null,
              null );
    }

    public ClassPathResource(String path,
                             Class<?> clazz) {
        this( path,
              null,
              clazz,
              null );
    }

    public ClassPathResource(String path,
                             ClassLoader classLoader) {
        this( path,
              null,
              null,
              classLoader );
    }

    public ClassPathResource(String path,
                             String encoding) {
        this( path,
              encoding,
              null,
              null );
    }

    public ClassPathResource(String path,
                             String encoding,
                             Class<?> clazz) {
        this( path,
              encoding,
              clazz,
              null );
    }

    public ClassPathResource(String path,
                             String encoding,
                             ClassLoader classLoader) {
        this( path,
              encoding,
              null,
              classLoader );
    }

    public ClassPathResource(String path,
                             String encoding,
                             Class<?> clazz,
                             ClassLoader classLoader) {
        if ( path == null ) {
            throw new IllegalArgumentException( "path cannot be null" );
        }
        this.path = path;
        this.encoding = encoding;
        this.clazz = clazz;
        this.classLoader = ClassLoaderUtil.getClassLoader( classLoader == null ? null : new ClassLoader[] { classLoader },
                                                           clazz,
                                                           false );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.path );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.path = (String) in.readObject();
    }

    /**
     * This implementation opens an InputStream for the given class path resource.
     * @see java.lang.ClassLoader#getResourceAsStream(String)
     * @see java.lang.Class#getResourceAsStream(String)
     */
    public InputStream getInputStream() throws IOException {
        //update the lastRead field
        this.lastRead = this.getLastModified();

        //Some ClassLoaders caches the result of getResourceAsStream() this is
        //why we get the Input Stream from the URL of the resource
        //@see JBRULES-2960
        return this.getURL().openStream();
    }

    /**
     * This implementation returns a URL for the underlying class path resource.
     * @see java.lang.ClassLoader#getResource(String)
     * @see java.lang.Class#getResource(String)
     */
    public URL getURL() throws IOException {
        URL url = null;
        if ( this.clazz != null ) {
            url = this.clazz.getResource( this.path );
        }

        if ( url == null ) {
            url = this.classLoader.getResource( this.path );
        }

        if ( url == null ) {
            throw new FileNotFoundException( "'" + this.path + "' cannot be opened because it does not exist" );
        }
        return url;
    }

    public boolean hasURL() {
        return true;
    }

    public long getLastModified() {
        try {
            URLConnection conn = getURL().openConnection();
            long date = conn.getLastModified();
            return date;
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get LastModified for ClasspathResource",
                                        e );
        }
    }

    public long getLastRead() {
        return this.lastRead;
    }

    public String getEncoding() {
        return encoding;
    }

    public Reader getReader() throws IOException {
        if ( this.encoding != null ) {
            return new InputStreamReader( getInputStream(), encoding );
        } else {
            return new InputStreamReader( getInputStream() );
        }
    }

    public boolean isDirectory() {
        try {
            URL url = getURL();

            if ( !"file".equals( url.getProtocol() ) ) {
                return false;
            }

            File file = new File( StringUtils.toURI( url.toString() ).getSchemeSpecificPart() );

            return file.isDirectory();
        } catch ( Exception e ) {
            return false;
        }
    }

    public Collection<Resource> listResources() {
        try {
            URL url = getURL();

            if ( "file".equals( url.getProtocol() ) ) {
                File dir = new File( StringUtils.toURI( url.toString() ).getSchemeSpecificPart() );

                List<Resource> resources = new ArrayList<Resource>();

                for ( File file : dir.listFiles() ) {
                    resources.add( new FileSystemResource( file ) );
                }

                return resources;
            }
        } catch ( Exception e ) {
            // swollow as we'll throw an exception anyway            
        }

        throw new RuntimeException( "This Resource cannot be listed, or is not a directory" );
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public String getPath() {
        return path;
    }

    protected void setLastRead(long lastRead) {
        this.lastRead = lastRead;
    }
    
    public boolean equals(Object object) {
        if ( object == null || !(object instanceof ClassPathResource) ) {
            return false;
        }

        ClassPathResource other = (ClassPathResource) object;
        if ( !this.path.equals( other.path ) ) {
            return false;
        }

        return this.clazz == other.clazz && this.classLoader == other.classLoader;
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    public String toString() {
        return "[ClassPathResource path='" + this.path + "']";
    }

}
