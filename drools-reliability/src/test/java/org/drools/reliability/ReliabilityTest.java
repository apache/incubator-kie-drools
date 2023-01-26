package org.drools.reliability;

import org.junit.Ignore;
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
        conf.setOption(PersistedSessionOption.newSession());
        KieSession firstSession = kbase.newKieSession(conf, null);

        long id = firstSession.getIdentifier();

        firstSession.insert("M");
        firstSession.insert(new Person("Mark", 37));

        KieSessionConfiguration conf2 = KieServices.get().newKieSessionConfiguration();
        conf.setOption(PersistedSessionOption.fromSession(id));
        KieSession secondSession = kbase.newKieSession(conf2, null);

        secondSession.insert(new Person("Edson", 35));
        secondSession.insert(new Person("Mario", 40));

        assertThat(secondSession.fireAllRules()).isEqualTo(2);
    }
}
