/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.api.context.impl;

import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractKogitoBuildContextTest {

    protected KogitoBuildContext.Builder builder;

    @BeforeEach
    public void init() {
        builder = MockKogitoBuildContext.builder().withAddonsConfig(AddonsConfig.DEFAULT);
    }

    @Test
    public void packageNameValidation() {
        assertThat(builder.build().getPackageName()).isEqualTo(KogitoBuildContext.DEFAULT_PACKAGE_NAME);
        assertThatThrownBy(() -> builder.withPackageName(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> builder.withPackageName("i.am.an-invalid.package-name.sorry")).isInstanceOf(IllegalArgumentException.class);
        KogitoBuildContext context = builder.withPackageName(AbstractKogitoBuildContext.DEFAULT_GROUP_ID).build();
        assertThat(context.getPackageName()).isNotEqualTo(AbstractKogitoBuildContext.DEFAULT_GROUP_ID);
    }

    @Test
    public void applicationPropertiesValidation() {
        assertThat(builder.build().getApplicationProperties()).isNotNull();
        assertThatThrownBy(() -> builder.withApplicationProperties((Properties) null)).isInstanceOf(NullPointerException.class).hasMessageContaining("applicationProperties");
    }

    @Test
    public void withAddonsConfig() {
        assertThat(builder.withAddonsConfig(null).build().getAddonsConfig()).isNotNull().isNotEqualTo(AddonsConfig.DEFAULT);
        assertThat(builder.withAddonsConfig(AddonsConfig.DEFAULT).build().getAddonsConfig()).isEqualTo(AddonsConfig.DEFAULT);
    }

    @Test
    public void withClassAvailabilityResolver() {
        assertThatThrownBy(() -> builder.withClassAvailabilityResolver(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("classAvailabilityResolver");
    }

    @Test
    public void withClassSubTypeAvailabilityResolver() {
        assertThatThrownBy(() -> builder.withClassSubTypeAvailabilityResolver(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("classSubTypeAvailabilityResolver");
    }

    @Test
    public void withClassLoader() {
        assertThatThrownBy(() -> builder.withClassLoader(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("classLoader");
    }

    @Test
    public void withAppPaths() {
        assertThat(builder.build().getAppPaths()).isNotNull();
        assertThatThrownBy(() -> builder.withAppPaths(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("appPaths");
    }

    @Test
    public void withGAV() {
        assertThat(builder.build().getGAV()).isEmpty();
        assertThatThrownBy(() -> builder.withGAV(null)).isInstanceOf(NullPointerException.class).hasMessageContaining("gav");
        assertThat(builder.withGAV(KogitoGAV.EMPTY_GAV).build().getGAV()).hasValue(KogitoGAV.EMPTY_GAV);
    }

    static class MockKogitoBuildContext extends AbstractKogitoBuildContext {

        public static Builder builder() {
            return new MockKogiotBuildContextBuilder();
        }

        protected MockKogitoBuildContext(MockKogiotBuildContextBuilder builder) {
            super(builder, null, null, "Mock");
        }

        public static class MockKogiotBuildContextBuilder extends AbstractKogitoBuildContext.AbstractBuilder {

            protected MockKogiotBuildContextBuilder() {
            }

            @Override
            public KogitoBuildContext build() {
                return new MockKogitoBuildContext(this);
            }
        }
    }

}
