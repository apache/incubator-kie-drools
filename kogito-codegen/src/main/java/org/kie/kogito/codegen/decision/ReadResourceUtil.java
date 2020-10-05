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

package org.kie.kogito.codegen.decision;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.core.util.IoUtils;
import org.kie.kogito.codegen.io.CollectedResource;

class ReadResourceUtil {

    private ReadResourceUtil() {
        //Utility class cannot be instantiated.
    }

    public static MethodCallExpr getReadResourceMethod(ClassOrInterfaceType applicationClass, CollectedResource resource) {
        if (resource.basePath().toString().endsWith(".jar")) {
            return new MethodCallExpr(
                    new MethodCallExpr(new NameExpr(IoUtils.class.getCanonicalName() + ".class"), "getClassLoader"),
                    "getResourceAsStream").addArgument(new StringLiteralExpr(getDecisionModelJarResourcePath(resource)));
        }

        return new MethodCallExpr(new FieldAccessExpr(applicationClass.getNameAsExpression(), "class"), "getResourceAsStream")
                .addArgument(new StringLiteralExpr(getDecisionModelRelativeResourcePath(resource)));
    }

    private static String getDecisionModelJarResourcePath(CollectedResource resource) {
        return resource.resource().getSourcePath();
    }

    private static String getDecisionModelRelativeResourcePath(CollectedResource resource) {
        String source = getDecisionModelJarResourcePath(resource);
        try {
            Path sourcePath = Paths.get(source).toAbsolutePath().toRealPath();
            Path relativizedPath = resource.basePath().toAbsolutePath().toRealPath().relativize(sourcePath);
            return "/" + relativizedPath.toString().replace(File.separatorChar, '/');
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}