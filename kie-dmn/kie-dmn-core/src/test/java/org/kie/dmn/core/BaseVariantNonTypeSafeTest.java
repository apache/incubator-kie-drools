package org.kie.dmn.core;

import org.junit.runners.Parameterized;

import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.BUILDER_STRICT;
import static org.kie.dmn.core.BaseVariantTest.VariantTestConf.KIE_API_TYPECHECK;

public abstract class BaseVariantNonTypeSafeTest extends BaseVariantTest {

    public BaseVariantNonTypeSafeTest(VariantTestConf testConfig) {
        super(testConfig);
    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{KIE_API_TYPECHECK, BUILDER_STRICT, BUILDER_DEFAULT_NOCL_TYPECHECK};
    }

}
