package org.drools.compiler.cdi.test;

import org.kie.KnowledgeBase;
import org.kie.api.cdi.KBase;
import org.kie.api.cdi.KSession;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.StatelessKnowledgeSession;

import javax.inject.Inject;


public class KProjectTestClassfol4 implements org.drools.compiler.cdi.test.KProjectTestClass {
    private
    @Inject
    @KBase("fol4.test1.KBase1")
    KnowledgeBase kBase1;

    public KnowledgeBase getKBase1() {
        return kBase1;
    }

    private
    @Inject
    @KBase("fol4.test2.KBase2")
    KnowledgeBase kBase2;

    public KnowledgeBase getKBase2() {
        return kBase2;
    }

    private
    @Inject
    @KBase("fol4.test3.KBase3")
    KnowledgeBase kBase3;

    public KnowledgeBase getKBase3() {
        return kBase3;
    }

    private
    @Inject
    @KSession("fol4.test1.KSession1")
    StatelessKnowledgeSession kBase1kSession1;

    public StatelessKnowledgeSession getKBase1KSession1() {
        return kBase1kSession1;
    }

    private
    @Inject
    @KSession("fol4.test1.KSession2")
    StatefulKnowledgeSession kBase1kSession2;

    public StatefulKnowledgeSession getKBase1KSession2() {
        return kBase1kSession2;
    }

    private
    @Inject
    @KSession("fol4.test2.KSession3")
    StatefulKnowledgeSession kBase2kSession3;

    public StatefulKnowledgeSession getKBase2KSession3() {
        return kBase2kSession3;
    }

    private
    @Inject
    @KSession("fol4.test3.KSession4")
    StatelessKnowledgeSession kBase3kSession4;

    public StatelessKnowledgeSession getKBase3KSession4() {
        return kBase3kSession4;
    }
}
