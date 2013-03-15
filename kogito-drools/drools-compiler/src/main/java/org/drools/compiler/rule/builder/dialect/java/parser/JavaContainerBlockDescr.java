package org.drools.compiler.rule.builder.dialect.java.parser;

import java.util.List;

public interface JavaContainerBlockDescr extends JavaBlockDescr {
    public void addJavaBlockDescr(JavaBlockDescr descr);
    
    public List<JavaBlockDescr> getJavaBlockDescrs();    
}
