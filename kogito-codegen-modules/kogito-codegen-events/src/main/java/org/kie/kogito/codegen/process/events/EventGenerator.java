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
package org.kie.kogito.codegen.process.events;

import java.util.Objects;

import org.drools.util.StringUtils;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseType;
import static org.kie.kogito.codegen.core.CodegenUtils.interpolateTypes;

public abstract class EventGenerator implements ClassGenerator {

    public static String USE_CLOUD_EVENTS = "kogito.messaging.as-cloudevents";
    private final TemplatedGenerator template;
    private final CompilationUnit generator;
    private final ChannelInfo channelInfo;
    private final String packageName;
    private final String className;
    protected final ClassOrInterfaceDeclaration clazz;
    private final KogitoBuildContext context;

    public EventGenerator(KogitoBuildContext context, ChannelInfo channelInfo, String templateName) {
        this.className = StringUtils.ucFirst(polishClassName(channelInfo, templateName));
        this.context = context;
        this.packageName = context.getPackageName();
        this.channelInfo = channelInfo;
        template = TemplatedGenerator.builder()
                .withTemplateBasePath(TemplatedGenerator.DEFAULT_TEMPLATE_BASE_PATH + "events")
                .withTargetTypeName(className)
                .build(context, templateName);
        generator = template.compilationUnitOrThrow("Cannot generate " + templateName);
        clazz = generator.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new InvalidTemplateException(template, "Cannot find class declaration"));
        clazz.setName(className);
        clazz.findAll(ClassOrInterfaceType.class).stream().filter(type -> type.getNameAsString().contains("$Type$")).forEach(type -> type.setName(channelInfo.getClassName()));
        clazz.findAll(ClassOrInterfaceType.class).stream().filter(type -> type.getNameAsString().contains("$ClassName$")).forEach(type -> type.setName(className));
        clazz.findAll(StringLiteralExpr.class).forEach(str -> str.setString(str.asString()
                .replace("$Trigger$", channelInfo.getChannelName())
                .replace("$ChannelName$", channelInfo.getChannelName())
                .replace("$Topic$", channelInfo.getTopic())));

        generator.findAll(ClassOrInterfaceType.class).forEach(cls -> interpolateTypes(cls, channelInfo.getClassName()));
    }

    protected boolean isCloudEvent() {
        return context.getApplicationProperty(USE_CLOUD_EVENTS, Boolean.class).orElse(true);
    }

    protected ClassOrInterfaceDeclaration getClassOrInterfaceDeclaration() {
        return clazz;
    }

    protected FieldDeclaration generateMarshallerField(String fieldName, Class<?> fieldClass) {
        FieldDeclaration field = clazz.addField(parseClassOrInterfaceType(fieldClass.getCanonicalName()).setTypeArguments(NodeList.nodeList(parseType(channelInfo.getClassName()))), fieldName);
        channelInfo.getMarshaller().ifPresentOrElse(
                marshallerName -> setMarshaller(marshallerName, field),
                () -> {
                    context.getDependencyInjectionAnnotator().withInjection(field);
                    field.addAnnotation("KogitoMessaging");
                });
        return field;
    }

    private void setMarshaller(String marshallerName, FieldDeclaration field) {
        context.getDependencyInjectionAnnotator().withNamedInjection(field, marshallerName);
    }

    @Override
    public String getCode() {
        return generator.toString();
    }

    @Override
    public String getPath() {
        return template.generatedFilePath();
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
