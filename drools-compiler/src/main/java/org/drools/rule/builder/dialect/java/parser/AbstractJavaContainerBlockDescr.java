package org.drools.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractJavaContainerBlockDescr implements JavaContainerBlockDescr {
    private List<JavaBlockDescr> blocks = new ArrayList<JavaBlockDescr>();
    private Map<String, Class< ? >> inputs;
    private List<JavaLocalDeclarationDescr> inScopeLocalVars;
    
    public List<JavaBlockDescr> getJavaBlockDescrs() {
        return this.blocks;
    }      

    public void addJavaBlockDescr(JavaBlockDescr descr) {
        this.blocks.add( descr );
    }


    public Map<String, Class< ? >> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Class< ? >> variables) {
        this.inputs = variables;
    }

    /**
     * Returns the list of in-code, declared variables that are available
     * in the scope of this block
     * @return
     */
    public List<JavaLocalDeclarationDescr> getInScopeLocalVars() {
        return inScopeLocalVars;
    }

    /**
     * Sets the list of in-code, declared variables that are available
     * in the scope of this block
     */
    public void setInScopeLocalVars( List<JavaLocalDeclarationDescr> inScopeLocalVars ) {
        this.inScopeLocalVars = inScopeLocalVars;
    }    
}
