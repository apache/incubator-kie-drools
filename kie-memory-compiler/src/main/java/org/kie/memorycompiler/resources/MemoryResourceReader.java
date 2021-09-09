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

import static org.kie.memorycompiler.resources.PathUtils.normalizePath;

/**
 * A memory based reader to compile from memory
 */
public class MemoryResourceReader implements ResourceReader {
    
    private final Map<String, byte[]> resources = new ConcurrentHashMap<>();

    private Set<String> modifiedResourcesSinceLastMark;

    public boolean isAvailable( final String resourceName ) {
        return resources.containsKey(normalizePath(resourceName));
    }
    
    public void add( final String resourceName, final byte[] pContent ) {
        String normalizedName = normalizePath(resourceName);
        resources.put(normalizedName, pContent);
        if (modifiedResourcesSinceLastMark != null) {
            modifiedResourcesSinceLastMark.add(normalizedName);
        }
    }
    
    public void remove( final String resourceName ) {
        resources.remove(normalizePath(resourceName));
    }

    public void mark() {
        modifiedResourcesSinceLastMark = new HashSet<>();
    }

    public Collection<String> getModifiedResourcesSinceLastMark() {
        return modifiedResourcesSinceLastMark;
    }

    public byte[] getBytes( final String resourceName ) {
        return resources.get(normalizePath(resourceName));
    }

    public Collection<String> getFileNames() {
        return resources.keySet();
    }
    /**
     * @deprecated
     */
    public String[] list() {
        return resources.keySet().toArray(new String[resources.size()]);
    }
}
