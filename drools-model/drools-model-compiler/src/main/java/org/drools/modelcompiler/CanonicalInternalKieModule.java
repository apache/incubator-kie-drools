package org.drools.modelcompiler;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

import org.drools.compiler.kie.builder.impl.AbstractKieModule;
import org.drools.io.InternalResource;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

public class CanonicalInternalKieModule extends AbstractKieModule {

    public CanonicalInternalKieModule( ReleaseId releaseId, KieModuleModel kModuleModel) {
        super(releaseId, kModuleModel);
    }

    @Override
    public Collection<String> getFileNames() {
        return Collections.emptyList();
    }

    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InternalResource getResource( String fileName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAvailable( String pResourceName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] getBytes( String pResourceName ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getFile() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getCreationTimestamp() {
        throw new UnsupportedOperationException();
    }
}
