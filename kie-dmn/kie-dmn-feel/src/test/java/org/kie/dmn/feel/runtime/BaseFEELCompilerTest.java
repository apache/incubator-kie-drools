package org.kie.dmn.feel.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.parser.feel11.profiles.DoCompileFEELProfile;
import org.kie.dmn.feel.runtime.BaseFEELTest.FEEL_TARGET;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public abstract class BaseFEELCompilerTest {

    @Parameterized.Parameter(4)
    public FEEL_TARGET testFEELTarget;

    private FEEL feel = null; // due to @Parameter injection by JUnit framework, need to defer FEEL init to actual instance method, to have the opportunity for the JUNit framework to initialize all the @Parameters

    @Parameterized.Parameter(0)
    public String expression;

    @Parameterized.Parameter(1)
    public Map<String, Type> inputTypes;

    @Parameterized.Parameter(2)
    public Map<String, Object> inputValues;

    @Parameterized.Parameter(3)
    public Object result;

    @Test
    public void testExpression() {
        feel = (testFEELTarget == FEEL_TARGET.JAVA_TRANSLATED) ? FEEL.newInstance(Collections.singletonList(new DoCompileFEELProfile())) : FEEL.newInstance();
        assertResult( expression, inputTypes, inputValues, result );
    }

    protected void assertResult(final String expression, final Map<String, Type> inputTypes, final Map<String, Object> inputValues, final Object result) {
        final CompilerContext ctx = feel.newCompilerContext();
        inputTypes.forEach(ctx::addInputVariableType);
        final CompiledExpression compiledExpression = feel.compile(expression, ctx );

        if( result == null ) {
        	assertThat(feel.evaluate( compiledExpression, inputValues)).as("Evaluating: '" + expression + "'").isNull();
        } else if( result instanceof Class<?> ) {
        	assertThat(feel.evaluate( compiledExpression, inputValues)).as("Evaluating: '" + expression + "'").isInstanceOf((Class<?>) result);
        } else {
        	assertThat(feel.evaluate( compiledExpression, inputValues)).as("Evaluating: '" + expression + "'").isEqualTo(result);
        }
    }

    protected static List<Object[]> enrichWith5thParameter(final Object[][] cases) {
        final List<Object[]> results = new ArrayList<>();
        for (final Object[] c : cases) {
            results.add(new Object[]{c[0], c[1], c[2], c[3], FEEL_TARGET.AST_INTERPRETED});
        }
        for (final Object[] c : cases) {
            results.add(new Object[]{c[0], c[1], c[2], c[3], FEEL_TARGET.JAVA_TRANSLATED});
        }
        return results;
    }
}
