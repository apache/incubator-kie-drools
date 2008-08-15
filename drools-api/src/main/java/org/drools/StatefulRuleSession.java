package org.drools;

public interface StatefulRuleSession {
    FactHandle insertObject(Object object);
    void retractObject(FactHandle factHandle);
    void updateObject(FactHandle factHandle);
    void updateObject(FactHandle factHandle, Object object);
    
    void fireAllRules();
}
