package org.drools.compiler.common;

import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;

public class MarshallerTest {

    @Test
    public void testAgendaReconciliation() throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();
        assertEquals( 3, ksession.fireAllRules() );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true );

        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void testAgendaReconciliation3() throws Exception {
        String str =
                "import java.util.Collection\n" +
                        "rule R1 when\n" +
                        "    String() from [ \"x\", \"y\", \"z\" ]\n" +
                        "then\n" +
                        "end\n";

        KieBase kbase = new KieHelper().addContent(str, ResourceType.DRL ).build();
        KieSession ksession = kbase.newKieSession();

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true );

        assertEquals( 3, ksession.fireAllRules() );
    }
}
