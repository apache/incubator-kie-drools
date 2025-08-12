/*
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

package org.kie.kogito.codegen.process.util;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import org.kie.api.io.Resource;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.process.DummyProcess;
import org.kie.kogito.codegen.process.ProcessCodegenException;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

public class SourceFilesProviderProducerUtil {

    private SourceFilesProviderProducerUtil() {
        // utility class, shouldn't be initialized
    }

    public static void addSourceFilesToProvider(CompilationUnit compilationUnit, Map<String, KogitoWorkflowProcess> workflows, KogitoBuildContext context) {

        ClassOrInterfaceDeclaration producerClass = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new ProcessCodegenException("SourceFileProviderProducerTemplate does not contain a class declaration"));

        context.getDependencyInjectionAnnotator().withFactoryClass(producerClass);

        MethodDeclaration producerMethod = producerClass.getMethodsByName("getSourceFilesProvider")
                .iterator()
                .next();

        context.getDependencyInjectionAnnotator().withFactoryMethod(producerMethod);

        InitializerDeclaration staticInitDeclaration = producerClass.findFirst(InitializerDeclaration.class)
                .stream()
                .filter(InitializerDeclaration::isStatic)
                .findFirst()
                .orElseThrow(() -> new ProcessCodegenException("SourceFileProviderProducerTemplate does not contain a class declaration"));

        if (workflows.isEmpty() || workflows.values().stream().allMatch(DummyProcess.class::isInstance)) { // Temporary hack for incubator-kie-issues#2060
            producerClass.remove(staticInitDeclaration);
        } else {
            registerWorkflows(staticInitDeclaration, workflows, context);
        }
    }

    private static void registerWorkflows(InitializerDeclaration staticInit, Map<String, KogitoWorkflowProcess> workflows, KogitoBuildContext context) {

        BlockStmt initBody = staticInit.getBody();

        Statement statementTemplate = initBody.getStatement(0);

        initBody.remove(statementTemplate);

        workflows.forEach((id, workflowProcess) -> {
            Statement newProcessSourceStatement = statementTemplate.clone();
            String resourcePath = getResourceRelativePath(context, workflowProcess.getResource());
            newProcessSourceStatement.findAll(StringLiteralExpr.class)
                    .forEach(stringLiteralExpr -> interpolateStrings(stringLiteralExpr, id, resourcePath));
            initBody.addStatement(newProcessSourceStatement);
        });
    }

    private static void interpolateStrings(StringLiteralExpr stringLiteral, String id, String resourcePath) {
        String stringValue = stringLiteral.getValue();
        String interpolated = stringValue.replace("$processId$", id);
        interpolated = interpolated.replace("$sourcePath$", resourcePath);
        stringLiteral.setString(interpolated);
    }

    static String getResourceRelativePath(KogitoBuildContext context, Resource resource) {
        String resourcePath = resource.getSourcePath();

        Path sourceFilePath = Path.of(resourcePath);

        return Arrays.stream(context.getAppPaths().getResourcePaths())
                .filter(sourceFilePath::startsWith)
                .findFirst()
                .map(appPath -> appPath.relativize(sourceFilePath).toString())
                .orElse(resourcePath);
    }
}
