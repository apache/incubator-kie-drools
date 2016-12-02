package org.drools.persistence.jpa.processinstance;

import static org.drools.persistence.util.DroolsPersistenceUtil.DROOLS_PERSISTENCE_UNIT_NAME;
import static org.drools.persistence.util.DroolsPersistenceUtil.createEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.test.util.db.PersistenceUtil.cleanUp;
import static org.kie.test.util.db.PersistenceUtil.setupWithPoolingDataSource;

import java.util.Map;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.Context;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;

public class JPAWorkItemManagerTest {

    private Map<String, Object> context;

    @After
    public void cleanup() {
       cleanUp(context);
    }

    @Test
    public void jpaWorkItemManagerTest() {
        context = setupWithPoolingDataSource(DROOLS_PERSISTENCE_UNIT_NAME);
        Environment env = createEnvironment(context);

        KieBase kbase = KnowledgeBuilderFactory.newKnowledgeBuilder().newKnowledgeBase();
        KieSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession( kbase, null, env );

        WorkItemManager manager = ksession.getWorkItemManager();
        assertNotNull(manager);

        manager = ksession.execute(new GenericCommand<WorkItemManager>() {
            @Override
            public WorkItemManager execute( Context context ) {
                KieSession ksession = ((KnowledgeCommandContext) context).getKieSession();
                return ksession.getWorkItemManager();
            }
        });

        assertEquals( "Incorrect work item manager class!",
                JPAWorkItemManager.class,
                manager.getClass());
    }

}
