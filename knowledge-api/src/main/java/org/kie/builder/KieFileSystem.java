package org.kie.builder;

import org.kie.io.Resource;

public interface KieFileSystem {

    KieFileSystem write(String path, byte[] content);
    KieFileSystem write(String path, String text);
    KieFileSystem write(String path, Resource resource);

    void delete(String... paths);

    byte[] read(String path);
}
