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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;
import org.kie.kogito.event.TopicType;

public class TopicsInformationResourceGenerator extends AbstractEventResourceGenerator {

    private static final String RESOURCE_TEMPLATE = "/class-templates/events/TopicsInformationResourceTemplate.java";
    private static final String CLASS_NAME = "TopicsInformationResource";

    private final List<TriggerMetaData> triggers;

    public TopicsInformationResourceGenerator(final List<ProcessExecutableModelGenerator> generators) {
        this.triggers = this.filterTriggers(generators);
    }

    protected String getResourceTemplate() {
        return RESOURCE_TEMPLATE;
    }

    @Override
    protected String getClassName() {
        return CLASS_NAME;
    }

    List<TriggerMetaData> getTriggers() {
        return triggers;
    }

    public String generate() {
        final CompilationUnit clazz = this.parseTemplate();
        final ClassOrInterfaceDeclaration template = clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
        template.setName(CLASS_NAME);
        this.addTopics(template);

        template.getMembers().sort(new BodyDeclarationComparator());

        return clazz.toString();
    }

    private void addTopics(final ClassOrInterfaceDeclaration template) {
        final BlockStmt constructorBlock = template.getDefaultConstructor().orElseThrow(() -> new IllegalArgumentException("No body found in setup method!")).getBody();
        final List<String> repeatLines = extractRepeatLinesFromMethod(constructorBlock);
        this.triggers.forEach(t -> {
            String topicType = TopicType.class.getName() + "." + TopicType.CONSUMED.name();
            if (TriggerMetaData.TriggerType.ProduceMessage.equals(t.getType())) {
                topicType = TopicType.class.getName() + "." + TopicType.PRODUCED.name();
            }
            for (String l : repeatLines) {
                constructorBlock.addStatement(l.replace("$name$", t.getName()).replace("$type$", topicType));
            }
        });
    }

    private List<TriggerMetaData> filterTriggers(final List<ProcessExecutableModelGenerator> generators) {
        if (generators != null) {
            final List<TriggerMetaData> filteredTriggers = new ArrayList<>();
            generators
                    .stream()
                    .filter(m -> m.generate().getTriggers() != null)
                    .forEach(m -> filteredTriggers.addAll(m.generate().getTriggers().stream()
                                                                  .filter(t -> !TriggerMetaData.TriggerType.Signal.equals(t.getType()))
                                                                  .collect(Collectors.toList())));
            return filteredTriggers;
        }
        return Collections.emptyList();
    }
}
