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

import org.drools.compiler.commons.jci.readers.ResourceReader;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.Resource;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.common.ResourceProvider;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class MemoryKieModule extends AbstractKieModule
        implements
        ResourceReader {

    private static final Logger logger = LoggerFactory.getLogger(MemoryKieModule.class);

    private static final String MEMORY_URL_PROTOCOL = "mfs";

    private final MemoryFileSystem mfs;
    private final long creationTimestamp = System.currentTimeMillis();

    public MemoryKieModule(ReleaseId releaseId) {
        this( releaseId, new KieModuleModelImpl(), new MemoryFileSystem() );
    }

    public MemoryKieModule(ReleaseId releaseId,
                           KieModuleModel kModuleModel,
                           MemoryFileSystem mfs) {
        super( releaseId, kModuleModel );
        this.mfs = mfs;
    }

    @Override
    public boolean isAvailable(String path) {
        return mfs.existsFile( path );
    }

    @Override
    public byte[] getBytes(String path) {
        return mfs.getBytes( path );
    }

    @Override
    public Collection<String> getFileNames() {
        return mfs.getFileNames();
    }

    public MemoryFileSystem getMemoryFileSystem() {
        return this.mfs;
    }

    public void mark() {
        mfs.mark();
    }

    public Collection<String> getModifiedResourcesSinceLastMark() {
        return mfs.getModifiedResourcesSinceLastMark();
    }

    @Override
    public File getFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getBytes() {
        return mfs.writeAsBytes();
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public String toString() {
        return "MemoryKieModule[releaseId=" + getReleaseId() + "]";
    }

    MemoryKieModule cloneForIncrementalCompilation(ReleaseId releaseId, KieModuleModel kModuleModel, MemoryFileSystem newFs) {
        MemoryKieModule clone = new MemoryKieModule(releaseId, kModuleModel, newFs);
        for (InternalKieModule dep : getKieDependencies().values()) {
            clone.addKieDependency(dep);
        }
        for (KieBaseModel kBaseModel : getKieModuleModel().getKieBaseModels().values()) {
            clone.cacheKnowledgeBuilderForKieBase(kBaseModel.getName(), getKnowledgeBuilderForKieBase( kBaseModel.getName() ));
        }

        clone.setPomModel( getPomModel() );
        for ( InternalKieModule dependency : getKieDependencies().values() ) {
            clone.addKieDependency( dependency );
        }
        clone.setUnresolvedDependencies( getUnresolvedDependencies() );
        
        return clone;
    }

    @Override
    public ResourceProvider createResourceProvider() {
        return new MemoryKieModuleResourceProvider(mfs);
    }

    private static class MemoryKieModuleResourceProvider implements ResourceProvider {

        private final MemoryFileSystem mfs;

        private MemoryKieModuleResourceProvider(MemoryFileSystem mfs) {
            this.mfs = mfs;
        }

        @Override
        public URL getResource(String name) {
            try {
                if (mfs.existsFile(name)) {
                    return new URL(MEMORY_URL_PROTOCOL, null, -1, constructName(name), new MemoryFileURLStreamHandler(mfs.getFile(name)));
                } else if (mfs.existsFolder(name)) {
                    return new URL(MEMORY_URL_PROTOCOL, null, -1, constructName(name), new MemoryFolderURLStreamHandler(mfs.getFolder(name)));
                } else {
                    return null;
                }
            } catch (MalformedURLException e) {
                logger.debug("Can't create URL for resource " + name, e);
                return null;
            }
        }

        private String constructName(String name) {
            if (name.startsWith("/")) {
                return name;
            }

            return "/" + name;
        }

        @Override
        public InputStream getResourceAsStream(String name) throws IOException {
            if (mfs.existsFile(name)) {
                return mfs.getFile(name).getContents();
            } else if (mfs.existsFolder(name)) {
                return new FolderMembersInputStream(mfs.getFolder(name));
            } else {
                return null;
            }
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MemoryKieModuleResourceProvider)) return false;

            MemoryKieModuleResourceProvider that =
                    (MemoryKieModuleResourceProvider) o;

            return mfs != null ? mfs.equals(that.mfs) : that.mfs == null;
        }

        @Override public int hashCode() {
            return mfs != null ? mfs.hashCode() : 0;
        }
    }

    private static class MemoryFileURLStreamHandler extends URLStreamHandler {

        private final org.drools.compiler.compiler.io.File file;

        private MemoryFileURLStreamHandler(org.drools.compiler.compiler.io.File file) {
            this.file = file;
        }

        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            return MEMORY_URL_PROTOCOL.equals(url.getProtocol()) ? new MemoryFileURLConnection(url, file) : url.openConnection();
        }
    }

    private static class MemoryFolderURLStreamHandler extends URLStreamHandler {
        private final Folder folder;

        private MemoryFolderURLStreamHandler(Folder folder) {
            this.folder = folder;
        }


        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            return MEMORY_URL_PROTOCOL.equals(url.getProtocol()) ? new MemoryFolderURLConnection(url, folder) : url.openConnection();
        }
    }

    private static class MemoryFileURLConnection extends URLConnection {

        private final org.drools.compiler.compiler.io.File file;

        public MemoryFileURLConnection(URL url, org.drools.compiler.compiler.io.File file) {
            super(url);
            this.file = file;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return file.getContents();
        }

        @Override
        public void connect() throws IOException {
            throw new UnsupportedOperationException();
        }
    }

    private static class MemoryFolderURLConnection extends URLConnection {

        private final Folder folder;

        public MemoryFolderURLConnection(URL url, Folder folder) {
            super(url);
            this.folder = folder;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new FolderMembersInputStream(folder);
        }

        @Override
        public void connect() throws IOException {
            throw new UnsupportedOperationException();
        }
    }

    private static class FolderMembersInputStream extends InputStream {
        private final InputStream dataIs;

        public FolderMembersInputStream(Folder folder) {
            this.dataIs = folderMembersToInputStream(folder);
        }

        @Override
        public int read() throws IOException {
            return dataIs.read();
        }

        private InputStream folderMembersToInputStream(Folder folder) {
            StringBuilder sb = new StringBuilder();
            Collection<? extends Resource> members = folder.getMembers();
            if (members != null) {
                for (Resource resource : members) {
                    // take just the name of the member, no the whole path
                    sb.append(resource.getPath().toRelativePortableString(folder.getPath()));
                    // append "\n" to be in sync with the JDK's ClassLoader (returns "\n" even on Windows)
                    sb.append("\n");
                }
            }
            return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
}
