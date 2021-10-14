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

import java.util.HashMap;
import java.util.Map;

public class MemoryResourceStore implements ResourceStore {

    private final Map<KiePath, byte[]> resources = new HashMap<>();

    @Override
    public void write( KiePath resourcePath, byte[] pResourceData ) {
        resources.put( resourcePath, pResourceData );
    }

    @Override
    public void write( KiePath resourcePath, byte[] pResourceData, boolean createFolder ) {
        resources.put( resourcePath, pResourceData );
    }

    @Override
    public byte[] read( KiePath resourcePath ) {
        return resources.get( resourcePath );
    }

    @Override
    public void remove( KiePath resourcePath ) {
        resources.remove( resourcePath );
    }

    public Map<KiePath, byte[]> getResources() {
        return resources;
    }
}
