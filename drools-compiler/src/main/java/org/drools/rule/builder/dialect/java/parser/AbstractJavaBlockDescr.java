package org.drools.rule.builder.dialect.java.parser;

import java.util.List;
import java.util.Map;

public abstract class AbstractJavaBlockDescr implements JavaBlockDescr {
    private Map<String, Class< ? >> inputs;
    private List<JavaLocalDeclarationDescr> inScopeLocalVars;

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
