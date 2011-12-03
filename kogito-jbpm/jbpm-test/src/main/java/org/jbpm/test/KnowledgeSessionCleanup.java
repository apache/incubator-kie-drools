package org.jbpm.test;

import java.util.HashSet;
import java.util.Set;

import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KnowledgeSessionCleanup implements MethodRule {

    private static Logger logger = LoggerFactory.getLogger(KnowledgeSessionCleanup.class);
    
    protected static ThreadLocal<Set<StatefulKnowledgeSession>> knowledgeSessionSetLocal 
        = new ThreadLocal<Set<StatefulKnowledgeSession>>();
    static { 
        knowledgeSessionSetLocal.set(new HashSet<StatefulKnowledgeSession>());
    }
    
    public Statement apply(final Statement base, FrameworkMethod method, Object target) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try { 
                    base.evaluate();
                }
                finally { 
                    Set<StatefulKnowledgeSession> ksessionSet = knowledgeSessionSetLocal.get();
                    if( ksessionSet.isEmpty() ) { 
                        return;
                    }
                    for( StatefulKnowledgeSession ksession : ksessionSet ) { 
                        logger.debug("Cleaning up " + ksession);
                        if( ksession != null ) { 
                            try { 
                                ksession.dispose();
                            }
                            catch(Throwable t) { 
                                // Don't log anything if dispose() fails
                                // Maybe the test already disposed of the ksession, who knows -- it doesn't matter
                            }
                        }
                    }
                }
            }
        };
    }

}
