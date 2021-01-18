/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;

import java.io.File;
import java.util.Properties;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KogitoBuildContextTest {

    protected KogitoBuildContext.Builder builder;

    @BeforeEach
    public void init() {
        builder = MockKogitoBuildContext.builder();
    }

    @Test
    public void packageNameValidation() {
        assertThat(builder.build().getPackageName()).isEqualTo(KogitoBuildContext.DEFAULT_PACKAGE_NAME);
        assertThatThrownBy(() -> builder.withPackageName(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> builder.withPackageName("i.am.an-invalid.package-name.sorry"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void applicationPropertiesValidation() {
        assertThat(builder.build().getApplicationProperties()).isNotNull();
        assertThatThrownBy(() -> builder.withApplicationProperties((Properties) null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void withAddonsConfig() {
        assertThat(builder.build().getAddonsConfig()).isEqualTo(AddonsConfig.DEFAULT);
        assertThatThrownBy(() -> builder.withAddonsConfig(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void withClassAvailabilityResolver() {
        assertThatThrownBy(() -> builder.withClassAvailabilityResolver(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void withTargetDirectory() {
        File testFile = new File("");
        assertThat(builder.build().getTargetDirectory())
                .isNotNull()
                .isDirectory();
        assertThatThrownBy(() -> builder.withTargetDirectory(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> builder.withTargetDirectory(testFile))
                .isInstanceOf(IllegalArgumentException.class);
    }

    static class MockKogitoBuildContext extends AbstractKogitoBuildContext {

        public static Builder builder() {
            return new MockKogiotBuildContextBuilder();
        }

        protected MockKogitoBuildContext(String packageName, Predicate<String> classAvailabilityResolver, DependencyInjectionAnnotator dependencyInjectionAnnotator, File targetDirectory, AddonsConfig addonsConfig, Properties applicationProperties) {
            super(packageName, classAvailabilityResolver, dependencyInjectionAnnotator, targetDirectory, addonsConfig, applicationProperties, "Mock");
        }

        public static class MockKogiotBuildContextBuilder extends AbstractKogitoBuildContext.AbstractBuilder {

            protected MockKogiotBuildContextBuilder() {
            }

            @Override
            public KogitoBuildContext build() {
                return new MockKogitoBuildContext(packageName, classAvailabilityResolver, null, targetDirectory, addonsConfig, applicationProperties);
            }
        }
    }

}