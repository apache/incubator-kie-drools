package org.kie.builder;

import java.io.InputStream;
import java.util.List;

public interface KieJar {
    GAV getGAV();

    byte[] getBytes();

    InputStream getInputStream();

    List<String> getFiles();

    byte[] getBytes(String path);

    InputStream getInputStream(String path);
}
