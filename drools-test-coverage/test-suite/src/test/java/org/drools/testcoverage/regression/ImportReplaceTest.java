package org.drools.testcoverage.regression;

import java.io.StringReader;

import java.util.Collection;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;

@RunWith(Parameterized.class)
public class ImportReplaceTest {

    private static final String declares =
            "package " + TestConstants.PACKAGE_REGRESSION + ".importreplace\n"
            + "import " + TestConstants.PACKAGE_TESTCOVERAGE + ".common.model.Person\n"
            + "declare SomePerson\n"
            + "    person : Person\n"
            + "    weight : double\n"
            + "    height : double\n"
            + "end\n";

    private static final String rules =
            "package " + TestConstants.PACKAGE_REGRESSION + ".importreplace\n"
            + "import " + TestConstants.PACKAGE_TESTCOVERAGE + ".common.model.Person\n"
            + "declare Holder\n"
            + "    person : Person\n"
            + "end\n"
            + "rule \"create holder\"\n"
            + "    when\n"
            + "        person : Person( )\n"
            + "        not (\n"
            + "            Holder( person; )\n"
            + "        )\n"
            + "    then\n"
            + "        insert(new Holder(person));\n"
            + "end\n";

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ImportReplaceTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void test() {
        final Resource declaresResource =
                KieServices.Factory.get().getResources().newReaderResource(new StringReader(declares));
        declaresResource.setTargetPath("src/main/resources/declares.drl");

        final Resource rulesResource =
                KieServices.Factory.get().getResources().newReaderResource(new StringReader(rules));
        rulesResource.setTargetPath("src/main/resources/rules.drl");

        // this should be OK
        KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, declaresResource, rulesResource);

        // this should be fine too
        KieUtil.getKieBuilderFromResources(kieBaseTestConfiguration, true, rulesResource, declaresResource);
    }

}
