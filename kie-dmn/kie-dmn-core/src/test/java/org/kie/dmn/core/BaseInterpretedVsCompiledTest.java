package org.kie.dmn.core;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.core.compiler.ExecModelCompilerOption;

@RunWith(Parameterized.class)
public abstract class BaseInterpretedVsCompiledTest {

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{false};
    }

    private final boolean useExecModelCompiler;

    public BaseInterpretedVsCompiledTest(final boolean useExecModelCompiler) {
        this.useExecModelCompiler = useExecModelCompiler;
    }

    @Before
    public void before() {
        System.setProperty(ExecModelCompilerOption.PROPERTY_NAME, Boolean.toString(useExecModelCompiler));
    }

    @After
    public void after() {
        System.clearProperty(ExecModelCompilerOption.PROPERTY_NAME);
    }
}
