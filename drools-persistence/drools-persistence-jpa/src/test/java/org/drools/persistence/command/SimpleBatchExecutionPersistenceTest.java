package org.drools.persistence.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.drools.core.impl.RuleBaseFactory;
import org.drools.mvel.compiler.command.SimpleBatchExecutionTest;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

@RunWith(Parameterized.class)
public class SimpleBatchExecutionPersistenceTest extends SimpleBatchExecutionTest {

    private Map<String, Object> context;
    private boolean locking;

    @Parameters(name="{0}")
    public static Collection<Object[]> persistence() {
        Object[][] locking = new Object[][] {
                { DroolsPersistenceUtil.OPTIMISTIC_LOCKING },
                { DroolsPersistenceUtil.PESSIMISTIC_LOCKING }
                };
        return Arrays.asList(locking);
    };

    public SimpleBatchExecutionPersistenceTest(String locking) {
        this.locking = DroolsPersistenceUtil.PESSIMISTIC_LOCKING.equals(locking);
    };

    @After
    public void cleanUpPersistence() throws Exception {
        disposeKSession();
        DroolsPersistenceUtil.cleanUp(context);
        context = null;
    }

    protected StatefulKnowledgeSession createKnowledgeSession(KieBase kbase) {
        if( context == null ) {
            context = DroolsPersistenceUtil.setupWithPoolingDataSource(DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME);
        }
        KieSessionConfiguration ksconf = RuleBaseFactory.newKnowledgeSessionConfiguration();
        Environment env = DroolsPersistenceUtil.createEnvironment(context);
        if( this.locking ) {
            env.set(EnvironmentName.USE_PESSIMISTIC_LOCKING, true);
        }
        return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, ksconf, env);
    }
}
