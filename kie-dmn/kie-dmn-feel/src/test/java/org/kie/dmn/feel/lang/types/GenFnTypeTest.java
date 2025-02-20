package org.kie.dmn.feel.lang.types;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.functions.AbsFunction;
import org.kie.dmn.feel.runtime.functions.AnyFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

import java.util.Arrays;
import java.util.Collections;

class GenFnTypeTest {

    private static final AnyFunction anyFunction = AnyFunction.INSTANCE;
    private final GenFnType genFnType = new GenFnType(Arrays.asList(null, null), null);;

    @Test
    public void testIsInstanceOfWithNoParameters() {
        AbsFunction functionWithNoParams = new AbsFunction();
        assertThat(genFnType.isInstanceOf(functionWithNoParams)).isTrue();
    }

    @Test
    public void testIsInstanceOfWithNonMatchingParameters() {
        FEELFnResult<Boolean> feelFn = anyFunction.invoke(new Object[]{Boolean.TRUE, Boolean.TRUE});
        assertThat(genFnType.isInstanceOf(feelFn)).isFalse();
    }


    @Test
    public void testIsInstanceOfWithMatchingFunctionSignature() {
        GenFnType matchingGenFnType = new GenFnType(Arrays.asList(null, null), null);
        AbsFunction matchingFunction = new AbsFunction();
        assertThat(matchingGenFnType.isInstanceOf(matchingFunction)).isTrue();
    }

    @Test
    public void testIsAssignableValueWithNullValue() {
        assertThat(genFnType.isAssignableValue(null)).isTrue();
    }

    @Test
    public void testIsAssignableValueWithFunction() {
        AbsFunction functionWithNoParams = new AbsFunction();
        assertThat(genFnType.isAssignableValue(functionWithNoParams)).isTrue();
    }


    @Test
    public void testConformsToWithSignature() {
        GenFnType matchingGenFnType = new GenFnType(Collections.singletonList(null), null);
        assertThat(genFnType.conformsTo(matchingGenFnType)).isFalse();
    }

    @Test
    public void testConformsToWithFunctionType() {
        assertThat(genFnType.conformsTo(BuiltInType.FUNCTION)).isTrue();
    }

}