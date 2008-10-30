package org.drools.builder;

import java.io.Reader;
import java.net.URL;

public interface ProcessBuilder {
    void addProcessFromXml(URL url);
    void addProcessFromXml(Reader reader);
    
}
