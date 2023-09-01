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
package org.drools.compiler.kie.builder.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.util.PortablePath;
import org.kie.memorycompiler.resources.ResourceReader;

import static org.drools.util.IoUtils.readBytesFromInputStream;

public class DiskResourceReader implements ResourceReader {
    private final File root;
    private final PortablePath rootPath;

    private Map<PortablePath, Integer> filesHashing;

    public DiskResourceReader( final File root ) {
        this.root = root;
        this.rootPath = PortablePath.of(root.getAbsolutePath());
    }
    
    public boolean isAvailable( PortablePath resourcePath ) {
        return new File(root, resourcePath.asString()).exists();
    }

    public byte[] getBytes( PortablePath resourcePath ) {
        try {
            return readBytesFromInputStream(new FileInputStream(new File(root, resourcePath.asString())));
        } catch(Exception e) {
            return null;
        }
    }
    
    public Collection<PortablePath> getFilePaths() {
        List<PortablePath> list = new ArrayList<>();
        list(root, list);        
        return list;
    }

    public void mark() {
        filesHashing = hashFiles();
    }

    public Collection<String> getModifiedResourcesSinceLastMark() {
        Set<String> modifiedResources = new HashSet<>();
        Map<PortablePath, Integer> newHashing = hashFiles();
        for (Map.Entry<PortablePath, Integer> entry : newHashing.entrySet()) {
            Integer oldHashing = filesHashing.get(entry.getKey());
            if (oldHashing == null || !oldHashing.equals(entry.getValue())) {
                modifiedResources.add(entry.getKey().asString());
            }
        }
        for (PortablePath oldFile : filesHashing.keySet()) {
            if (!newHashing.containsKey(oldFile)) {
                modifiedResources.add(oldFile.asString());
            }
        }
        return modifiedResources;
    }

    private Map<PortablePath, Integer> hashFiles() {
        Map<PortablePath, Integer> hashing = new HashMap<>();
        for (PortablePath filePath : getFilePaths()) {
            byte[] bytes = getBytes( filePath );
            if ( bytes != null ) {
                hashing.put(filePath, Arrays.hashCode(bytes));
            }
        }
        return hashing;
    }

    private void list( final File pFile, final List<PortablePath> pFiles ) {
        if (pFile.isDirectory()) {
            final File[] directoryFiles = pFile.listFiles();
            for (int i = 0; i < directoryFiles.length; i++) {
                list(directoryFiles[i], pFiles);
            }
        } else {
            pFiles.add( PortablePath.of( pFile.getAbsolutePath().substring(rootPath.asString().length()+1) ) );
        }
    }   
    
}
