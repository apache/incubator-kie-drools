/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.core.impl.InternalKieContainer;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseIdComparator.ComparableVersion;
import org.kie.api.runtime.KieContainer;

public class KieFileSystemScannerImpl extends AbstractKieScanner<InternalKieModule> implements KieScanner {

    private final Path repositoryFolder;

    private WatchService watchService;

    public KieFileSystemScannerImpl( KieContainer kieContainer, Path repositoryFolder ) {
        this.kieContainer = ( InternalKieContainer ) kieContainer;
        this.repositoryFolder = repositoryFolder;

        try {
            watchService = FileSystems.getDefault().newWatchService();
            repositoryFolder.register( watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY );
        } catch (IOException e) {
            throw new RuntimeException( e );
        }
    }

    @Override
    protected InternalKieModule internalScan() {
        String newKJar = findNewFileName( watchService.poll() );
        return newKJar == null ? null : InternalKieModule.createKieModule(kieContainer.getReleaseId(), new File(repositoryFolder.toString(), newKJar));
    }

    @Override
    protected void internalUpdate( InternalKieModule kmodule ) {
        (( KieContainerImpl ) kieContainer).updateToKieModule( kmodule );
    }

    private String findNewFileName( WatchKey key ) {
        if (key == null) {
            return null;
        }
        List<String> modifiedJars = key.pollEvents().stream()
                .map( e -> e.context().toString() ).distinct()
                .filter( s -> s.startsWith( kieContainer.getReleaseId().getArtifactId() + "-") && s.endsWith( ".jar" ))
                .collect(Collectors.toList());

        if (modifiedJars.isEmpty()) {
            return null;
        }
        if (modifiedJars.size() > 1) {
            Collections.sort(modifiedJars, new VersionComparator( kieContainer.getReleaseId().getArtifactId().length()+1 ).reversed());
        }
        return modifiedJars.get(0);
    }

    private static class VersionComparator implements Comparator<String> {
        private final int headLength;

        private VersionComparator( int headLength ) {
            this.headLength = headLength;
        }

        @Override
        public int compare( String s1, String s2 ) {
            return new ComparableVersion(s1.substring( headLength, s1.length()-4 )).compareTo( new ComparableVersion( s2.substring( headLength, s1.length()-4 ) ) );
        }
    }
}
