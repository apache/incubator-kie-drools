package org.drools.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractJavaContainerBlockDescr implements JavaContainerBlockDescr {
    private List<JavaBlockDescr> blocks = new ArrayList<JavaBlockDescr>();
    private Map<String, Class< ? >> variables;
    
    public List<JavaBlockDescr> getJavaBlockDescrs() {
        return this.blocks;
    }      

    public void addJavaBlockDescr(JavaBlockDescr descr) {
        this.blocks.add( descr );
    }


    public Map<String, Class< ? >> getInputs() {
        return variables;
    }

    public void setInputs(Map<String, Class< ? >> variables) {
        this.variables = variables;
    }


  
}
