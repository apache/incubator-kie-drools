package org.kie.dmn.feel.lang.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.DynamicTypeUtils.entry;
import static org.kie.dmn.feel.util.DynamicTypeUtils.mapOf;

public class FEELProfileTest {

    @Test
    public void testFeelProfileFunctionsAndValues() {

        // Instantiate a new FEEL with the profile to try the method that uses the data cache
        FEEL feel = FEEL.newInstance(Arrays.asList(new TestFEELProfile()));

        assertThat(feel.evaluate("use cache(\"val 1\")")).isEqualTo("1");
        assertThat(feel.evaluate("use cache(\"val 3\")")).isEqualTo("3");
        assertThat(feel.evaluate("use cache(\"val 5\")")).isNull();
    }

    /**
     * This profile adds a function that use an object introduced as a value to the feel stack to reference external data 
     */
    public static class TestFEELProfile implements FEELProfile {

        @Override
        public List<FEELFunction> getFEELFunctions() {
            return Arrays.asList(new UseCacheFunction());
        }

        @Override
        public Map<String, Object> getValues() {
            return mapOf(entry("[internal-cache]", mapOf(entry("val 1", "1"), entry("val 2", "2"), entry("val 3", "3"))));
        }
    }

    public static class UseCacheFunction extends BaseFEELFunction {

        public UseCacheFunction() {
            super("use cache");
        }

        public FEELFnResult<String> invoke(EvaluationContext ctx, String key) {

            @SuppressWarnings("unchecked")
            Map<String, String> cache = (Map<String, String>) ctx.getValue("[internal-cache]");
            if (cache != null) {
                return FEELFnResult.ofResult(cache.get(key));
            }

            return FEELFnResult.ofResult(null);

        }
    }
}
