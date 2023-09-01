package org.kie.dmn.legacy.tests.core.v1_1;

import org.junit.runners.Parameterized;
import org.kie.dmn.core.BaseVariantTest;

import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK;

public abstract class BaseDMN1_1VariantTest extends BaseVariantTest {

    public BaseDMN1_1VariantTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{KIE_API_TYPECHECK};
    }

}
