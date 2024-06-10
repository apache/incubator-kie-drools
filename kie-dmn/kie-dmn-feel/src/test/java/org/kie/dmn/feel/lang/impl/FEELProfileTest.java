/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.lang.impl;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.DynamicTypeUtils.entry;
import static org.kie.dmn.feel.util.DynamicTypeUtils.mapOf;

class FEELProfileTest {

    @Test
    void feelProfileFunctionsAndValues() {

        // Instantiate a new FEEL with the profile to try the method that uses the data cache
        FEEL feel = FEELBuilder.builder().withProfiles(List.of(new TestFEELProfile())).build();

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
            return List.of(new UseCacheFunction());
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
