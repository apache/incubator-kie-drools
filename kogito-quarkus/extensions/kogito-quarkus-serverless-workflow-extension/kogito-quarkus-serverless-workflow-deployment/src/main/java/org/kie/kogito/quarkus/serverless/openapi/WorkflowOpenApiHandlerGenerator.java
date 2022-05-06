/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.quarkus.serverless.openapi;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;
import org.jboss.jandex.Type.Kind;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.process.impl.CachedWorkItemHandlerConfig;
import org.kie.kogito.serverless.workflow.openapi.OpenApiWorkItemHandler;
import org.kie.kogito.serverless.workflow.utils.OpenAPIOperationId;

import com.github.javaparser.StaticJavaParser;
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
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import io.quarkiverse.openapi.generator.annotations.GeneratedClass;
import io.quarkiverse.openapi.generator.annotations.GeneratedMethod;
import io.quarkiverse.openapi.generator.annotations.GeneratedParam;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class WorkflowOpenApiHandlerGenerator implements Runnable {

    private static final DotName generatedClass = DotName.createSimple(GeneratedClass.class.getCanonicalName());
    private static final DotName generatedMethod = DotName.createSimple(GeneratedMethod.class.getCanonicalName());
    private static final DotName generatedParam = DotName.createSimple(GeneratedParam.class.getCanonicalName());

    private static final String WORK_ITEM_PARAMETERS = "parameters";
    private static final String OPEN_API_REF = "openApiRef";

    private final IndexView index;
    private final KogitoBuildContext context;
    private final Collection<GeneratedFile> files = new ArrayList<>();

    public static Collection<GeneratedFile> generateHandlerClasses(KogitoBuildContext context, IndexView index) {
        WorkflowOpenApiHandlerGenerator runnable = new WorkflowOpenApiHandlerGenerator(context, index);
        runnable.run();
        return runnable.files;
    }

    private WorkflowOpenApiHandlerGenerator(KogitoBuildContext context, IndexView index) {
        this.index = index;
        this.context = context;
    }

    @Override
    public void run() {
        index.getAnnotations(generatedClass).forEach(this::generateHandler);
        if (!context.getGeneratedHandlers().isEmpty()) {
            generateWorkItemHandlerConfig();
        }
    }

    private void generateHandler(AnnotationInstance a) {
        final String fileName = a.value().asString();
        final ClassInfo classInfo = a.target().asClass();
        classInfo.methods().stream().filter(m -> m.hasAnnotation(generatedMethod)).map(m -> generateHandler(classInfo, fileName, m)).forEach(files::add);
    }

    private GeneratedFile generateHandler(ClassInfo classInfo, String fileName, MethodInfo m) {
        final String packageName = context.getPackageName();
        final String methodName = m.annotation(generatedMethod).value().asString();
        final String className = OpenAPIOperationId.getClassName(fileName, methodName);
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
        if (m.returnType().kind() == Kind.VOID) {
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
            Type param = m.parameters().get(i);
            if (annotation != null) {
                methodCallExpr.addArgument(new CastExpr(fromClass(param), new MethodCallExpr(parameters, "remove").addArgument(new StringLiteralExpr(annotation.value().asString()))));
            } else {
                methodCallExpr.addArgument(new MethodCallExpr(new SuperExpr(), "buildBody").addArgument(parameters).addArgument(new ClassExpr(fromClass(param))));
            }
        }
        clazz.addMethod("getRestClass", Keyword.PROTECTED).setType(parseClassOrInterfaceType(Class.class.getCanonicalName()).setTypeArguments(classNameType))
                .setBody(new BlockStmt().addStatement(new ReturnStmt(new ClassExpr(classNameType))));
        context.addGeneratedHandler(className);
        return fromCompilationUnit(unit, className);
    }

    private GeneratedFile fromCompilationUnit(CompilationUnit unit, String className) {
        return new GeneratedFile(GeneratedFileType.SOURCE, Path.of("", context.getPackageName().split("\\.")).resolve(className + ".java"),
                unit.toString());
    }

    private com.github.javaparser.ast.type.Type fromClass(Type param) {
        switch (param.kind()) {
            case CLASS:
                return parseClassOrInterfaceType(param.asClassType().name().toString());
            case PRIMITIVE:
                return StaticJavaParser.parseType(param.asPrimitiveType().name().toString());
            default:
                throw new UnsupportedOperationException("Kind " + param.kind() + " is not supported");
        }

    }

    private void generateWorkItemHandlerConfig() {
        CompilationUnit unit = new CompilationUnit(context.getPackageName());
        final String className = "OpenApiWorkItemHandlerConfig";
        ClassOrInterfaceDeclaration clazz = unit.addClass(className);
        clazz.addExtendedType(CachedWorkItemHandlerConfig.class);
        clazz.addAnnotation(ApplicationScoped.class);
        BlockStmt body = clazz.addMethod("init").addAnnotation(PostConstruct.class).createBody();
        for (String refHandler : context.getGeneratedHandlers()) {
            final String fieldName = refHandler.toLowerCase();
            clazz.addField(context.getPackageName() + "." + refHandler, fieldName).addAnnotation(Inject.class);
            body.addStatement(new MethodCallExpr(new SuperExpr(), "register").addArgument(new StringLiteralExpr(refHandler))
                    .addArgument(new NameExpr(fieldName)));
        }
        files.add(fromCompilationUnit(unit, className));
    }
}
