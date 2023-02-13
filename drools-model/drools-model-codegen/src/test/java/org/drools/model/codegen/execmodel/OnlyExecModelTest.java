package org.drools.model.codegen.execmodel;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.drools.model.codegen.execmodel.BaseModelTest.RUN_TYPE.PATTERN_DSL;

@RunWith(Parameterized.class)
public abstract class OnlyExecModelTest extends BaseModelTest {

    public OnlyExecModelTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    final static Object[] ONLY_EXEC_MODEL = {
            PATTERN_DSL,
    };

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return ONLY_EXEC_MODEL;
    }
}
