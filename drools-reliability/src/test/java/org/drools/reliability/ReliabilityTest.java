package org.drools.reliability;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class ReliabilityTest {

    @Test
    public void test() {
        String drl =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule X when\n" +
                "  $p: Person ( getName().startsWith(\"M\") )\n" +
                "then\n" +
                "  System.out.println( $p.getAge() );\n" +
                "end";

        KieSession ksession = new KieHelper()
                .addContent(drl, ResourceType.DRL)
                .build().newKieSession();

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }
}
