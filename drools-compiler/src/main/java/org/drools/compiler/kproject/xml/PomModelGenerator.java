package org.drools.compiler.kproject.xml;

import java.io.InputStream;

public interface PomModelGenerator {
    PomModel parse(String path, InputStream is);
}
