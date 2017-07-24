/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.config.domain;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import org.drools.compiler.compiler.io.File;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.Resource;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.core.impl.InternalKieContainer;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.reflections.vfs.Vfs;

public class ReflectionsKieVfsUrlType implements Vfs.UrlType {

    public static void register(KieContainer kieContainer) {
        // TODO Support having multiple KieContainers on the classpath (maybe through ClassLoader to KieContainer mapping?)
        Vfs.addDefaultURLTypes(new ReflectionsKieVfsUrlType(kieContainer));
    }

    private final KieContainer kieContainer;

    public ReflectionsKieVfsUrlType(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }

    @Override
    public boolean matches(URL url) {
        return url.getProtocol().equalsIgnoreCase("mfs");
    }

    @Override
    public Vfs.Dir createDir(URL url) {
        KieModule kieModule = ((InternalKieContainer) kieContainer).getMainKieModule();
        if (!(kieModule instanceof MemoryKieModule)) {
            throw new IllegalStateException("The classpath url (" + url
                    + ") has an mfs protocol but the kieModule (" + kieModule
                    + ") is not an instance of " + MemoryKieModule.class.getSimpleName() + ".");
        }
        MemoryFileSystem memoryFileSystem = ((MemoryKieModule) kieModule).getMemoryFileSystem();
        Folder folder = memoryFileSystem.getFolder(url.toExternalForm().substring("mfs:/".length()));
        return new ReflectionsKieVfsDir(folder);
    }

    public static class ReflectionsKieVfsDir implements Vfs.Dir {

        private final Folder kieFolder;

        public ReflectionsKieVfsDir(Folder kieFolder) {
            this.kieFolder = kieFolder;
        }

        @Override
        public String getPath() {
            return kieFolder.getPath().toPortableString();
        }

        @Override
        public Iterable<Vfs.File> getFiles() {
            List<Vfs.File> vfsFileList = new ArrayList<>();
            Deque<Resource> resourceDeque = new ArrayDeque<>();
            Collection<? extends Resource> mainMembers = kieFolder.getMembers();
            if (mainMembers != null) {
                resourceDeque.addAll(mainMembers);
            }
            while (!resourceDeque.isEmpty()) {
                Resource resource = resourceDeque.pop();
                if (resource instanceof File) {
                    File file = (File) resource;
                    if (file.getName().endsWith(".class")) {
                        vfsFileList.add(new ReflectionsKieVfsFile(file));
                    }
                } else if (resource instanceof Folder) {
                    Collection<? extends Resource> members = ((Folder) resource).getMembers();
                    if (members != null) {
                        resourceDeque.addAll(members);
                    }
                } else {
                    throw new IllegalStateException("Unsupported resource class (" + resource.getClass()
                            + ") for resource (" + resource + ").");
                }
            }
            return vfsFileList;
        }

        @Override
        public void close() {
            // Do nothing
        }

    }

    public static class ReflectionsKieVfsFile implements Vfs.File {

        private final File kieFile;

        public ReflectionsKieVfsFile(File kieFile) {
            this.kieFile = kieFile;
        }

        @Override
        public String getName() {
            return kieFile.getName();
        }

        @Override
        public String getRelativePath() {
            return kieFile.getPath().toPortableString();
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return kieFile.getContents();
        }

    }

}
