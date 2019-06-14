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

package org.drools.compiler.kie.builder.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

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
        Map<String, List<String>> folders = new HashMap<>();
        String urlPath = jarFile.getAbsolutePath();
        if (jarFile.exists()) {

            try (final ZipFile zipFile = new ZipFile( jarFile )) {
                Enumeration< ? extends ZipEntry> entries = zipFile.entries();
                while ( entries.hasMoreElements() ) {
                    ZipEntry entry = entries.nextElement();

                    processEntry(entry, folders, true, () -> {
                        try {
                            return zipFile.getInputStream( entry );
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        } else if (urlPath.indexOf( '!' ) > 0) {
            urlPath = urlPath.substring( urlPath.lastIndexOf( '!' ) + 1 ).replace("\\", "/");
            ArrayList<ZipEntry> entries = new ArrayList<>();
            // read jar file from uber-jar
            InputStream in = this.getClass().getResourceAsStream(urlPath);
            ZipInputStream zipIn = new ZipInputStream(in);
            try {
                ZipEntry entry = zipIn.getNextEntry();
                while (entry != null) {
                    // process each entry
                    processEntry(entry, folders, false, () -> zipIn);            
                    zipIn.closeEntry();
                    
                    entries.add(entry);
                    // get next entry
                    entry = zipIn.getNextEntry();
                }
            } finally {
                zipIn.close();
            }            
        } else {
            throw new FileNotFoundException(urlPath);
        }
        
        return folders;
    }
    
    protected void processEntry(ZipEntry entry, Map<String, List<String>> folders, boolean closeEntryStream, Supplier<InputStream> stream) throws IOException {
        if (entry.getName().endsWith(".dex")) {
            return; //avoid out of memory error, it is useless anyway
        }
        String entryName = entry.getName();
        if (entry.isDirectory()) {
            if (entryName.endsWith( "/" )) {
                entryName = entryName.substring( 0, entryName.length()-1 );
            }
        } else {
            byte[] bytes = readBytesFromInputStream( stream.get(), closeEntryStream );
            zipEntries.put( entryName, bytes );
            fileNames.add( entryName );
        }
        int lastSlashPos = entryName.lastIndexOf( '/' );
        String folderName = lastSlashPos < 0 ? "" : entryName.substring( 0, lastSlashPos );
        List<String> folder = folders.computeIfAbsent(folderName, k -> new ArrayList<>());
        folder.add(lastSlashPos < 0 ? entryName : entryName.substring( lastSlashPos+1 ));
    }
    
}
