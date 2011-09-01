package org.drools.rule.builder.dialect.java.parser;

import java.util.List;
import java.util.Map;

public interface JavaBlockDescr {
    
    public static enum BlockType {
        MODIFY, UPDATE, RETRACT, ENTRY, EXIT, CHANNEL, TRY, CATCH, FINAL, IF, ELSE, FOR, SWITCH, WHILE, THROW
    }

    public BlockType getType();

    public int getStart();

    public int getEnd();
    
    public String getTargetExpression();
    public void setTargetExpression(String str);

    public Map<String, Class<?>> getInputs();

    public void setInputs(Map<String, Class< ? >> variables);

    /**
     * Returns the list of in-code, declared variables that are available
     * in the scope of this block
     * @return
     */
    public List<JavaLocalDeclarationDescr> getInScopeLocalVars();

    /**
     * Sets the list of in-code, declared variables that are available
     * in the scope of this block
     */
    public void setInScopeLocalVars( List<JavaLocalDeclarationDescr> inScopeLocalVars );
}