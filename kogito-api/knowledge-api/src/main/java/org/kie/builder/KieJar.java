package org.kie.builder;

import java.io.InputStream;
import java.util.Collection;

public interface KieJar {
    GAV getGAV();

    byte[] getBytes();

    InputStream getInputStream();

    Collection<String> getFiles();

    byte[] getBytes(String path);

    InputStream getInputStream(String path);
}
