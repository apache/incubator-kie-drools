package org.drools.compiler.integrationtests.nomvel;

import org.drools.testcoverage.common.model.Person;
import org.junit.Test;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.compiler.integrationtests.nomvel.TestUtil.getKieContainer;

public class VerifyTest {

    @Test
    public void verify_kieContainer_executableModel() throws Exception {
        String drl =
                "import " + Person.class.getCanonicalName() + " \n" +
                     "rule R1\n" +
                     "when\n" +
                     " Person()\n" +
                     "then\n" +
                     "end";

        final KieContainer kieContainer = getKieContainer(null, drl);

        Results results = kieContainer.verify();
        assertThat(results.getMessages().size()).isEqualTo(0);

        Results resultsWithKieBaseName = kieContainer.verify("defaultKieBase");
        assertThat(resultsWithKieBaseName.getMessages().size()).isEqualTo(0);
    }
}
