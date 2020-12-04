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

package org.kie.kogito.codegen.process.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.ApplicationGenerator;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.InvalidTemplateException;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;
import org.kie.kogito.event.EventKind;
import org.kie.kogito.services.event.DataEventAttrBuilder;

public class TopicsInformationResourceGenerator extends AbstractEventResourceGenerator {

    private static final String CDI_TEMPLATE = "/class-templates/events/TopicsInformationResourceTemplate.java";
    private static final String SPRING_TEMPLATE = "/class-templates/events/SpringTopicsInformationResourceTemplate.java";
    private static final String CLASS_NAME = "TopicsInformationResource";

    private final DependencyInjectionAnnotator annotator;
    private final Map<String, List<TriggerMetaData>> triggers;
    private final AddonsConfig addonsConfig;

    public TopicsInformationResourceGenerator(final List<ProcessExecutableModelGenerator> generators,
                                              final DependencyInjectionAnnotator annotator,
                                              final AddonsConfig addonsConfig) {
        super(new TemplatedGenerator(ApplicationGenerator.DEFAULT_PACKAGE_NAME, CLASS_NAME,
                                     CDI_TEMPLATE, SPRING_TEMPLATE, CDI_TEMPLATE)
                      .withDependencyInjection(annotator));
        this.triggers = this.filterTriggers(generators);
        this.annotator = annotator;
        this.addonsConfig = addonsConfig;
    }

    Map<String, List<TriggerMetaData>> getTriggers() {
        return triggers;
    }

    public String generate() {
        final CompilationUnit clazz = generator.compilationUnit()
                .orElseThrow(() -> new InvalidTemplateException(CLASS_NAME, generator.templatePath(),
                                                                "Cannot generate TopicInformation REST Resource"));
        final ClassOrInterfaceDeclaration template = clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));

        this.addEventsMeta(template);

        // in case we don't have the bean in the classpath, just ignore the injection that the generated class will use NoOp instead
        if (annotator != null && addonsConfig.useCloudEvents()) {
            annotator.withApplicationComponent(template);
            template.findAll(FieldDeclaration.class, fd -> fd.getVariables().get(0).getNameAsString().contains("discovery"))
                    .forEach(annotator::withInjection);
        } else {
            template.findFirst(MethodDeclaration.class, md -> md.getName().toString().equals("getTopics"))
                    .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain method getTopics!"))
                    .getBody().orElseThrow(() -> new NoSuchElementException("getTopics method doesn't have a body!"))
                    .addStatement(0, StaticJavaParser.parseStatement("discovery = new org.kie.kogito.services.event.impl.NoOpTopicDiscovery();"));
        }

        template.getMembers().sort(new BodyDeclarationComparator());

        return clazz.toString();
    }

    private void addEventsMeta(final ClassOrInterfaceDeclaration template) {
        final BlockStmt constructorBlock = template.getDefaultConstructor().orElseThrow(() -> new IllegalArgumentException("No body found in setup method!")).getBody();
        final List<String> repeatLines = extractRepeatLinesFromMethod(constructorBlock);
        this.triggers.forEach((processId, triggers) -> triggers.forEach(t -> {
            String eventKind = EventKind.class.getName() + "." + EventKind.CONSUMED.name();
            String eventType = t.getName();
            // we don't know the source of a consumed event, should be provided by the producer
            String eventSource = "";
            if (TriggerMetaData.TriggerType.ProduceMessage.equals(t.getType())) {
                eventType = DataEventAttrBuilder.toType(t.getName(), processId);
                eventKind = EventKind.class.getName() + "." + EventKind.PRODUCED.name();
                eventSource = DataEventAttrBuilder.toSource(processId);
            }
            for (String l : repeatLines) {
                constructorBlock.addStatement(l.replace("$type$", eventType).replace("$source$", eventSource).replace("$kind$", eventKind));
            }
        }));
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
}
