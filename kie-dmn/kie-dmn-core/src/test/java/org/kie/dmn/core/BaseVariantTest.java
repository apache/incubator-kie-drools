/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder.DMNRuntimeBuilderConfigured;
import org.kie.dmn.core.util.DMNRuntimeUtil;

@RunWith(Parameterized.class)
public abstract class BaseVariantTest implements VariantTest {

    public static enum VariantTestConf implements VariantTest {
        KIE_API_TYPECHECK {

            @Override
            public DMNRuntime createRuntime(String string, Class<?> class1) {
                return DMNRuntimeUtil.createRuntime(string, class1);
            }

            @Override
            public DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
                return DMNRuntimeUtil.createRuntimeWithAdditionalResources(string, class1, string2);
            }
        },
        BUILDER_STRICT {

            private DMNRuntimeBuilderConfigured builder() {
                return DMNRuntimeBuilder.usingStrict();
            }

            @Override
            public DMNRuntime createRuntime(String string, Class<?> class1) {
                return builder().fromClasspathResource(string, class1).getOrElseThrow(RuntimeException::new);
            }

            @Override
            public DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
                return builder().fromClasspathResources(string, class1, string2).getOrElseThrow(RuntimeException::new);
            }
        },
        BUILDER_DEFAULT_NOCL_TYPECHECK {

            private DMNRuntimeBuilderConfigured builder() {
                return DMNRuntimeBuilder.fromDefaults().setRootClassLoader(null).setOption(new RuntimeTypeCheckOption(true)).buildConfiguration();
            }

            @Override
            public DMNRuntime createRuntime(String string, Class<?> class1) {
                return builder().fromClasspathResource(string, class1).getOrElseThrow(RuntimeException::new);
            }

            @Override
            public DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
                return builder().fromClasspathResources(string, class1, string2).getOrElseThrow(RuntimeException::new);
            }
        };

    }

    @Parameterized.Parameters(name = "{0}")
    public static Object[] params() {
        return new Object[]{VariantTestConf.KIE_API_TYPECHECK, VariantTestConf.BUILDER_STRICT, VariantTestConf.BUILDER_DEFAULT_NOCL_TYPECHECK};
    }

    private final VariantTestConf testConfig;

    public BaseVariantTest(final VariantTestConf testConfig) {
        this.testConfig = testConfig;
    }

    @Override
    public DMNRuntime createRuntime(String string, Class<?> class1) {
        return testConfig.createRuntime(string, class1);
    }

    @Override
    public DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2) {
        return testConfig.createRuntimeWithAdditionalResources(string, class1, string2);
    }
}

interface VariantTest {

    DMNRuntime createRuntime(String string, Class<?> class1);

    DMNRuntime createRuntimeWithAdditionalResources(String string, Class<?> class1, String... string2);

}
