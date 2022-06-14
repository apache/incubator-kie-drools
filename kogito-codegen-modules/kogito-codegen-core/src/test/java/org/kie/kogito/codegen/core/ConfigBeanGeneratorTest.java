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
package org.kie.kogito.codegen.core;

import java.util.Optional;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.KogitoGAV;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.QuarkusKogitoBuildContext;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigBeanGeneratorTest {

    @ParameterizedTest
    @MethodSource("org.kie.kogito.codegen.api.utils.KogitoContextTestUtils#contextBuilders")
    public void generate(KogitoBuildContext.Builder contextBuilder) {
        if (!QuarkusKogitoBuildContext.CONTEXT_NAME.equals(contextBuilder.build().name())) {
            // null GAV
            assertThat(contextBuilder.build().getGAV()).isEmpty();
            Optional<MethodCallExpr> setGavNull = getGavMethodCallExpr(contextBuilder);
            setGavNull.ifPresent(methodCallExpr -> assertThat(methodCallExpr.toString()).contains("null"));

            // with GAV
            KogitoGAV kogitoGAV = new KogitoGAV("groupId", "artifactId", "version");
            contextBuilder.withGAV(kogitoGAV);
            Optional<MethodCallExpr> setGav = getGavMethodCallExpr(contextBuilder);
            setGav.ifPresent(methodCallExpr -> assertThat(methodCallExpr.toString())
                    .contains(kogitoGAV.getGroupId())
                    .contains(kogitoGAV.getArtifactId())
                    .contains(kogitoGAV.getVersion()));
        }
    }

    private Optional<MethodCallExpr> getGavMethodCallExpr(KogitoBuildContext.Builder contextBuilder) {
        ConfigBeanGenerator configBeanGenerator = new ConfigBeanGenerator(contextBuilder.build());
        CompilationUnit compilationUnit = configBeanGenerator.toCompilationUnit();
        assertThat(compilationUnit.toString()).doesNotContain(configBeanGenerator.GAV_TEMPLATE);
        Optional<MethodCallExpr> setGav = compilationUnit.findFirst(MethodCallExpr.class, mc -> "setGav".equals(mc.getNameAsString()));
        boolean shouldMethodPresent = contextBuilder.build().hasDI();
        assertThat(setGav.isPresent()).isEqualTo(shouldMethodPresent);
        return setGav;
    }
}
