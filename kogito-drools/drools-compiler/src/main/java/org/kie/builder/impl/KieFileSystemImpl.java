package org.kie.builder.impl;

import org.drools.kproject.memory.MemoryFileSystem;
import org.kie.builder.KieFileSystem;
import org.kie.io.Resource;

public class KieFileSystemImpl implements KieFileSystem {

    private final MemoryFileSystem mfs = new MemoryFileSystem();

    public KieFileSystem write(String path, byte[] content) {
        mfs.write(path, content);
        return this;
    }

    public KieFileSystem write(String path, String text) {
        return write(path, text.getBytes());
    }

    public KieFileSystem write(String path, Resource resource) {
        throw new UnsupportedOperationException("org.kie.builder.impl.KieFileSystemImpl.write -> TODO");
    }

    public void delete(String... paths) {
        for (String path : paths) {
            mfs.remove(path);
        }
    }

    public byte[] read(String path) {
        return mfs.read(path);
    }

    MemoryFileSystem asMemoryFileSystem() {
        return mfs;
    }
}
