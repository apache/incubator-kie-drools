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
package org.kie.kogito.addon.cloudevents.quarkus.deployment;

import java.util.Objects;
import java.util.Optional;

import org.drools.util.StringUtils;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.eclipse.microprofile.reactive.messaging.OnOverflow.Strategy;
import org.kie.kogito.addon.quarkus.messaging.common.ChannelFormat;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseType;
import static org.kie.kogito.codegen.core.CodegenUtils.interpolateTypes;

public abstract class EventGenerator implements ClassGenerator {

    private final TemplatedGenerator template;
    private final CompilationUnit generator;
    private final Optional<String> annotationName;
    private Optional<String> fullAnnotationName;
    private final ChannelInfo channelInfo;
    private final String packageName;
    private final String className;
    private final ClassOrInterfaceDeclaration clazz;
    private final KogitoBuildContext context;

    public EventGenerator(KogitoBuildContext context, ChannelInfo channelInfo, String templateName) {
        className = StringUtils.ucFirst(polishClassName(channelInfo, templateName));
        this.context = context;
        this.packageName = context.getPackageName();
        this.channelInfo = channelInfo;
        template = TemplatedGenerator.builder()
                .withTargetTypeName(className)
                .build(context, templateName);
        generator = template.compilationUnitOrThrow("Cannot generate " + templateName);
        clazz = generator.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new InvalidTemplateException(template, "Cannot find class declaration"));
        clazz.setName(className);
        String annotationName = null;
        if (!channelInfo.isDefault()) {
            annotationName = StringUtils.ucFirst(polishClassName(channelInfo, "Qualifier"));
        }
        this.annotationName = Optional.ofNullable(annotationName);
        this.fullAnnotationName = this.annotationName.map(a -> packageName + '.' + a);
        clazz.findAll(StringLiteralExpr.class)
                .forEach(str -> str.setString(str.asString().replace("$Trigger$", channelInfo.getChannelName())));
        clazz.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, channelInfo.getClassName()));
        channelInfo.getOnOverflow().ifPresent(this::onOverflowAnnotation);
    }

    private void onOverflowAnnotation(OnOverflowInfo info) {
        clazz.getFieldByName("emitter").ifPresent(f -> {
            NormalAnnotationExpr annotation =
                    f.addAndGetAnnotation(OnOverflow.class).addPair("value", new FieldAccessExpr(new NameExpr(new SimpleName(Strategy.class.getCanonicalName())), info.getStrategy().name()));
            info.getBufferSize().ifPresent(bufferSize -> annotation.addPair("bufferSize", new LongLiteralExpr(bufferSize)));
        });
    }

    protected FieldDeclaration generateMarshallerField(String fieldName, String setMethodName, Class<?> fieldClass) {
        FieldDeclaration field =
                clazz.addField(parseClassOrInterfaceType(fieldClass.getCanonicalName()).setTypeArguments(NodeList.nodeList(parseType(channelInfo.getClassName()))), fieldName);
        clazz.findFirst(MethodDeclaration.class, m -> m.getNameAsString().equals("init")).flatMap(MethodDeclaration::getBody).ifPresent(body -> body.addStatement(new MethodCallExpr(
                new SuperExpr(), setMethodName).addArgument(new FieldAccessExpr(new ThisExpr(), fieldName))));
        channelInfo.getMarshaller().ifPresentOrElse(marshallerName -> setMarshaller(marshallerName, field), () -> context.getDependencyInjectionAnnotator().withInjection(field));
        return field;
    }

    private void setMarshaller(String marshallerName, FieldDeclaration field) {
        context.getDependencyInjectionAnnotator().withNamedInjection(field, marshallerName);
        field.addAnnotation(ChannelFormat.class);
    }

    @Override
    public String getCode() {
        return generator.toString();
    }

    @Override
    public String getPath() {
        return template.generatedFilePath();
    }

    public Optional<String> getFullAnnotationName() {
        return fullAnnotationName;
    }

    public Optional<String> getAnnotationName() {
        return annotationName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    private static String polishClassName(ChannelInfo channelInfo, String suffix) {
        return channelInfo.getChannelName().replaceAll("[\\s-]", "") + suffix;
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelInfo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof EventGenerator))
            return false;
        EventGenerator other = (EventGenerator) obj;
        return Objects.equals(channelInfo, other.channelInfo);
    }
}
