package org.drools.cdi;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.drools.KnowledgeBase;


public class TestClassImpl implements TestClass {
    
    public TestClassImpl() {
        
    }

    private @Inject @KBase("org.droosl.test1")  KnowledgeBase kBase1;
    
    public KnowledgeBase getKBase1() {
        return kBase1;
    }
}
