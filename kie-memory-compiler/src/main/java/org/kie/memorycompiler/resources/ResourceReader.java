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
package org.kie.memorycompiler.resources;

import java.util.Collection;

import org.drools.util.PortablePath;

/**
 * A ResourceReader provide access to resource like e.g. source code
 */
public interface ResourceReader {

    boolean isAvailable( PortablePath resourcePath );
    default boolean isAvailable( String resourceName ) {
        return isAvailable(PortablePath.of(resourceName));
    }

    byte[] getBytes( final PortablePath resourcePath );
    default byte[] getBytes( String resourceName ) {
        return getBytes(PortablePath.of(resourceName));
    }

    Collection<PortablePath> getFilePaths();

    void mark();
    Collection<String> getModifiedResourcesSinceLastMark();
}
