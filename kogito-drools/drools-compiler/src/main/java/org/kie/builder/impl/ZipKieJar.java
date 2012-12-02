package org.kie.builder.impl;

import org.drools.kproject.memory.MemoryFileSystem;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProjectModel;
import org.kie.builder.KieSessionModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ZipKieJar implements InternalKieJar {
    private final GAV              gav;
    private final File             file;
    private final KieProjectModel  kieProject;
    
    private Collection<InternalKieJar> dependencies;
   

    public ZipKieJar(GAV gav,
                     KieProjectModel kieProject,
                     File file) {
        this.gav = gav;
        this.file = file;
        this.kieProject = kieProject;
    }
    
    @Override
    public GAV getGAV() {
        return null;
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
        return false;
    }


    @Override
    public byte[] getBytes(String pResourceName) {
        return null;
    }

    @Override
    public Collection<String> getFileNames() {
        return null;
    }

    @Override
    public KieProjectModel getKieProjectModel() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] getBytes() {
        // TODO Auto-generated method stub
        return null;
    }

}
