package org.kie.builder.impl;

import org.drools.kproject.memory.MemoryFileSystem;
import org.kie.builder.KieProject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;

public class MemoryKieJar extends AbstractKieJar {

    private final MemoryFileSystem mfs;

    public MemoryKieJar(KieProject kieProject, MemoryFileSystem mfs) {
        super(kieProject);
        this.mfs = mfs;
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
