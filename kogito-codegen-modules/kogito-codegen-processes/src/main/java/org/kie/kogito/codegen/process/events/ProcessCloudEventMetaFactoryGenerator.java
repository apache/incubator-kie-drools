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
package org.kie.kogito.codegen.process.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.template.InvalidTemplateException;
import org.kie.kogito.codegen.core.CodegenUtils;
import org.kie.kogito.codegen.core.events.AbstractCloudEventMetaFactoryGenerator;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.services.event.DataEventAttrBuilder;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;

public class ProcessCloudEventMetaFactoryGenerator extends AbstractCloudEventMetaFactoryGenerator {

    static final String CLASS_NAME = "ProcessCloudEventMetaFactory";

    private final Map<String, List<TriggerMetaData>> triggers;

    public ProcessCloudEventMetaFactoryGenerator(KogitoBuildContext context, List<ProcessExecutableModelGenerator> generators) {
        super(buildTemplatedGenerator(context, CLASS_NAME), context);
        this.triggers = this.filterTriggers(generators);
    }

    Map<String, List<TriggerMetaData>> getTriggers() {
        return triggers;
    }

    public String generate() {
        CompilationUnit compilationUnit = generator.compilationUnitOrThrow("Cannot generate CloudEventMetaFactory");

        ClassOrInterfaceDeclaration classDefinition = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new InvalidTemplateException(generator, "Compilation unit doesn't contain a class or interface declaration!"));

        MethodDeclaration templatedBuildMethod = classDefinition
                .findFirst(MethodDeclaration.class, x -> x.getName().toString().startsWith("buildCloudEventMeta_"))
                .orElseThrow(() -> new InvalidTemplateException(generator, "Impossible to find expected buildCloudEventMeta_ method"));

        List<MethodData> methodDataList = triggers.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream().map(trigger -> new MethodData(entry.getKey(), trigger)))
                .distinct()
                .collect(Collectors.toList());

        methodDataList.forEach(methodData -> {
            MethodDeclaration builderMethod = templatedBuildMethod.clone();

            String methodNameValue = String.format("%s_%s", methodData.eventKind.name(), toValidJavaIdentifier(methodData.triggerName));
            String builderMethodName = getBuilderMethodName(classDefinition, templatedBuildMethod.getNameAsString(), methodNameValue);
            builderMethod.setName(builderMethodName);

            Map<String, Expression> expressions = new HashMap<>();
            expressions.put("$type$", new StringLiteralExpr(methodData.eventType));
            expressions.put("$source$", new StringLiteralExpr(methodData.eventSource));
            expressions.put("$kind$", new FieldAccessExpr(new NameExpr(new SimpleName(EventKind.class.getName())), methodData.eventKind.name()));

            ObjectCreationExpr objectCreationExpr = builderMethod.findAll(ObjectCreationExpr.class).get(0);
            CodegenUtils.interpolateArguments(objectCreationExpr, expressions);

            if (context.hasDI()) {
                context.getDependencyInjectionAnnotator().withFactoryMethod(builderMethod);
            }

            classDefinition.addMember(builderMethod);
        });

        templatedBuildMethod.remove();

        if (context.hasDI()) {
            context.getDependencyInjectionAnnotator().withFactoryClass(classDefinition);
        }

        return compilationUnit.toString();
    }

    private Map<String, List<TriggerMetaData>> filterTriggers(final List<ProcessExecutableModelGenerator> generators) {
        if (generators != null) {
            final Map<String, List<TriggerMetaData>> filteredTriggers = new HashMap<>();
            generators
                    .stream()
                    .filter(m -> m.generate().getTriggers() != null && !m.generate().getTriggers().isEmpty())
                    .forEach(m -> filteredTriggers.put(m.getProcessId(),
                            m.generate().getTriggers().stream()
                                    .filter(t -> !TriggerMetaData.TriggerType.Signal.equals(t.getType()))
                                    .collect(Collectors.toList())));
            return filteredTriggers;
        }
        return Collections.emptyMap();
    }

    private static class MethodData {

        final String processId;
        final String triggerName;
        final EventKind eventKind;
        final String eventType;
        final String eventSource;

        public MethodData(String processId, TriggerMetaData trigger) {
            this.processId = processId;
            this.triggerName = trigger.getName();

            this.eventKind = TriggerMetaData.TriggerType.ProduceMessage.equals(trigger.getType())
                    ? EventKind.PRODUCED
                    : EventKind.CONSUMED;

            this.eventType = eventKind == EventKind.PRODUCED
                    ? DataEventAttrBuilder.toType(triggerName, processId)
                    : triggerName;

            this.eventSource = eventKind == EventKind.PRODUCED
                    ? DataEventAttrBuilder.toSource(processId)
                    : "";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MethodData that = (MethodData) o;
            return processId.equals(that.processId) && triggerName.equals(that.triggerName) && eventKind == that.eventKind && eventType.equals(that.eventType) && eventSource.equals(that.eventSource);
        }

        @Override
        public int hashCode() {
            return Objects.hash(processId, triggerName, eventKind, eventType, eventSource);
        }
    }
}
