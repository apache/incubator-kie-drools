/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.memorycompiler.resources;

/**
 * A Store is where the compilers are storing the results
 */
public interface ResourceStore {

    void write( KiePath resourcePath, byte[] pResourceData );
    default void write( String resourceName, byte[] pResourceData ) {
        write( KiePath.of(resourceName), pResourceData );
    }

    void write( KiePath resourcePath, byte[] pResourceData, boolean createFolder );
    default void write( String resourceName, byte[] pResourceData, boolean createFolder ) {
        write( KiePath.of(resourceName), pResourceData, createFolder );
    }

    byte[] read( KiePath resourcePath );
    default byte[] read( String resourceName ) {
        return read( KiePath.of(resourceName) );
    }

    void remove( KiePath resourcePath );
    default void remove( String resourceName ) {
        remove( KiePath.of(resourceName) );
    }
}
