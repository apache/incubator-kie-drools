package org.drools.compiler.kie.builder.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;

import org.drools.io.InternalResource;
import org.drools.util.IoUtils;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.internal.io.ResourceFactory;

import static org.drools.util.IoUtils.readBytesFromInputStream;

public class FileKieModule extends AbstractKieModule implements InternalKieModule, Serializable {

    private File file;

    public FileKieModule() { }

    public FileKieModule(ReleaseId releaseId,
                      KieModuleModel kieProject,
                      File file) {
        super(releaseId, kieProject );
        this.file = file;
    }

    @Override
    public File getFile() {
        return this.file;
    }

    public long getCreationTimestamp() {
        return file.lastModified();
    }

    @Override
    public boolean isAvailable(String pResourceName) {
        return new File( file, pResourceName).exists();
    }


    @Override
    public byte[] getBytes(String pResourceName ) {
        try {
            File resource = new File( file, pResourceName);
            return resource.exists() && !resource.isDirectory() ? IoUtils.readBytesFromInputStream( new FileInputStream( resource ) ) : null;
        } catch ( IOException e ) {
            throw new RuntimeException("Unable to get bytes for: " + new File( file, pResourceName) + " " +e.getMessage());
        }
    }

    @Override
    public InternalResource getResource( String fileName ) {
        File resource = new File( file, fileName);
        return resource.exists() ? ( InternalResource ) ResourceFactory.newFileResource( resource ) : null;
    }

    @Override
    public Collection<String> getFileNames() {
        return IoUtils.recursiveListFile( file );
    }


    @Override
    public byte[] getBytes() {
        try {
            return readBytesFromInputStream(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return "FileKieModule[releaseId=" + getReleaseId() + ",file=" + file + "]";
    }

}
