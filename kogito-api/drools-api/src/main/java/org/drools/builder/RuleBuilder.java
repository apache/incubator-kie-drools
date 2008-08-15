package org.drools.builder;

import java.io.Reader;

public interface RuleBuilder {
    public void addPackageFromDrl(Reader reader);
    public void addPackageFromXml(Reader reader);    
}
