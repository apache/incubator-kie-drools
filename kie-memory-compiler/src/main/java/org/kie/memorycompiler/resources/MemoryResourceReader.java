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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A memory based reader to compile from memory
 */
public class MemoryResourceReader implements ResourceReader {
    
    private final Map<KiePath, byte[]> resources = new ConcurrentHashMap<>();

    private Set<String> modifiedResourcesSinceLastMark;

    public boolean isAvailable( KiePath path ) {
        return resources.containsKey(path);
    }
    
    public void add( final String resourceName, final byte[] pContent ) {
        KiePath normalizedName = KiePath.of(resourceName);
        resources.put(normalizedName, pContent);
        if (modifiedResourcesSinceLastMark != null) {
            modifiedResourcesSinceLastMark.add(normalizedName.asString());
        }
    }
    
    public void remove( KiePath path ) {
        resources.remove(path);
    }

    public void mark() {
        modifiedResourcesSinceLastMark = new HashSet<>();
    }

    public Collection<String> getModifiedResourcesSinceLastMark() {
        return modifiedResourcesSinceLastMark;
    }

    public byte[] getBytes( KiePath path ) {
        return resources.get(path);
    }

    public Collection<KiePath> getFilePaths() {
        return resources.keySet();
    }
}
