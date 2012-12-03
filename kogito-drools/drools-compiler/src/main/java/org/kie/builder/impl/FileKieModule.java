package org.kie.builder.impl;

import org.drools.core.util.IoUtils;
import org.kie.builder.GAV;
import org.kie.builder.KieModuleModel;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

public class FileKieModule extends AbstractKieModules implements InternalKieModule {
    private final GAV              gav;
    private final File             file;
    private final KieModuleModel  kieProject;   

    public FileKieModule(GAV gav,
                      KieModuleModel kieProject,
                      File file) {
        super( gav );
        this.gav = gav;
        this.file = file;
        this.kieProject = kieProject;
    }

    @Override
    public File getFile() {
        return this.file;
    }    


    @Override
    public boolean isAvailable(String pResourceName) {
        return new File( file, pResourceName).exists();
    }


    @Override
    public byte[] getBytes(String pResourceName) {
        try {
            return IoUtils.readBytesFromInputStream( new FileInputStream( new File( file, pResourceName) ) );
        } catch ( Exception e ) {
            throw new RuntimeException("Unable to get bytes for: " + new File( file, pResourceName) );
        }
    }


    @Override
    public Collection<String> getFileNames() {
        return IoUtils.recursiveListFile( file );
    }


    @Override
    public KieModuleModel getKieProjectModel() {
        return this.kieProject;
    }


    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }




}
