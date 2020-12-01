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
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A memory based reader to compile from memory
 */
public class MemoryResourceReader implements ResourceReader {
    
    private Map<String, byte[]> resources = new ConcurrentHashMap<>();

    private Set<String> modifiedResourcesSinceLastMark;

    public boolean isAvailable( final String pResourceName ) {
        if (resources == null) {
            return false;
        }

        return resources.containsKey(pResourceName);
    }
    
    public void add( final String pResourceName, final byte[] pContent ) {
        resources.put(pResourceName, pContent);
        if (modifiedResourcesSinceLastMark != null) {
            modifiedResourcesSinceLastMark.add(pResourceName);
        }
    }
    
    public void remove( final String pResourceName ) {
        if (resources != null) {
            resources.remove(pResourceName);
        }
    }

    public void mark() {
        modifiedResourcesSinceLastMark = new HashSet<String>();
    }

    public Collection<String> getModifiedResourcesSinceLastMark() {
        return modifiedResourcesSinceLastMark;
    }

    public byte[] getBytes( final String pResourceName ) {
        return (byte[]) resources.get(pResourceName);
    }

    public Collection<String> getFileNames() {
        if ( resources == null ) {
            return Collections.emptySet();
        }
        
        return resources.keySet();       
    }
    /**
     * @deprecated
     */
    public String[] list() {
        if (resources == null) {
            return new String[0];
        }
        return (String[]) resources.keySet().toArray(new String[resources.size()]);
    }
}
