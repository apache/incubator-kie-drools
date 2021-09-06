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

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A memory based reader to compile from memory
 */
public class MemoryResourceReader implements ResourceReader {
    
    private final Map<Path, byte[]> resources = new ConcurrentHashMap<>();

    private Set<Path> modifiedResourcesSinceLastMark;

    @Override
    public boolean isAvailable( Path resourcePath ) {
        return resources.containsKey(resourcePath);
    }
    
    public void add( Path resourcePath, byte[] pContent ) {
        resources.put(resourcePath, pContent);
        if (modifiedResourcesSinceLastMark != null) {
            modifiedResourcesSinceLastMark.add(resourcePath);
        }
    }

    @Override
    public void mark() {
        modifiedResourcesSinceLastMark = new HashSet<>();
    }

    @Override
    public Collection<Path> getModifiedResourcesSinceLastMark() {
        return modifiedResourcesSinceLastMark;
    }

    @Override
    public byte[] getBytes( Path resourcePath ) {
        return resources.get(resourcePath);
    }

    @Override
    public Collection<Path> getFilePaths() {
        if ( resources == null ) {
            return Collections.emptySet();
        }
        
        return resources.keySet();       
    }
}
