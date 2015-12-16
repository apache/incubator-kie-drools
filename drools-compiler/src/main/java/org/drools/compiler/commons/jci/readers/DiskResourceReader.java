/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

public class DiskResourceReader implements ResourceReader {
    private final File root;

    private Map<String, Integer> filesHashing;

    public DiskResourceReader( final File pRoot ) {
        root = pRoot;        
    }
    
    public boolean isAvailable( final String pResourceName ) {
        return new File(root, pResourceName).exists();
    }

    public byte[] getBytes( final String pResourceName ) {
        try {
            return readBytesFromInputStream(new FileInputStream(new File(root, pResourceName)));
        } catch(Exception e) {
            return null;
        }
    }
    
    public Collection<String> getFileNames() {
        List<String> list = new ArrayList();
        list(root, list);        
        return list;
    }

    public void mark() {
        filesHashing = hashFiles();
    }

    public Collection<String> getModifiedResourcesSinceLastMark() {
        Set<String> modifiedResources = new HashSet<String>();
        Map<String, Integer> newHashing = hashFiles();
        for (Map.Entry<String, Integer> entry : newHashing.entrySet()) {
            Integer oldHashing = filesHashing.get(entry.getKey());
            if (oldHashing == null || !oldHashing.equals(entry.getValue())) {
                modifiedResources.add(entry.getKey());
            }
        }
        for (String oldFile : filesHashing.keySet()) {
            if (!newHashing.containsKey(oldFile)) {
                modifiedResources.add(oldFile);
            }
        }
        return modifiedResources;
    }

    private Map<String, Integer> hashFiles() {
        Map<String, Integer> hashing = new HashMap<String, Integer>();
        for (String fileName : getFileNames()) {
            byte[] bytes = getBytes( fileName );
            if ( bytes != null ) {
                hashing.put(fileName, Arrays.hashCode(bytes));
            }
        }
        return hashing;
    }

    /**
     * @deprecated
     */
    public String[] list() {
        final List<String> files = new ArrayList<String>();
        list(root, files);
        return files.toArray(new String[files.size()]);
    }

    /**
     * @deprecated
     */
    private void list( final File pFile, final List pFiles ) {
        if (pFile.isDirectory()) {
            final File[] directoryFiles = pFile.listFiles();
            for (int i = 0; i < directoryFiles.length; i++) {
                list(directoryFiles[i], pFiles);
            }
        } else {
            pFiles.add(pFile.getAbsolutePath().substring(root.getAbsolutePath().length()+1));
        }
    }   
    
}
