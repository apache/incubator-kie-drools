package org.kie.builder.impl;

import org.drools.kproject.memory.MemoryFileSystem;
import org.kie.builder.GAV;
import org.kie.builder.KieProject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;

public class MemoryKieJar extends AbstractKieJar {

    private final MemoryFileSystem mfs;

    public MemoryKieJar(GAV gav, KieProject kieProject, MemoryFileSystem mfs) {
        super(gav, kieProject);
        this.mfs = mfs;
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
}
