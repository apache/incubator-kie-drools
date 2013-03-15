package org.drools.compiler.cdi.example;

import org.kie.KnowledgeBase;


public class TestClassImpl implements TestClass {
    
    public TestClassImpl() {
        
    }

    private //@Inject @KBase("org.droosl.test1")  
    KnowledgeBase kBase1;
    
    public KnowledgeBase getKBase1() {
        return kBase1;
    }
}
