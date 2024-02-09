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

import java.util.HashMap;
import java.util.Map;

import org.drools.util.PortablePath;

public class MemoryResourceStore implements ResourceStore {

    private final Map<PortablePath, byte[]> resources = new HashMap<>();

    @Override
    public void write(PortablePath resourcePath, byte[] pResourceData ) {
        resources.put( resourcePath, pResourceData );
    }

    @Override
    public void write(PortablePath resourcePath, byte[] pResourceData, boolean createFolder ) {
        resources.put( resourcePath, pResourceData );
    }

    @Override
    public byte[] read( PortablePath resourcePath ) {
        return resources.get( resourcePath );
    }

    @Override
    public void remove( PortablePath resourcePath ) {
        resources.remove( resourcePath );
    }

    public Map<PortablePath, byte[]> getResources() {
        return resources;
    }
}
