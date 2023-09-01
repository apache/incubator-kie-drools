package org.kie.dmn.legacy.tests.core.v1_1;

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class VacationDaysTest extends BaseDMN1_1VariantTest {

    public VacationDaysTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Test
    public void testSolutionCase1() {
        executeTest( 16, 1, 27 );
    }

    @Test
    public void testSolutionCase2() {
        executeTest( 25, 5, 22 );
    }

    @Test
    public void testSolutionCase3() {
        executeTest( 44, 20, 24 );
    }

    @Test
    public void testSolutionCase4() {
        executeTest( 44, 30, 30 );
    }

    @Test
    public void testSolutionCase5() {
        executeTest( 50, 20, 24 );
    }

    @Test
    public void testSolutionCase6() {
        executeTest( 50, 30, 30 );
    }

    @Test
    public void testSolutionCase7() {
        executeTest( 60, 20, 30 );
    }

    private void executeTest(final int age, final int yearsService, final int expectedVacationDays ) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0020-vacation-days.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn", "0020-vacation-days" );
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();

        context.set( "Age", age );
        context.set( "Years of Service", yearsService );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Total Vacation Days")).isEqualTo(BigDecimal.valueOf(expectedVacationDays));
    }
}

