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

package org.kie.api.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;

/**
 * <p>
 * Generic interface to provide a Reader or InputStream for the underlying IO resource.
 * </p>
 */
public interface Resource extends Serializable {
    /**
     * Open an InputStream to the resource, the user must close this when finished.
     * 
     * @return InputStream to the resource
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Opens a Reader to the resource, the user must close this when finished.
     *
     * @return Reader to the resource
     * @throws IOException
     */
    Reader getReader() throws IOException;
    
    /**
     * @return source path of the resource, if one is available.
     */
    String getSourcePath();
    
    /**
     * @return the target path that should be used when writing this resource down
     * to KieFileSystem.
     */
    String getTargetPath();
    
    /**
     * Returns the type of the resource if one could be inferred by the
     * extension of the resource or if it was explicitly set.
     *
     * @return typo of the resource
     */
    ResourceType getResourceType();
    
    /**
     * @return the configuration for the resource if one is available.
     */
    ResourceConfiguration getConfiguration();
    
    /**
     * Sets this resource name. Some services require a resource to be
     * named and it is not always possible to infer a name automatically.
     * For instance, a file resource automatically infers its name from the
     * file and path, but a byte array resource has to be given a name.
     *  
     * @param path the path of the resource
     * 
     * @return the resource itself in order to use it as a fluent API
     */
    Resource setSourcePath( String path );
    
    /**
     * In case this resource should be written to a different path
     * when writing it down to the KieFileSystem, this property
     * allows the application to set such path.
     *  
     * @param path the path of the resource
     * 
     * @return the resource itself in order to use it as a fluent API
     */
    Resource setTargetPath( String path );
    
    /**
     * Sets the resource type. For some resources it is possible to infer
     * the type by the file name extension for instance. When it is not 
     * possible to infer it automatically, the user is expected to set it.
     * 
     * @param type the resource type
     * 
     * @return the resource itself in order to use it as a fluent API
     */
    Resource setResourceType( ResourceType type );
    
    /**
     * Sets the resource configuration in case it is necessary.
     * 
     * @param conf the resource configuration
     * 
     * @return the resource itself in order to use it as a fluent API
     */
    Resource setConfiguration( ResourceConfiguration conf );
}
