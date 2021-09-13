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

import org.kie.memorycompiler.resources.KiePath;
import org.kie.memorycompiler.resources.ResourceReader;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

public class DiskResourceReader implements ResourceReader {
    private final File root;
    private final KiePath rootPath;

    private Map<KiePath, Integer> filesHashing;

    public DiskResourceReader( final File root ) {
        this.root = root;
        this.rootPath = KiePath.of(root.getAbsolutePath());
    }
    
    public boolean isAvailable( KiePath resourcePath ) {
        return new File(root, resourcePath.asString()).exists();
    }

    public byte[] getBytes( KiePath resourcePath ) {
        try {
            return readBytesFromInputStream(new FileInputStream(new File(root, resourcePath.asString())));
        } catch(Exception e) {
            return null;
        }
    }
    
    public Collection<KiePath> getFilePaths() {
        List<KiePath> list = new ArrayList<>();
        list(root, list);        
        return list;
    }

    public void mark() {
        filesHashing = hashFiles();
    }

    public Collection<String> getModifiedResourcesSinceLastMark() {
        Set<String> modifiedResources = new HashSet<String>();
        Map<KiePath, Integer> newHashing = hashFiles();
        for (Map.Entry<KiePath, Integer> entry : newHashing.entrySet()) {
            Integer oldHashing = filesHashing.get(entry.getKey());
            if (oldHashing == null || !oldHashing.equals(entry.getValue())) {
                modifiedResources.add(entry.getKey().asString());
            }
        }
        for (KiePath oldFile : filesHashing.keySet()) {
            if (!newHashing.containsKey(oldFile)) {
                modifiedResources.add(oldFile.asString());
            }
        }
        return modifiedResources;
    }

    private Map<KiePath, Integer> hashFiles() {
        Map<KiePath, Integer> hashing = new HashMap<>();
        for (KiePath filePath : getFilePaths()) {
            byte[] bytes = getBytes( filePath );
            if ( bytes != null ) {
                hashing.put(filePath, Arrays.hashCode(bytes));
            }
        }
        return hashing;
    }

    private void list( final File pFile, final List<KiePath> pFiles ) {
        if (pFile.isDirectory()) {
            final File[] directoryFiles = pFile.listFiles();
            for (int i = 0; i < directoryFiles.length; i++) {
                list(directoryFiles[i], pFiles);
            }
        } else {
            pFiles.add( KiePath.of( pFile.getAbsolutePath().substring(rootPath.asString().length()+1) ) );
        }
    }   
    
}
