package org.drools.compiler.kie.builder.impl;

import static org.drools.core.util.IoUtils.readBytesFromInputStream;
import static org.drools.core.util.IoUtils.recursiveListFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.drools.core.util.IoUtils;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

public class FileKieModule extends AbstractKieModule implements InternalKieModule {
    private final File file;
    private final Collection<String> fileNames;

    public FileKieModule(ReleaseId releaseId,
                      KieModuleModel kieProject,
                      File file) {
        super(releaseId, kieProject );
        this.file = file;
        this.fileNames = new ArrayList<String>();
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
    public byte[] getBytes(String pResourceName) {
        try {
            File resource = new File( file, pResourceName);
            if( resource.exists() ) {
                return IoUtils.readBytesFromInputStream( new FileInputStream( resource ) );
            } else {
                return null;
            }
        } catch ( Exception e ) {
            throw new RuntimeException("Unable to get bytes for: " + new File( file, pResourceName) );
        }
    }


    @Override
    public Collection<String> getFileNames() {
    	if (fileNames.isEmpty()) {
    		fileNames.addAll(recursiveListFile( file ));
    	}
    	
    	return fileNames;
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
        return "FileKieModule[ ReleaseId=" + getReleaseId() + "file=" + file + "]";
    }

}
