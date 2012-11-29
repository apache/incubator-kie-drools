package org.kie.builder.impl;

import org.drools.kproject.memory.MemoryFileSystem;
import org.kie.builder.KieProject;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class MemoryKieJar extends AbstractKieJar {

    private final MemoryFileSystem mfs;

    public MemoryKieJar(KieProject kieProject, MemoryFileSystem mfs) {
        super(kieProject);
        this.mfs = mfs;
    }

    public File asFile() {
        return mfs.writeAsJar(new File( System.getProperty( "java.io.tmpdir" ) ), getGAV().toString());
    }

    public byte[] getBytes() {
        throw new UnsupportedOperationException("org.kie.builder.impl.MemoryKieJar.getBytes -> TODO");

    }

    public InputStream getInputStream() {
        throw new UnsupportedOperationException("org.kie.builder.impl.MemoryKieJar.getInputStream -> TODO");

    }

    public List<String> getFiles() {
        throw new UnsupportedOperationException("org.kie.builder.impl.MemoryKieJar.getFiles -> TODO");

    }

    public byte[] getBytes(String path) {
        throw new UnsupportedOperationException("org.kie.builder.impl.MemoryKieJar.getBytes -> TODO");

    }

    public InputStream getInputStream(String path) {
        throw new UnsupportedOperationException("org.kie.builder.impl.MemoryKieJar.getInputStream -> TODO");

    }
}
