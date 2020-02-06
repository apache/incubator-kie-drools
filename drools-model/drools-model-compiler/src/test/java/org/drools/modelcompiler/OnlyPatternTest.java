package org.drools.modelcompiler;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.drools.modelcompiler.BaseModelTest.RUN_TYPE.PATTERN_DSL;

@RunWith(Parameterized.class)
public abstract class OnlyPatternTest extends BaseModelTest {

    public OnlyPatternTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    final static Object[] EXCLUDE_FLOW = {
            RUN_TYPE.STANDARD_FROM_DRL,
            PATTERN_DSL,
    };

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return EXCLUDE_FLOW;
    }
}
