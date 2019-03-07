package org.drools.compiler.integrationtests;

import org.drools.compiler.Person;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.junit.Assert.assertEquals;

public class OrClauseTest {

    @Test
    public void testOrClause() {
        //PLANNER-1433
        String drl = "import " + Person.class.getName() + ";\n"
                + "rule R1\n"
                + "when\n"
                + "    Person( age > 20 )\n"
                + "    or\n"
                + "    Person( name == \"John\")\n"
                + "then\n"
                + "end";

        KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();

        Person john = new Person("John", 25);
        ksession.insert(john);

        int fired = ksession.fireAllRules();

        assertEquals(1, fired);
    }
}
