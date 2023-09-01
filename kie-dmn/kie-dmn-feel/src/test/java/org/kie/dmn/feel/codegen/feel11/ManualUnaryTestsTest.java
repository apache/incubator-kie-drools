package org.kie.dmn.feel.codegen.feel11;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.util.EvalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings.lt;

public class ManualUnaryTestsTest {

    public static final Logger LOG = LoggerFactory.getLogger(ManualUnaryTestsTest.class);

    public static class ManualImpl1 implements CompiledFEELUnaryTests {

        private static final UnaryTest UT_a = (feelExprCtx, left) -> lt(left, new BigDecimal(47, MathContext.DECIMAL128));

        private static final UnaryTest UT_b = (feelExprCtx, left) -> lt(left, new BigDecimal(1, MathContext.DECIMAL128));

        @Override
        public List<UnaryTest> getUnaryTests() {
            return Arrays.asList(UT_a, UT_b);
        }

    }

    @Test
    public void testManualUnaryTests() {
        Object left = EvalHelper.coerceNumber(7);

        CompiledFEELUnaryTests compiledUnaryTests = new ManualImpl1();
        LOG.debug("{}", compiledUnaryTests);

        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        List<Boolean> result = compiledUnaryTests.getUnaryTests().stream().map(ut -> ut.apply(emptyContext, left)).collect(Collectors.toList());
        LOG.debug("{}", result);

        assertThat(result).asList().containsExactly(true, false);
    }

}
