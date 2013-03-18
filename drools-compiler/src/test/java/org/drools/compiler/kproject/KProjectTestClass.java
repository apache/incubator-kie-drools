package org.drools.compiler.kproject;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

public interface KProjectTestClass {
    public KieBase getKBase1();

    public KieBase getKBase2();

    public KieBase getKBase3();
    
    public StatelessKieSession getKBase1KSession1();
    
    public KieSession getKBase1KSession2();
    
    public KieSession getKBase2KSession3();

    public StatelessKieSession getKBase3KSession4();
}
