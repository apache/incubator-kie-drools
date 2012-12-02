package org.kie.builder.impl;

import org.drools.core.util.IoUtils;
import org.drools.kproject.memory.MemoryFileSystem;
import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProjectModel;
import org.kie.builder.KieSessionModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FileKieJar implements InternalKieJar {
    private final GAV              gav;
    private final File             file;
    private final KieProjectModel  kieProject;
    
    private Collection<InternalKieJar> dependencies;
   

    public FileKieJar(GAV gav,
                      KieProjectModel kieProject,
                      File file) {
        this.gav = gav;
        this.file = file;
        this.kieProject = kieProject;
    }


    @Override
    public GAV getGAV() {
        return gav;
    }

    @Override
    public File getFile() {
        return this.file;
    }    

    @Override
    public void setDependencies(Collection<InternalKieJar> dependencies) {
        this.dependencies = dependencies;
    }


    @Override
    public Collection<InternalKieJar> getDependencies() {
        return dependencies;
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
    public KieProjectModel getKieProjectModel() {
        return this.kieProject;
    }


    @Override
    public byte[] getBytes() {
        throw new UnsupportedOperationException();
    }




}
