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
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kie.memorycompiler.resources.ResourceReader;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

public class DiskResourceReader implements ResourceReader {
    private final File root;

    private Map<Path, Integer> filesHashing;

    private Collection<Path> filePaths;

    public DiskResourceReader( final File pRoot ) {
        root = pRoot;        
    }
    
    public boolean isAvailable( Path resourcePath ) {
        return new File(root, resourcePath.toString()).exists();
    }

    public byte[] getBytes( final String pResourceName ) {
        try {
            return readBytesFromInputStream(new FileInputStream(new File(root, pResourceName)));
        } catch(Exception e) {
            return null;
        }
    }
    
    public byte[] getBytes( final Path resourcePath ) {
        try {
            return readBytesFromInputStream(new FileInputStream(new File(root.toPath().resolve(resourcePath).toString())));
        } catch(Exception e) {
            return null;
        }
    }

    public Collection<Path> getFilePaths() {
        if (filePaths == null) {
            filePaths = new ArrayList<>();
            list(root, filePaths);
        }
        return filePaths;
    }

    public void mark() {
        filesHashing = hashFiles();
    }

    public Collection<Path> getModifiedResourcesSinceLastMark() {
        Set<Path> modifiedResources = new HashSet<>();
        Map<Path, Integer> newHashing = hashFiles();
        for (Map.Entry<Path, Integer> entry : newHashing.entrySet()) {
            Integer oldHashing = filesHashing.get(entry.getKey());
            if (oldHashing == null || !oldHashing.equals(entry.getValue())) {
                modifiedResources.add(entry.getKey());
            }
        }
        for (Path oldFile : filesHashing.keySet()) {
            if (!newHashing.containsKey(oldFile)) {
                modifiedResources.add(oldFile);
            }
        }
        return modifiedResources;
    }

    private Map<Path, Integer> hashFiles() {
        Map<Path, Integer> hashing = new HashMap<>();
        for (Path fileName : getFilePaths()) {
            byte[] bytes = getBytes( fileName );
            if ( bytes != null ) {
                hashing.put(fileName, Arrays.hashCode(bytes));
            }
        }
        return hashing;
    }

    private void list( final File pFile, final Collection<Path> pFiles ) {
        if (pFile.isDirectory()) {
            final File[] directoryFiles = pFile.listFiles();
            for (int i = 0; i < directoryFiles.length; i++) {
                list(directoryFiles[i], pFiles);
            }
        } else {
            pFiles.add(root.toPath().relativize(pFile.toPath()));
        }
    }   
    
}
