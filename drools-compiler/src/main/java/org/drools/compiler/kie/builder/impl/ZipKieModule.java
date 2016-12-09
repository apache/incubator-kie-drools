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

import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;

public class ZipKieModule extends AbstractKieModule implements InternalKieModule {
    private final File file;
    private Map<String, byte[]> zipEntries;
    private List<String> fileNames;

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
        Map<String, List<String>> folders = new HashMap<String, List<String>>();
        zipEntries = new HashMap<String, byte[]>();
        fileNames = new ArrayList<String>();

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile( jarFile );
            Enumeration< ? extends ZipEntry> entries = zipFile.entries();
            while ( entries.hasMoreElements() ) {
                ZipEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".dex")) {
                    continue; //avoid out of memory error, it is useless anyway
                }
                String entryName = entry.getName();
                if (entry.isDirectory()) {
                    if (entryName.endsWith( "/" )) {
                        entryName = entryName.substring( 0, entryName.length()-1 );
                    }
                } else {
                    byte[] bytes = readBytesFromInputStream( zipFile.getInputStream( entry ) );
                    zipEntries.put( entryName, bytes );
                    fileNames.add( entryName );
                }
                int lastSlashPos = entryName.lastIndexOf( '/' );
                String folderName = lastSlashPos < 0 ? "" : entryName.substring( 0, lastSlashPos );
                List<String> folder = folders.get(folderName);
                if (folder == null) {
                    folder = new ArrayList<String>();
                    folders.put( folderName, folder );
                }
                folder.add(lastSlashPos < 0 ? entryName : entryName.substring( lastSlashPos+1 ));
            }
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get all ZipFile entries: " + jarFile, e );
        } finally {
            if ( zipFile != null ) {
                try {
                    zipFile.close();
                } catch ( IOException e ) {
                    throw new RuntimeException( "Unable to get all ZipFile entries: " + jarFile, e );
                }
            }
        }

        for (Map.Entry<String, List<String>> folder : folders.entrySet()) {
            StringBuilder sb = new StringBuilder();
            for (String child : folder.getValue()) {
                sb.append( child ).append( "\n" );
            }
            zipEntries.put( folder.getKey(), sb.toString().getBytes( StandardCharsets.UTF_8 ) );
        }
    }
}
