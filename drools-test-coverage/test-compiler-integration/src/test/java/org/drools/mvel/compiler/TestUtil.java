package org.drools.mvel.compiler;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Results;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtil {

    public static void assertDrlHasCompilationError( String str, int errorNr, KieBaseTestConfiguration kieBaseTestConfiguration ) {
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        Results results = kieBuilder.getResults();
        if ( errorNr > 0 ) {
            assertThat(results.getMessages().size()).isEqualTo(errorNr);
        } else {
            assertThat(results.getMessages().size() > 0).isTrue();
        }
    }
}
