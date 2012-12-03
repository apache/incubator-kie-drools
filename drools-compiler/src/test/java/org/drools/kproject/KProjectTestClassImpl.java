package org.drools.kproject;

import org.kie.builder.KieContainer;
import org.kie.runtime.KieBase;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatelessKieSession;

public class KProjectTestClassImpl implements KProjectTestClass {
    
    KieContainer kContainer;
    String namespace;
    
    public KProjectTestClassImpl(String namespace, KieContainer kContainer) {
        this.namespace = namespace;
        this.kContainer = kContainer;
    }

    @Override
    public KieBase getKBase1() {
        return this.kContainer.getKieBase( namespace + ".KBase1" );
    }

    @Override
    public KieBase getKBase2() {
        return this.kContainer.getKieBase( namespace + ".KBase2" );
    }

    @Override
    public KieBase getKBase3() {
        return this.kContainer.getKieBase( namespace + ".KBase3" );
    }

    @Override
    public StatelessKieSession getKBase1KSession1() {
        return this.kContainer.getKieStatelessSession( namespace + ".KSession1" );        
    }

    @Override
    public KieSession getKBase1KSession2() {
        return this.kContainer.getKieSession(namespace + ".KSession2" );
    }

    @Override
    public KieSession getKBase2KSession3() {
        return this.kContainer.getKieSession(namespace + ".KSession3" );
    }

    @Override
    public StatelessKieSession getKBase3KSession4() {
        return this.kContainer.getKieStatelessSession( namespace + ".KSession4" );
    }

}
