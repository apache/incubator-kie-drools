package org.drools.core.process.instance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.command.Context;

public class WorkItemManagerTest {

    @Test
    public void workItemManagerTest() {
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        KieSession ksession = kbase.newKieSession();

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
                DefaultWorkItemManager.class,
                manager.getClass());
    }

}
