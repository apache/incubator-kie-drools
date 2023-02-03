package org.drools.reliability;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.internal.utils.KieHelper;

public class ReliabilityTest {

    @Test @Ignore
    public void test() {
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  $s: String()\n" +
                "  $p: Person( getName().startsWith($s) )\n" +
                "then\n" +
                "  System.out.println( $p.getAge() );\n" +
                "end";

        KieBase kbase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        int nextSessionId = ((InternalKnowledgeBase) kbase).getWorkingMemoryCounter();
        conf.setOption(PersistedSessionOption.newSession(nextSessionId));
        KieSession firstSession = kbase.newKieSession(conf, null);

        long id = firstSession.getIdentifier();

        firstSession.insert("M");
        firstSession.insert(new Person("Mark", 37));

        firstSession.fireAllRules();

        KieSessionConfiguration conf2 = KieServices.get().newKieSessionConfiguration();
        conf2.setOption(PersistedSessionOption.fromSession(id));
        KieSession secondSession = kbase.newKieSession(conf2, null);

        secondSession.insert(new Person("Edson", 35));
        secondSession.insert(new Person("Mario", 40));

        secondSession.fireAllRules();
        //assertThat(secondSession.fireAllRules()).isEqualTo(2);

        KieSessionConfiguration conf3 = KieServices.get().newKieSessionConfiguration();
        int nextSessionId3 = ((InternalKnowledgeBase) kbase).getWorkingMemoryCounter();
        conf3.setOption(PersistedSessionOption.newSession(nextSessionId3));
        KieSession thirdSession = kbase.newKieSession(conf3, null);

        thirdSession.insert("J");
        thirdSession.insert(new Person("John", 42));

        thirdSession.fireAllRules();
    }
}
