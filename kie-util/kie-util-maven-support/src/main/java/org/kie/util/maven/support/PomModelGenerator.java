package org.kie.util.maven.support;

import java.io.InputStream;

public interface PomModelGenerator {
    PomModel parse(String path, InputStream is);
}
