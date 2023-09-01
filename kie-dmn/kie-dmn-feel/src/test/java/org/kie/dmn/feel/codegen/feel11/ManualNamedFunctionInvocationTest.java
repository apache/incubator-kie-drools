package org.kie.dmn.feel.codegen.feel11;

import java.util.Arrays;

import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.NamedParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class ManualNamedFunctionInvocationTest {
    
    public static final Logger LOG = LoggerFactory.getLogger(ManualNamedFunctionInvocationTest.class);

    static class NamedFunctionExample implements CompiledFEELExpression {
        static final java.math.BigDecimal K_1 = new java.math.BigDecimal(2, java.math.MathContext.DECIMAL128);
        static final String K_s = "FOOBAR";

        /**   FEEL: substring( start position: 2, string: "FOOBAR" )  */
        @Override
        public Object apply(EvaluationContext feelExprCtx) {
            return CompiledFEELSupport.invoke(
                    feelExprCtx,
                    feelExprCtx.getValue("substring"),
                    Arrays.asList(new NamedParameter("start position", K_1),
                                  new NamedParameter("string", K_s)));
        }

    }

    @Test
    public void testManualContext() {
        CompiledFEELExpression compiledExpression = new NamedFunctionExample();
        LOG.debug("{}", compiledExpression);

        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        Object result = compiledExpression.apply(emptyContext);
        LOG.debug("{}", result);

        assertThat(result).isEqualTo("OOBAR");
    }

}
