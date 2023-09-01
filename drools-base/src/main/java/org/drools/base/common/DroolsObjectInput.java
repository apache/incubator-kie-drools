package org.drools.base.common;

import org.drools.base.RuleBase;

import java.io.ObjectInput;

public interface DroolsObjectInput extends ObjectInput {
    ClassLoader getParentClassLoader();
    ClassLoader getClassLoader();
    void setClassLoader(ClassLoader classLoader);
    RuleBase getRuleBase();
    void setRuleBase(RuleBase kBase);
    Package getPackage();
    void setPackage(Package pkg);
}
