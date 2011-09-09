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

package org.drools.io;

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
     * @return
     * @throws IOException
     */
    InputStream getInputStream() throws IOException;

    /**
     * Opens a Reader to the resource, the user most close this when finished.
     * @return
     * @throws IOException
     */
    public Reader getReader() throws IOException;
    
    /**
     * Returns the name of the resource. This is just a descriptive name of
     * the resource. 
     * This is not a mandatory attribute
     * 
     * @return the name of the resource, or null if is not set.
     */
    String getName();
    
    /**
     * Returns the description of the resource. This is just a text description
     * of the resource used to add more information about it.
     * This is not a mandatory attribute
     * 
     * @return the name of the resource, or null if is not set.
     */
    String getDescription();

}
