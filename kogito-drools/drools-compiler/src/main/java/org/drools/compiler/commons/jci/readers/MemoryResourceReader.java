/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.commons.jci.readers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A memory based reader to compile from memory
 */
public class MemoryResourceReader implements ResourceReader {
    
    private Map<String, byte[]> resources;

    private Set<String> modifiedResourcesSinceLastMark;

    public boolean isAvailable( final String pResourceName ) {
        if (resources == null) {
            return false;
        }

        return resources.containsKey(pResourceName);
    }
    
    public void add( final String pResourceName, final byte[] pContent ) {
        if (resources == null) {
            resources = new HashMap();
        }
        
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
