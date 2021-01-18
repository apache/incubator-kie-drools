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
import com.github.javaparser.ast.body.FieldDeclaration;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.kie.kogito.codegen.BodyDeclarationComparator;
import org.kie.kogito.codegen.TemplatedGenerator;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;

public class CloudEventsResourceGenerator extends AbstractEventResourceGenerator {

    static final String EMITTER_PREFIX = "emitter_";
    private static final String CLASS_NAME = "CloudEventListenerResource";

    private final KogitoBuildContext context;
    private final List<TriggerMetaData> triggers;

    public CloudEventsResourceGenerator(final KogitoBuildContext context,
                                        final List<ProcessExecutableModelGenerator> generators) {
        super(TemplatedGenerator.builder()
                .withTemplateBasePath(TEMPLATE_EVENT_FOLDER)
                .build(context, CLASS_NAME));
        this.context = context;
        this.triggers = this.filterTriggers(generators);
    }

    /**
     * Triggers used to generate the channels
     *
     * @return
     */
    List<TriggerMetaData> getTriggers() {
        return triggers;
    }

    /**
     * Generates the source code for a CloudEventListenerResource
     *
     * @return
     */
    public String generate() {
        final CompilationUnit clazz = generator.compilationUnitOrThrow("Cannot generate CloudEvents REST Resource");
        final ClassOrInterfaceDeclaration template = clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface declaration!"));
        this.addInjection(template);
        template.getMembers().sort(new BodyDeclarationComparator());
        return clazz.toString();
    }

    /**
     * Filter TriggerMetadata to keep only  {@link org.jbpm.compiler.canonical.TriggerMetaData.TriggerType#ConsumeMessage}
     *
     * @param generators Process generators
     * @return filtered list
     */
    private List<TriggerMetaData> filterTriggers(final List<ProcessExecutableModelGenerator> generators) {
        if (generators != null) {
            final List<TriggerMetaData> filteredTriggers = new ArrayList<>();
            generators
                    .stream()
                    .filter(m -> m.generate().getTriggers() != null)
                    .forEach(m -> filteredTriggers.addAll(m.generate().getTriggers().stream()
                                                                  .filter(t -> TriggerMetaData.TriggerType.ConsumeMessage.equals(t.getType()))
                                                                  .collect(Collectors.toList())));
            return filteredTriggers;
        }
        return Collections.emptyList();
    }

    private void addInjection(final ClassOrInterfaceDeclaration template) {
        if(context.hasDI()) {
            context.getDependencyInjectionAnnotator().withApplicationComponent(template);
            template.findAll(FieldDeclaration.class, fd -> fd.getVariables().get(0).getNameAsString().contains(EMITTER_PREFIX))
                    .forEach(context.getDependencyInjectionAnnotator()::withInjection);
        }
    }

    String sanitizeEmitterName(String triggerName) {
        return String.join("", EMITTER_PREFIX, triggerName.replaceAll("[^a-zA-Z0-9]+", ""));
    }
}