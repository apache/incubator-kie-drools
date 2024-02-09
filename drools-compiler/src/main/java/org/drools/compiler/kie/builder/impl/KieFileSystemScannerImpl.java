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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.drools.core.impl.InternalKieContainer;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseIdComparator.ComparableVersion;
import org.kie.api.runtime.KieContainer;

public class KieFileSystemScannerImpl extends AbstractKieScanner<InternalKieModule> implements KieScanner {

    private final File repositoryFolder;
    private final String kjarFileHead;
    private final VersionComparator versionComparator;

    public KieFileSystemScannerImpl(final KieContainer kieContainer, final String repositoryFolder ) {
        this.kieContainer = ( InternalKieContainer ) kieContainer;
        this.kjarFileHead = kieContainer.getReleaseId().getArtifactId() + "-";
        this.versionComparator = new VersionComparator( kjarFileHead.length() );
        this.repositoryFolder = new File( repositoryFolder );
    }

    @Override
    protected InternalKieModule internalScan() {
        final File newKJar = findNewFile();
        return newKJar == null ? null : InternalKieModule.createKieModule(kieContainer.getReleaseId(), newKJar);
    }

    @Override
    protected void internalUpdate(final InternalKieModule kmodule ) {
        (( KieContainerImpl ) kieContainer).updateToKieModule( kmodule );
    }

    private File findNewFile() {
        File[] files = repositoryFolder.listFiles((dir, name) -> name.startsWith(kjarFileHead) && name.endsWith(".jar" ));
        if (files == null || files.length == 0) {
            return null;
        }

        File candidateNew = getCandidateNew( files );
        int versionComparison = compareVersion(getVersionFromFile(candidateNew, kjarFileHead.length()), kieContainer.getReleaseId().getVersion());
        return versionComparison > 0 || ( versionComparison == 0 && kieContainer.getReleaseId().isSnapshot() ) ? candidateNew : null;
    }

    private File getCandidateNew( File[] files ) {
        if (files.length == 1) {
            return files[0];
        }
        final List<File> jarFiles = Arrays.asList(files);
        jarFiles.sort(versionComparator.reversed());
        return jarFiles.get(0);
    }

    private static class VersionComparator implements Comparator<File> {
        private final int headLength;

        private VersionComparator(final int headLength) {
            this.headLength = headLength;
        }

        @Override
        public int compare(final File f1, final File f2 ) {
            return compareVersion( getVersionFromFile(f1, headLength), getVersionFromFile(f2, headLength) );
        }
    }

    private static int compareVersion(String v1, String v2) {
        return new ComparableVersion( v1 ).compareTo( new ComparableVersion( v2 ) );
    }

    private static String getVersionFromFile(File f, int headLength) {
        String name = f.getName();
        return name.substring( headLength, name.length()-4 );
    }
}
