package org.jbpm.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.kie.runtime.StatefulKnowledgeSession;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

public class KnowledgeSessionCleanup implements MethodRule {

    protected static ThreadLocal<Set<StatefulKnowledgeSession>> knowledgeSessionSetLocal = new ThreadLocal<Set<StatefulKnowledgeSession>>();
    static {
        knowledgeSessionSetLocal.set(new HashSet<StatefulKnowledgeSession>());
    }

    public static void addKnowledgeSessionForCleanup(StatefulKnowledgeSession ksession) { 
        knowledgeSessionSetLocal.get().add(ksession);
    }
    
    public Statement apply(final Statement base, FrameworkMethod method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    base.evaluate();
                } finally {
                    Set<StatefulKnowledgeSession> ksessionSet = knowledgeSessionSetLocal.get();
                    if (!ksessionSet.isEmpty()) {
                        // return'ing here will keep throwables (above) from being thrown!
                        Iterator<StatefulKnowledgeSession> iter = ksessionSet.iterator();
                        while (iter.hasNext()) {
                            StatefulKnowledgeSession ksession = iter.next();
                            if (ksession != null) {
                                try {
                                    ksession.dispose();
                                } catch (Throwable t) {
                                    // Don't log anything if dispose() fails
                                    // Maybe the test already disposed of the ksession, who knows -- it doesn't matter
                                }
                            }
                            iter.remove();
                        }
                    }
                }
            }
        };
    }

}
