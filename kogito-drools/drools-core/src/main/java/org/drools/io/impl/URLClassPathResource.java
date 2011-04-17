/*
 * Copyright 2011 JBoss Inc..
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.drools.io.Resource;

/**
 *
 */
public class URLClassPathResource extends BaseResource{
    
    private UrlResource resource;

    public URLClassPathResource(String path) {
        //CompositeClassLoader compositeClassLoader = ClassLoaderUtil.getClassLoader(classLoader == null ? null : new ClassLoader[] { classLoader },null,false );
        ClassLoader classLoader = URLClassPathResource.class.getClassLoader();
        System.out.println("Converting '"+path+"' to URL.");
        System.out.println("Reource1: '"+classLoader.getResource(path));
        String url = classLoader.getResource(path).toString();
        resource = new UrlResource(url);
    }
    

    @Override
    public String toString() {
        try {
            return "[URLClassPathResource url='" + (resource==null?"null":resource.getURL()) + "']";
        } catch (IOException ex) {
            Logger.getLogger(URLClassPathResource.class.getName()).log(Level.SEVERE, null, ex);
            return "[URLClassPathResource url='ERROR']";
        }
    }

    public URL getURL() throws IOException {
        return this.resource.getURL();
    }

    public boolean hasURL() {
        return this.resource.hasURL();
    }

    public boolean isDirectory() {
        return this.resource.isDirectory();
    }

    public Collection<Resource> listResources() {
        return this.resource.listResources();
    }

    public long getLastModified() {
        return this.resource.getLastModified();
    }

    public long getLastRead() {
        return this.resource.getLastRead();
    }

    public InputStream getInputStream() throws IOException {
        return this.resource.getInputStream();
    }

    public Reader getReader() throws IOException {
        return this.resource.getReader();
    }
}
