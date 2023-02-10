package org.drools.reliability;

import java.util.Collection;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class ReliabilityTest {

    // Using @Before rather than @After because sometimes we terminate a process while debugging
    @Before
    public void tearDown() {
        CacheManager.INSTANCE.removeCache("cacheSession_0");
    }

    @Test
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

        KieSessionConfiguration conf2 = KieServices.get().newKieSessionConfiguration();
        conf2.setOption(PersistedSessionOption.fromSession(id));
        KieSession secondSession = kbase.newKieSession(conf2, null);

        secondSession.insert(new Person("Edson", 35));
        secondSession.insert(new Person("Mario", 40));

        assertThat(secondSession.fireAllRules()).isEqualTo(2);
    }

    @Test
    public void testReliableObjectStore() {
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

        firstSession.insert("M");
        firstSession.insert(new Person("Mark", 37));
        firstSession.insert(new Person("Helen", 54));

        assertThat(firstSession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testSessionFromCache() {
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

        KieSessionConfiguration conf2 = KieServices.get().newKieSessionConfiguration();
        conf2.setOption(PersistedSessionOption.fromSession(id));
        KieSession secondSession = kbase.newKieSession(conf2, null);

        System.out.println("secondSession getObjects.size (from firstSession) = " + secondSession.getObjects().size());

        secondSession.insert(new Person("John", 22));
        secondSession.insert(new Person("Mary", 42));

        System.out.println("secondSession getObjects.size = " + secondSession.getObjects().size());

        secondSession.fireAllRules();
    }

}
