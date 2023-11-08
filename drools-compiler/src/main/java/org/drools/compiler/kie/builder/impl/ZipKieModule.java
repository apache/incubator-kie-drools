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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.drools.io.InternalResource;
import org.drools.util.PortablePath;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.internal.io.ResourceFactory;

import static org.drools.util.IoUtils.readBytesFromInputStream;

public class ZipKieModule extends AbstractKieModule implements InternalKieModule, Serializable {
    private File file;
    private Map<String, byte[]> zipEntries;
    private List<String> fileNames;
    

    public ZipKieModule() { }

    public ZipKieModule(ReleaseId releaseId,
                        KieModuleModel kieProject,
                        File file) {
        super(releaseId, kieProject );
        this.file = file;
        indexZipFile( file );
    }

    @Override
    public InternalResource getResource( String fileName) {
        byte[] bytes = getBytes(fileName);
        if (bytes != null) {
            return (InternalResource) ResourceFactory.newByteArrayResource(bytes).setSourcePath(fileName);
        }
        return null;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    @Override
    public boolean isAvailable(String name ) {
        return this.zipEntries.containsKey( name );
    }

    @Override
    public byte[] getBytes(String name) {
        return zipEntries.get(name);
    }

    @Override
    public Collection<String> getFileNames() {
        return fileNames;
    }

    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }

    public long getCreationTimestamp() {
        return file.lastModified();
    }

    public String toString() {
        return "ZipKieModule[releaseId=" + getReleaseId() + ",file=" + file + "]";
    }

    private void indexZipFile(java.io.File jarFile) {
        Map<String, List<String>> folders;
        zipEntries = new HashMap<>();
        fileNames = new ArrayList<>();

        
        try {
            folders = processZipEntries(jarFile);
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get all ZipFile entries: " + jarFile, e );
        }

        for (Map.Entry<String, List<String>> folder : folders.entrySet()) {
            StringBuilder sb = new StringBuilder();
            for (String child : folder.getValue()) {
                sb.append( child ).append( "\n" );
            }
            zipEntries.put( folder.getKey(), sb.toString().getBytes( StandardCharsets.UTF_8 ) );
        }
    }
    
    protected Map<String, List<String>> processZipEntries(File jarFile) throws IOException {
        if (jarFile.exists()) {
            return processZipFile( jarFile );
        }

        String urlPath = jarFile.getAbsolutePath();
        int urlSeparatorPos = urlPath.indexOf( '!' );
        if (urlSeparatorPos > 0) {
            return processZipUrl( urlPath, urlSeparatorPos );
        }
        
        throw new FileNotFoundException(urlPath);
    }

    private Map<String, List<String>> processZipFile( File jarFile ) throws IOException {
        try (FileInputStream fis = new FileInputStream(jarFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ZipInputStream zipIn = new ZipInputStream(bis)) {
            return processZipEntries( zipIn, zipIn.getNextEntry(), null );
        }
    }

    private Map<String, List<String>> processZipUrl( String urlPath, int urlSeparatorPos ) throws IOException {
        String folderInUrl = PortablePath.of( urlPath.substring( urlSeparatorPos + 1 ) ).asString();
        // read jar file from uber-jar
        InputStream in = this.getClass().getResourceAsStream(folderInUrl);
        if (in == null) {
            return processFolderInZipFile( urlPath, urlSeparatorPos );
        }

        try (ZipInputStream zipIn = new ZipInputStream(in)) {
            ZipEntry entry = zipIn.getNextEntry();
            return entry == null ? processFolderInZipFile( urlPath, urlSeparatorPos ) : processZipEntries( zipIn, entry, null );
        }
    }

    private Map<String, List<String>> processFolderInZipFile( String urlPath, int urlSeparatorPos ) throws IOException {
        String jarFile = urlPath.substring( 0, urlSeparatorPos );
        String folderInUrl = PortablePath.of( urlPath.substring( urlSeparatorPos + 1 ) ).asString();
        String rootFolder = folderInUrl.startsWith( "/" ) ? folderInUrl.substring( 1 ) : folderInUrl;

        try (FileInputStream fis = new FileInputStream(jarFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ZipInputStream zipIn = new ZipInputStream(bis)) {
            return processZipEntries( zipIn, zipIn.getNextEntry(), rootFolder );
        }
    }

    private Map<String, List<String>> processZipEntries( ZipInputStream zipIn, ZipEntry entry, String rootFolder ) throws IOException {
        Map<String, List<String>> folders = new HashMap<>();
        while (entry != null) {
            // process each entry
            processEntry( entry, folders, zipIn, rootFolder );
            zipIn.closeEntry();

            // get next entry
            entry = zipIn.getNextEntry();
        }
        return folders;
    }

    private void processEntry( ZipEntry entry, Map<String, List<String>> folders, InputStream stream, String rootFolder ) throws IOException {
        String entryName = entry.getName();
        if (entryName.endsWith(".dex")) {
            return; //avoid out of memory error, it is useless anyway
        }
        if (rootFolder != null) {
            if (entryName.startsWith( rootFolder )) {
                entryName = entryName.substring( rootFolder.length()+1 );
            } else {
                return;
            }
        }

        if (entry.isDirectory()) {
            if (entryName.endsWith( "/" )) {
                entryName = entryName.substring( 0, entryName.length()-1 );
            }
        } else {
            byte[] bytes = readBytesFromInputStream( stream, false );
            zipEntries.put( entryName, bytes );
            fileNames.add( entryName );
        }
        int lastSlashPos = entryName.lastIndexOf( '/' );
        String folderName = lastSlashPos < 0 ? "" : entryName.substring( 0, lastSlashPos );
        List<String> folder = folders.computeIfAbsent(folderName, k -> new ArrayList<>());
        folder.add(lastSlashPos < 0 ? entryName : entryName.substring( lastSlashPos+1 ));
    }
}
