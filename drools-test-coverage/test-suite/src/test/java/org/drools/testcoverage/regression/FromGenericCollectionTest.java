package org.drools.testcoverage.regression;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.ClassObjectFilter;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class FromGenericCollectionTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public FromGenericCollectionTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Test
    public void testGenerics() {

        final String drl = "package " + TestConstants.PACKAGE_REGRESSION + "\n"
                + " import java.util.Map.Entry\n"
                + " import java.util.List\n"
                + " import " + GenericHolder.class.getCanonicalName() + "\n"
                + " rule checkCrazyMap\n"
                + " when\n"
                + "        GenericHolder( $map : crazyMap )\n"
                + "        $entry : Entry( $list : value ) from $map.entrySet\n"
                + "        $string : String ( ) from $list\n"
                + " then\n"
                + "        insert(new Boolean(true));\n"
                + " end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl(TestConstants.PACKAGE_REGRESSION,
                                                                           kieBaseTestConfiguration, drl);
        final KieSession ksession = kieBase.newKieSession();
        try {
            final Map<String, List<String>> crazyMap = new HashMap<String, List<String>>();
            crazyMap.put("foo", List.of("bar"));
            final GenericHolder gh = new GenericHolder();
            gh.setCrazyMap(crazyMap);

            ksession.insert(gh);
            ksession.fireAllRules();

            assertThat(ksession.getObjects(new ClassObjectFilter(Boolean.class)).size()).isEqualTo(1);
        } finally {
            ksession.dispose();
        }
    }

    public static class GenericHolder {
        private Map<String, List<String>> crazyMap;

        public Map<String, List<String>> getCrazyMap() {
            return crazyMap;
        }

        public void setCrazyMap(final Map<String, List<String>> crazyMap) {
            this.crazyMap = crazyMap;
        }
    }

}
