package org.drools.compiler.integrationtests.concurrency;

import java.util.Collection;
import java.util.Date;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.KieSession;
import org.kie.test.testcategory.TurtleTestCategory;

@RunWith(Parameterized.class)
@Category(TurtleTestCategory.class)
public class MVELDateClassFieldReaderConcurrencyTest extends BaseConcurrencyTest {

    public MVELDateClassFieldReaderConcurrencyTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        super(kieBaseTestConfiguration);
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    protected String getDrl() {
        return "package com.example.reproducer\n" +
               "import " + Bus.class.getCanonicalName() + ";\n" +
               "import java.util.Date;\n" +
               "dialect \"mvel\"\n" +
               "rule R1\n" +
               "    when\n" +
               "        $d : Date()\n" +
               "        $bus1 : Bus( new Date(name.concat(karaoke.dvd[\"POWER PLANT\"].artist).length) == $d )\n" +
               "    then\n" +
               "end\n";
    }

    protected void insertFacts(KieSession kSession) {
        Bus bus1 = new Bus("red", 30);
        bus1.getKaraoke().getDvd().put("POWER PLANT", new Album("POWER PLANT", "GAMMA RAY"));
        bus1.getKaraoke().getDvd().put("Somewhere Out In Space", new Album("Somewhere Out In Space", "GAMMA RAY"));
        kSession.insert(bus1);
        kSession.insert(new Date());
    }

}
