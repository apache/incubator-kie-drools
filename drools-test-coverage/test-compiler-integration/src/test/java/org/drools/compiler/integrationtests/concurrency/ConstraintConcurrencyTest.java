package org.drools.compiler.integrationtests.concurrency;

import java.util.Collection;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.test.testcategory.TurtleTestCategory;

@RunWith(Parameterized.class)
@Category(TurtleTestCategory.class)
public class ConstraintConcurrencyTest extends BaseConcurrencyTest {

    public ConstraintConcurrencyTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(kieBaseTestConfiguration);
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    protected String getDrl() {
        return "package com.example.reproducer\n" +
               "import " + Bus.class.getCanonicalName() + ";\n" +
               "import static " + StaticUtils.class.getCanonicalName() + ".TOSTRING;\n" +
               "dialect \"mvel\"\n" +
               "rule \"rule_mt_1a\"\n" +
               "    when\n" +
               "        $bus : Bus( $check: \"GAMMA RAY\",\n" +
               "                    $title: \"POWER PLANT\",\n" +
               "                    karaoke.dvd[$title] != null,\n" +
               "                    TOSTRING(karaoke.dvd[$title].artist) != null )\n" +
               "    then\n" +
               "end";
    }
}
