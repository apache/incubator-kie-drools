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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.io.FileSystemItem;
import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.drools.io.InternalResource;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.util.PortablePath;
import org.drools.wiring.api.ResourceProvider;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.internal.utils.KieService;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.conf.AlphaNetworkCompilerOption;
import org.kie.memorycompiler.resources.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryKieModule extends AbstractKieModule
        implements
        ResourceReader, Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MemoryKieModule.class);

    private static final String MEMORY_URL_PROTOCOL = "mfs";

    private MemoryFileSystem mfs;
    private final long creationTimestamp = System.currentTimeMillis();

    public MemoryKieModule() { }

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
        return isAvailable( PortablePath.of(path) );
    }

    @Override
    public boolean isAvailable(PortablePath path) {
        return mfs.existsFile( path );
    }

    @Override
    public byte[] getBytes(String path) {
        return mfs.getBytes( path );
    }

    @Override
    public byte[] getBytes(PortablePath path) {
        return mfs.getBytes( path );
    }

    @Override
    public InternalResource getResource( String fileName) {
        return getResource( PortablePath.of(fileName) );
    }

    public InternalResource getResource( PortablePath path) {
        return mfs.getResource( path );
    }

    @Override
    public Collection<PortablePath> getFilePaths() {
        return mfs.getFilePaths();
    }

    @Override
    public Collection<String> getFileNames() {
        return getFilePaths().stream().map(PortablePath::asString).collect(Collectors.toList());
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

    @Override
    public void afterKieBaseCreationUpdate(String kBaseName, InternalKnowledgeBase kBase) {
        KnowledgeBuilder knowledgeBuilderForKieBase = getKnowledgeBuilderForKieBase(kBaseName);

        if(knowledgeBuilderForKieBase instanceof KnowledgeBuilderImpl) {
            KnowledgeBuilderImpl knowledgeBuilderForImpl = (KnowledgeBuilderImpl)knowledgeBuilderForKieBase;
            KnowledgeBuilderConfigurationImpl builderConfiguration = knowledgeBuilderForImpl.getBuilderConfiguration();

            KieContainerImpl.CompositeRunnable compositeUpdater = new KieContainerImpl.CompositeRunnable();

            KieBaseUpdaterOptions kieBaseUpdaterOptions = new KieBaseUpdaterOptions(new KieBaseUpdaterOptions.OptionEntry(
                    AlphaNetworkCompilerOption.class, builderConfiguration.getOption(AlphaNetworkCompilerOption.KEY)));

            KieBaseUpdaters updaters = KieService.load(KieBaseUpdaters.class);
            updaters.getChildren()
                    .stream()
                    .map(kbu -> kbu.create(new KieBaseUpdatersContext(kieBaseUpdaterOptions,
                                                                      kBase.getRete(),
                                                                      kBase.getRootClassLoader()
                    )))
                    .forEach(compositeUpdater::add);

            kBase.enqueueModification(compositeUpdater);
        }
    }

    public String toString() {
        return "MemoryKieModule[releaseId=" + getReleaseId() + "]";
    }

    @Override
    public MemoryKieModule cloneForIncrementalCompilation(ReleaseId releaseId, KieModuleModel kModuleModel, MemoryFileSystem newFs) {
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
            PortablePath portablePath = PortablePath.of(name);
            try {
                if (mfs.existsFile(portablePath)) {
                    return new URL(MEMORY_URL_PROTOCOL, null, -1, constructName(name), new MemoryFileURLStreamHandler(mfs.getFile(portablePath)));
                } else if (mfs.existsFolder(portablePath)) {
                    return new URL(MEMORY_URL_PROTOCOL, null, -1, constructName(name), new MemoryFolderURLStreamHandler(mfs.getFolder(portablePath)));
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
            PortablePath portablePath = PortablePath.of(name);
            if (mfs.existsFile(portablePath)) {
                return mfs.getFile(portablePath).getContents();
            } else if (mfs.existsFolder(portablePath)) {
                return new FolderMembersInputStream(mfs.getFolder(portablePath));
            } else {
                return null;
            }
        }

        @Override public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof MemoryKieModuleResourceProvider)) {
                return false;
            }

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
            Collection<? extends FileSystemItem> members = folder.getMembers();
            if (members != null) {
                for (FileSystemItem resource : members) {
                    // take just the name of the member, no the whole path
                    sb.append(resource.getPath().asString().substring(folder.getPath().asString().length()+1));
                    // append "\n" to be in sync with the JDK's ClassLoader (returns "\n" even on Windows)
                    sb.append("\n");
                }
            }
            return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
        }
    }
}
