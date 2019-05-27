/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen;

import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import org.junit.Test;
import org.kie.kogito.StaticConfig;
import org.kie.kogito.codegen.process.config.ProcessConfigGenerator;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigGeneratorTest {

    @Test
    public void withProcessConfig() {
        final ConfigGenerator generator = new ConfigGenerator();
        final ProcessConfigGenerator processConfigGenerator = Mockito.mock(ProcessConfigGenerator.class);
        final ConfigGenerator returnedConfigGenerator = generator.withProcessConfig(processConfigGenerator);
        assertThat(returnedConfigGenerator).isNotNull();
        assertThat(returnedConfigGenerator).isSameAs(generator);
    }

    @Test
    public void withProcessConfigNull() {
        final ConfigGenerator generator = new ConfigGenerator();
        final ConfigGenerator returnedConfigGenerator = generator.withProcessConfig(null);
        assertThat(returnedConfigGenerator).isNotNull();
        assertThat(returnedConfigGenerator).isSameAs(generator);
    }

    @Test
    public void newInstanceNoProcessConfig() {
        newInstanceTest(null, NullLiteralExpr.class);
    }

    @Test
    public void newInstanceWithProcessConfig() {
        final ProcessConfigGenerator processConfigGenerator = Mockito.mock(ProcessConfigGenerator.class);
        Mockito.when(processConfigGenerator.newInstance()).thenReturn(new ObjectCreationExpr());
        newInstanceTest(processConfigGenerator, ObjectCreationExpr.class);
    }

    private void newInstanceTest(final ProcessConfigGenerator processConfigGenerator, final Class expectedArgumentType) {
        ObjectCreationExpr expression = new ConfigGenerator().withProcessConfig(processConfigGenerator).newInstance();
        assertThat(expression).isNotNull();

        assertThat(expression.getType()).isNotNull();
        assertThat(expression.getType().asString()).isEqualTo(StaticConfig.class.getCanonicalName());

        assertThat(expression.getArguments()).isNotNull();
        assertThat(expression.getArguments()).hasSize(2);
        assertThat(expression.getArgument(0)).isInstanceOf(expectedArgumentType);
    }
}