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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.drools.core.impl.InternalKieContainer;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseIdComparator.ComparableVersion;
import org.kie.api.runtime.KieContainer;

public class KieFileSystemScannerImpl extends AbstractKieScanner<InternalKieModule> implements KieScanner {

    private final Path repositoryFolder;

    public KieFileSystemScannerImpl(final KieContainer kieContainer, final Path repositoryFolder ) {
        this.kieContainer = ( InternalKieContainer ) kieContainer;
        this.repositoryFolder = repositoryFolder;
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
        final File[] files = repositoryFolder.toFile().listFiles((dir, name) -> name.startsWith(kieContainer.getReleaseId().getArtifactId() + "-") && name.endsWith(".jar" ));
        final List<File> jarFiles;
        if (files != null) {
            jarFiles = Arrays.asList(files);
        } else {
            return null;
        }

        if (jarFiles.isEmpty()) {
            return null;
        }
        if (jarFiles.size() > 1) {
            jarFiles.sort(new VersionComparator(kieContainer.getReleaseId().getArtifactId().length() + 1).reversed());
        }
        return jarFiles.get(0);
    }

    private static class VersionComparator implements Comparator<File> {
        private final int headLength;

        private VersionComparator(final int headLength ) {
            this.headLength = headLength;
        }

        @Override
        public int compare(final File s1, final File s2 ) {
            final String s1Name = s1.getName();
            final String s2Name = s2.getName();
            return new ComparableVersion(s1Name.substring( headLength, s1Name.length()-4 )).compareTo( new ComparableVersion( s2Name.substring( headLength, s1Name.length()-4 ) ) );
        }
    }
}
