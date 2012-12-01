package org.kie.builder.impl;

import org.drools.kproject.memory.MemoryFileSystem;
import org.kie.builder.GAV;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieProject;
import org.kie.builder.KieSessionModel;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryKieJar extends AbstractKieJar {

    private final MemoryFileSystem mfs;
    private final KieProject kieProject;

    public MemoryKieJar(GAV gav, KieProject kieProject, MemoryFileSystem mfs) {
        super(gav);
        this.mfs = mfs;
        this.kieProject = kieProject;
    }
    
    public MemoryFileSystem getMemoryFileSystem() {
        return mfs;
    }

    public byte[] getBytes() {
        return mfs.writeAsBytes();
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(getBytes());
    }

    public Collection<String> getFiles() {
        return mfs.getFileNames();
    }

    public byte[] getBytes(String path) {
        return mfs.getBytes(path);
    }

    public InputStream getInputStream(String path) {
        return new ByteArrayInputStream(getBytes(path));
    }

    protected Map<String, KieBaseModel> indexKieSessions() {
        Map<String, KieBaseModel> kSessions = new HashMap<String, KieBaseModel>();
        for ( KieBaseModel kieBaseModel : kieProject.getKieBaseModels().values() ) {
            for ( KieSessionModel kieSessionModel : kieBaseModel.getKieSessionModels().values() ) {
                kSessions.put( kieSessionModel.getName(), kieBaseModel );
            }
        }
        return kSessions;
    }
}
