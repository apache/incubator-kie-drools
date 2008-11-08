package org.drools.builder;

import java.io.Reader;
import java.net.URL;

public interface RuleBuilder {
 
    public void addPackageFromDrl(URL url);
    public void addPackageFromDrl(Reader reader);
    public void addPackageFromXml(URL url);
    public void addPackageFromXml(Reader reader);    
}
