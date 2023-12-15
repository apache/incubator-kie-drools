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
package org.kie.kogito.quarkus.serverless.workflow.openapi;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.stream.Stream;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;
import org.jboss.jandex.VoidType;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.quarkus.serverless.workflow.ClassAnnotatedWorkflowHandlerGenerator;
import org.kie.kogito.quarkus.serverless.workflow.WorkflowCodeGenUtils;
import org.kie.kogito.quarkus.serverless.workflow.WorkflowHandlerGeneratedFile;
import org.kie.kogito.serverless.workflow.openapi.OpenApiWorkItemHandler;
import org.kie.kogito.serverless.workflow.utils.OpenAPIWorkflowUtils;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import io.quarkiverse.openapi.generator.annotations.GeneratedClass;
import io.quarkiverse.openapi.generator.annotations.GeneratedMethod;
import io.quarkiverse.openapi.generator.annotations.GeneratedParam;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class WorkflowOpenApiHandlerGenerator extends ClassAnnotatedWorkflowHandlerGenerator {

    private static final DotName generatedMethod = DotName.createSimple(GeneratedMethod.class.getCanonicalName());
    private static final DotName generatedParam = DotName.createSimple(GeneratedParam.class.getCanonicalName());

    private static final String WORK_ITEM_PARAMETERS = "parameters";
    private static final String OPEN_API_REF = "openApiRef";

    public static final WorkflowOpenApiHandlerGenerator instance = new WorkflowOpenApiHandlerGenerator();

    private WorkflowOpenApiHandlerGenerator() {
    }

    @Override
    protected Stream<WorkflowHandlerGeneratedFile> generateHandler(KogitoBuildContext context, AnnotationInstance a) {
        final String fileName = a.value().asString();
        final ClassInfo classInfo = a.target().asClass();
        return classInfo.methods().stream().filter(m -> m.hasAnnotation(generatedMethod)).map(m -> generateHandler(context, classInfo, fileName, m));
    }

    private WorkflowHandlerGeneratedFile generateHandler(KogitoBuildContext context, ClassInfo classInfo, String fileName, MethodInfo m) {
        final String packageName = context.getPackageName();
        final String className = OpenAPIWorkflowUtils.getOpenApiClassName(fileName, m.name());
        final ClassOrInterfaceType classNameType = parseClassOrInterfaceType(classInfo.name().toString());
        CompilationUnit unit = new CompilationUnit(packageName);
        ClassOrInterfaceDeclaration clazz = unit.addClass(className);
        clazz.addExtendedType(parseClassOrInterfaceType(OpenApiWorkItemHandler.class.getCanonicalName()).setTypeArguments(classNameType));
        clazz.addAnnotation(ApplicationScoped.class);
        MethodDeclaration executeMethod =
                clazz.addMethod("internalExecute", Keyword.PROTECTED).addParameter(classNameType, OPEN_API_REF).addParameter(parseClassOrInterfaceType(Map.class.getCanonicalName()).setTypeArguments(
                        parseClassOrInterfaceType(String.class.getCanonicalName()), parseClassOrInterfaceType(Object.class.getCanonicalName())), WORK_ITEM_PARAMETERS).setType(Object.class);
        BlockStmt body = executeMethod.createBody();
        MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr(OPEN_API_REF), m.name());
        final NameExpr parameters = new NameExpr(WORK_ITEM_PARAMETERS);
        if (m.returnType().name().equals(DotName.createSimple(Response.class.getName())) || (m.returnType() instanceof VoidType)) {
            body.addStatement(methodCallExpr).addStatement(new ReturnStmt(new NullLiteralExpr()));
        } else {
            body.addStatement(new ReturnStmt(methodCallExpr));
        }

        // param.annotation(generatedParam) is not working
        AnnotationInstance[] annotations = new AnnotationInstance[m.parameters().size()];
        for (AnnotationInstance a : m.annotations(generatedParam)) {
            annotations[a.target().asMethodParameter().position()] = a;
        }
        for (int i = 0; i < annotations.length; i++) {
            AnnotationInstance annotation = annotations[i];
            // Using deprecated args method because it is the only way to make it work across Quarkus main and 2.7
            Type param = m.args()[i];
            if (annotation != null) {
                methodCallExpr.addArgument(new CastExpr(fromClass(param), new MethodCallExpr(parameters, "remove").addArgument(new StringLiteralExpr(annotation.value().asString()))));
            } else {
                methodCallExpr.addArgument(new MethodCallExpr("buildBody").addArgument(parameters).addArgument(new ClassExpr(fromClass(param, false))));
            }
        }
        clazz.addMethod("getRestClass", Keyword.PROTECTED).setType(parseClassOrInterfaceType(Class.class.getCanonicalName()).setTypeArguments(classNameType))
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new ClassExpr(classNameType))));

        String operationId = m.annotation(generatedMethod).value().asString();
        String workItemHandlerName = OpenAPIWorkflowUtils.getOpenApiWorkItemName(fileName, operationId);

        clazz.addMethod("getName", Keyword.PUBLIC).setType(parseClassOrInterfaceType(String.class.getCanonicalName()))
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new StringLiteralExpr(workItemHandlerName))));

        return WorkflowCodeGenUtils.fromCompilationUnit(workItemHandlerName, context, unit, className);
    }

    @Override
    protected Class<? extends Annotation> getAnnotation() {
        return GeneratedClass.class;
    }
}
