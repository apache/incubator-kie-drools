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

    void write( final String pResourceName, final byte[] pResourceData );
    void write( final String pResourceName, final byte[] pResourceData, boolean createFolder );
    byte[] read( final String pResourceName );
    void remove( final String pResourceName );
}
