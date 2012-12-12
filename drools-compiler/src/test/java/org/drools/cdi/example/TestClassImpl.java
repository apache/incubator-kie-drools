package org.drools.cdi.example;

import javax.inject.Inject;

import org.kie.KnowledgeBase;
import org.kie.cdi.KBase;


public class TestClassImpl implements TestClass {
    
    public TestClassImpl() {
        
    }

    private //@Inject @KBase("org.droosl.test1")  
    KnowledgeBase kBase1;
    
    public KnowledgeBase getKBase1() {
        return kBase1;
    }
}
