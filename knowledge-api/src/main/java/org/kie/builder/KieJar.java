package org.kie.builder;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface KieJar {
    GAV getGAV();

    File asFile();

    byte[] getBytes();

    InputStream getInputStream();

    List<String> getFiles();

    byte[] getBytes(String path);

    InputStream getInputStream(String path);
}
