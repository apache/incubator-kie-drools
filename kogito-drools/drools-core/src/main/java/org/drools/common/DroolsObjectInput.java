package org.drools.common;

import java.io.ObjectInput;

import org.drools.rule.Package;

public interface DroolsObjectInput extends ObjectInput {
    ClassLoader getParentClassLoader(); 
    ClassLoader getClassLoader(); 
    void setClassLoader(ClassLoader classLoader);
    
    InternalRuleBase getRuleBase();    
    void setRuleBase(InternalRuleBase ruleBase);
    
    void setWorkingMemory(InternalWorkingMemory workingMemory);
    InternalWorkingMemory getWorkingMemory();
    
    Package getPackage();
    void setPackage(Package pkg);
}
