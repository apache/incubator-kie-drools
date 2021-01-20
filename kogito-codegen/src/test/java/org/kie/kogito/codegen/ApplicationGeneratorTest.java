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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationGeneratorTest {

    private static final String PACKAGE_NAME = "org.drools.test";
    private static final String EXPECTED_APPLICATION_NAME = PACKAGE_NAME + ".Application";
    private KogitoBuildContext context;

    @BeforeEach
    public void init() {
        context = JavaKogitoBuildContext.builder()
                .withPackageName(PACKAGE_NAME)
                .build();
    }

    @Test
    public void targetCanonicalName() {
        final ApplicationGenerator appGenerator = new ApplicationGenerator(context);
        assertThat(appGenerator.targetCanonicalName()).isNotNull();
        assertThat(appGenerator.targetCanonicalName()).isEqualTo(EXPECTED_APPLICATION_NAME);
    }

    @Test
    public void generatedFilePath() {
        final ApplicationGenerator appGenerator = new ApplicationGenerator(context);
        String path = appGenerator.generateApplicationDescriptor().relativePath();
        assertThat(path).isNotNull();
        assertThat(path).isEqualTo(EXPECTED_APPLICATION_NAME.replace(".", "/") + ".java");
    }

    @Test
    public void compilationUnit() {
        final ApplicationContainerGenerator appGenerator = new ApplicationContainerGenerator(context);
        assertCompilationUnit(appGenerator.getCompilationUnitOrThrow(), false);
    }

    @Test
    public void compilationUnitWithCDI() {
        context = QuarkusKogitoBuildContext.builder()
                .withPackageName(PACKAGE_NAME)
                .build();
        final ApplicationContainerGenerator appGenerator = new ApplicationContainerGenerator(context);
        assertCompilationUnit(appGenerator.getCompilationUnitOrThrow(), true);
    }

    @Test
    public void applicationSectionReplace() {
        final ApplicationContainerGenerator appGenerator = new ApplicationContainerGenerator(context);
        assertApplicationPlaceholderReplace(appGenerator, 0);

        appGenerator.withSections(Arrays.asList("Processes", "DecisionModels"));
        assertApplicationPlaceholderReplace(appGenerator, 2);
    }

    @Test
    public void registerGeneratorIfEnabled() {
        final ApplicationGenerator appGenerator = new ApplicationGenerator(context);
        final MockGenerator disabledGenerator = new MockGenerator(context, false);
        final MockGenerator enabledGenerator = new MockGenerator(context, true);
        assertThat(appGenerator.registerGeneratorIfEnabled(disabledGenerator))
                .isEmpty();
        assertThat(appGenerator.getGenerators()).isEmpty();

        assertThat(appGenerator.registerGeneratorIfEnabled(enabledGenerator))
                .isNotEmpty();
        assertThat(appGenerator.getGenerators()).hasSize(1);
    }

    private void assertCompilationUnit(final CompilationUnit compilationUnit, final boolean checkCDI) {
        assertThat(compilationUnit).isNotNull();

        assertThat(compilationUnit.getPackageDeclaration()).isPresent();
        assertThat(compilationUnit.getPackageDeclaration().get().getName().toString()).isEqualTo(PACKAGE_NAME);

        assertThat(compilationUnit.getTypes()).isNotNull();
        assertThat(compilationUnit.getTypes()).hasSize(1);

        final TypeDeclaration mainAppClass = compilationUnit.getTypes().get(0);
        assertThat(mainAppClass).isNotNull();
        assertThat(mainAppClass.getName().toString()).isEqualTo("Application");

        if (checkCDI) {
            assertThat(mainAppClass.getAnnotations()).isNotEmpty();
            assertThat(mainAppClass.getAnnotationByName("Singleton")).isPresent();
        } else {
            assertThat(mainAppClass.getAnnotationByName("Singleton")).isNotPresent();
        }

        assertThat(mainAppClass.getMembers()).isNotNull();
    }

    private void assertApplicationPlaceholderReplace(ApplicationContainerGenerator appGenerator, long expectedParams) {
        CompilationUnit compilationUnit = appGenerator.getCompilationUnitOrThrow();

        Optional<NodeList<Expression>> expressions = compilationUnit.findFirst(MethodCallExpr.class, mtd -> "loadEngines".equals(mtd.getNameAsString()))
                .map(MethodCallExpr::getArguments);
        assertThat(expressions).isPresent();

        expressions.get()
                .forEach(expression -> assertThat(expression.toString()).doesNotContain("$"));

        long numberOfNull = expressions.get().stream()
                .filter(Expression::isNullLiteralExpr)
                .count();

        assertThat(numberOfNull).isZero();

        assertThat(expressions.get().size()).isEqualTo(expectedParams);
    }

    static class MockGenerator extends AbstractGenerator {

        private boolean enabled;

        protected MockGenerator(KogitoBuildContext context, boolean enabled) {
            super(context, "mockGenerator");
            this.enabled = enabled;
        }

        @Override
        public Optional<ApplicationSection> section() {
            return Optional.empty();
        }

        @Override
        public Collection<GeneratedFile> generate() {
            return Collections.emptyList();
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }
    }
}
