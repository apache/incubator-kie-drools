package org.drools.compiler.kie.builder.impl;

import static org.drools.core.util.IoUtils.readBytesFromZipEntry;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.zip.ZipEntry;

import org.drools.core.util.IoUtils;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

public class ZipKieModule extends AbstractKieModule implements InternalKieModule {
    private final File             file;    
    private Map<String, ZipEntry> zipEntries;

    public ZipKieModule(ReleaseId releaseId,
                        KieModuleModel kieProject,
                        File file) {
        super(releaseId, kieProject );
        this.file = file;
        this.zipEntries = IoUtils.buildZipFileMapEntries( file );
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
        try {
            return readBytesFromZipEntry(file, zipEntries.get(name));
        } catch (IOException e) {
            throw new RuntimeException( "Unable to get ZipFile bytes for :  " + name + " : " + file, e );
        }
    }

    @Override
    public Collection<String> getFileNames() {
        return this.zipEntries.keySet();
    }

    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return "ZipKieModule[ ReleaseId=" + getReleaseId() + "file=" + file + "]";
    }
}
