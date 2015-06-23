/*
 * Copyright 2015 JBoss Inc
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
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.core.common.ResourceProvider;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Collection;

public class MemoryKieModule extends AbstractKieModule
        implements
        ResourceReader {

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
                return mfs.existsFile(name) ? new URL(MemoryURLStreamHandler.MEMORY_URL_PROTOCOL, null, -1, constructName(name), new MemoryURLStreamHandler(mfs.getFile(name))) : null;
            } catch (MalformedURLException e) {
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
            return mfs.existsFile(name) ? mfs.getFile(name).getContents() : null;
        }
    }

    private static class MemoryURLStreamHandler extends URLStreamHandler {

        private static final String MEMORY_URL_PROTOCOL = "mfs";

        private final org.drools.compiler.compiler.io.File file;

        private MemoryURLStreamHandler(org.drools.compiler.compiler.io.File file) {
            this.file = file;
        }

        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            return MEMORY_URL_PROTOCOL.equals(url.getProtocol()) ? new MemoryURLConnection(url, file) : url.openConnection();
        }
    }

    private static class MemoryURLConnection extends URLConnection {

        private final org.drools.compiler.compiler.io.File file;

        public MemoryURLConnection(URL url, org.drools.compiler.compiler.io.File file) {
            super(url);
            this.file = file;
        }

        public InputStream getInputStream() throws IOException {
            return file.getContents();
        }

        @Override
        public void connect() throws IOException {
            throw new UnsupportedOperationException();
        }
    }
}
