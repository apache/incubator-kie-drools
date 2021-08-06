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
package org.kie.kogito.codegen.process;

import java.util.Objects;

import org.drools.core.util.StringUtils;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.api.template.TemplatedGenerator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public abstract class EventGenerator {

    private TemplatedGenerator template;
    private CompilationUnit generator;
    private final String beanName;

    public EventGenerator(KogitoBuildContext context, String trigger, String templateName) {
        final String className = StringUtils.ucFirst(trigger.replaceAll("\\s", "") + templateName);
        this.beanName = "_" + trigger + '_' + templateName;
        template = TemplatedGenerator.builder()
                .withTargetTypeName(className)
                .build(context, templateName);
        generator = template.compilationUnitOrThrow("Cannot generate eventReceiver");
        ClassOrInterfaceDeclaration clazz = generator.findFirst(ClassOrInterfaceDeclaration.class).orElseThrow(() -> new InvalidTemplateException(template, "Cannot find class declaration"));
        clazz.setName(className);
        clazz.findAll(StringLiteralExpr.class)
                .forEach(str -> str.setString(str.asString().replace("$BeanName$", beanName)));
        clazz.findAll(StringLiteralExpr.class)
                .forEach(str -> str.setString(str.asString().replace("$Trigger$", trigger)));
    }

    public String generate() {
        return generator.toString();
    }

    public String generateFilePath() {
        return template.generatedFilePath();
    }

    public String getBeanName() {
        return beanName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(beanName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof EventGenerator))
            return false;
        EventGenerator other = (EventGenerator) obj;
        return Objects.equals(beanName, other.beanName);
    }

}
