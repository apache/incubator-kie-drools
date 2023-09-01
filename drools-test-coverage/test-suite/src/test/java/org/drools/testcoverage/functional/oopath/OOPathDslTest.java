package org.drools.testcoverage.functional.oopath;

import org.drools.testcoverage.common.model.InternationalAddress;
import org.drools.testcoverage.common.model.Person;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests usage of OOPath in DSL.
 */
public class OOPathDslTest {

    /**
     * Shows it's possible to use OOPath including some characters that are significant for both DSL and OOPath.
     */
    @Test
    public void dslWithOOPathAndSpecialChars() {
        final String dsl
                = "[when]Person comes from \"{state}\"=person : "
                + "Person( /address#InternationalAddress[ state == \"{state}\" ] )\n";
        final String drl
                = "package P\n"
                + "\n"
                + "import org.drools.testcoverage.common.model.InternationalAddress\n"
                + "import org.drools.testcoverage.common.model.Person\n"
                + "\n"
                + "rule R\n"
                + "when\n"
                + "    Person comes from \"Peaceful State\"\n"
                + "then\n"
                + "end";

        KieSession kieSession = new KieHelper()
                .addContent(dsl, ResourceType.DSL)
                .addContent(drl, ResourceType.DSLR)
                .build()
                .newKieSession();

        final Person person = new Person("Bruno", 21);
        person.setAddress(new InternationalAddress("Some Street", 10, "Beautiful City", "Peaceful State"));
        kieSession.insert(person);
        assertThat(kieSession.fireAllRules()).isEqualTo(1);
    }

}
