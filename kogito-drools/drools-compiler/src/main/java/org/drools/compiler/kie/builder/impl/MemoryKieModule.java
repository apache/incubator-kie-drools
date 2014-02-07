package org.drools.compiler.kie.builder.impl;

import java.io.File;
import java.util.Collection;

import org.drools.compiler.commons.jci.readers.ResourceReader;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kproject.models.KieModuleModelImpl;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;

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
        return "MemoryKieModule[ ReleaseId=" + getReleaseId() + "]";
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
}
