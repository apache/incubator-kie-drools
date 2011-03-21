package org.drools.rule.builder.dialect.java.parser;

import java.util.List;
import java.util.Map;

public interface JavaContainerBlockDescr extends JavaBlockDescr {
    public void addJavaBlockDescr(JavaBlockDescr descr);
    
    public List<JavaBlockDescr> getJavaBlockDescrs();    
}
