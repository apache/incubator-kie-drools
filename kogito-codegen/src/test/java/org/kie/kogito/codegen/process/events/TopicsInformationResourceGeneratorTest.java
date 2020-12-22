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

import java.util.List;
import java.util.Map;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.context.JavaKogitoBuildContext;
import org.kie.kogito.codegen.context.KogitoBuildContext;
import org.kie.kogito.codegen.context.QuarkusKogitoBuildContext;
import org.kie.kogito.codegen.process.ProcessGenerationUtils;
import org.kie.kogito.event.EventKind;

import static org.assertj.core.api.Assertions.assertThat;

class TopicsInformationResourceGeneratorTest {

    @Test
    void verifyProcessWithMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messageevent/IntermediateCatchEventMessage.bpmn2", 1, true);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getDefaultConstructor()).isPresent();
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatements()).hasSize(2);
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatement(1).toExpressionStmt().get().getExpression().toString()).contains("customer");
    }

    @Test
    void verifyProcessWithMessageEventNoInjection() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messageevent/IntermediateCatchEventMessage.bpmn2", 1, false);

        assertThat(clazz).isNotNull();
        assertThat(clazz.findFirst(MethodDeclaration.class,
                                   md -> md.getName().toString().equals("getTopics"))
                           .get().getBody().get()
                           .findFirst(BlockStmt.class,
                                      b -> b.getStatements().get(0).asExpressionStmt().getExpression().toString().contains("NoOpTopicDiscovery")))
                .isPresent();
    }

    @Test
    void verifyProcessWithStartAndEndMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messagestartevent/MessageStartAndEndEvent.bpmn2", 2, true);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getDefaultConstructor()).isPresent();
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatements()).hasSize(3);
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatement(1).toExpressionStmt().get().getExpression().toString())
                .contains("customers").contains(EventKind.CONSUMED.name());
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatement(2).toExpressionStmt().get().getExpression().toString())
                .contains("processedcustomers").contains(EventKind.PRODUCED.name());
    }

    @Test
    void verifyProcessWithIntermediateThrowEventMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messageevent/IntermediateThrowEventMessage.bpmn2", 1, true);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getDefaultConstructor()).isPresent();
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatements()).hasSize(2);
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatement(1).toExpressionStmt().get().getExpression().toString())
                .contains("customers").contains(EventKind.PRODUCED.name());
    }

    @Test
    void verifyProcessWithBoundaryEventMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messageevent/BoundaryMessageEventOnTask.bpmn2", 1, true);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getDefaultConstructor()).isPresent();
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatements()).hasSize(2);
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatement(1).toExpressionStmt().get().getExpression().toString())
                .contains("customers").contains(EventKind.CONSUMED.name());
    }

    @Test
    void verifyProcessWithoutMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/usertask/approval.bpmn2", 0, true);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getDefaultConstructor()).isPresent();
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatements()).hasSize(1);
    }

    private ClassOrInterfaceDeclaration generateAndParseClass(String bpmnFile, int expectedTriggers, boolean withInjection) {
        KogitoBuildContext context = (withInjection ?
                QuarkusKogitoBuildContext.builder() :
                JavaKogitoBuildContext.builder())
                .withAddonsConfig(AddonsConfig.builder().withCloudEvents(true).build())
                .build();

        final TopicsInformationResourceGenerator generator =
                new TopicsInformationResourceGenerator(
                        context,
                        ProcessGenerationUtils.execModelFromProcessFile(bpmnFile));
        if (expectedTriggers > 0) {
            assertThat(generator.getTriggers()).isNotEmpty();
            int triggersCount = 0;
            for (Map.Entry<String, List<TriggerMetaData>> entry : generator.getTriggers().entrySet()) {
                triggersCount += entry.getValue().size();
            }
            assertThat(triggersCount).isEqualTo(expectedTriggers);
        } else {
            assertThat(generator.getTriggers()).isEmpty();
        }
        final String source = generator.generate();
        assertThat(source).isNotNull();
        final ClassOrInterfaceDeclaration clazz = StaticJavaParser
                .parse(source)
                .getClassByName(generator.getClassName())
                .orElseThrow(() -> new IllegalArgumentException("Class does not exists"));
        return clazz;
    }
}