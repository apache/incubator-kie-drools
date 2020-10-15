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

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.process.ProcessGenerationUtils;
import org.kie.kogito.event.TopicType;

import static org.assertj.core.api.Assertions.assertThat;

class TopicsInformationResourceGeneratorTest {

    @Test
    void verifyProcessWithMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messageevent/IntermediateCatchEventMessage.bpmn2", 1);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getDefaultConstructor()).isPresent();
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatements()).hasSize(2);
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatement(1).toExpressionStmt().get().getExpression().toString()).contains("customer");
    }

    @Test
    void verifyProcessWithStartAndEndMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messagestartevent/MessageStartAndEndEvent.bpmn2", 2);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getDefaultConstructor()).isPresent();
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatements()).hasSize(3);
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatement(1).toExpressionStmt().get().getExpression().toString())
                .contains("customers").contains(TopicType.CONSUMED.name());
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatement(2).toExpressionStmt().get().getExpression().toString())
                .contains("processedcustomers").contains(TopicType.PRODUCED.name());
    }

    @Test
    void verifyProcessWithIntermediateThrowEventMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messageevent/IntermediateThrowEventMessage.bpmn2", 1);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getDefaultConstructor()).isPresent();
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatements()).hasSize(2);
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatement(1).toExpressionStmt().get().getExpression().toString())
                .contains("customers").contains(TopicType.PRODUCED.name());
    }

    @Test
    void verifyProcessWithBoundaryEventMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/messageevent/BoundaryMessageEventOnTask.bpmn2", 1);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getDefaultConstructor()).isPresent();
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatements()).hasSize(2);
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatement(1).toExpressionStmt().get().getExpression().toString())
                .contains("customers").contains(TopicType.CONSUMED.name());
    }

    @Test
    void verifyProcessWithoutMessageEvent() {
        final ClassOrInterfaceDeclaration clazz = generateAndParseClass("/usertask/approval.bpmn2", 0);

        assertThat(clazz).isNotNull();
        assertThat(clazz.getDefaultConstructor()).isPresent();
        assertThat(clazz.getDefaultConstructor().get().getBody().getStatements()).hasSize(1);
    }

    private ClassOrInterfaceDeclaration generateAndParseClass(String bpmnFile, int expectedTriggers) {
        final TopicsInformationResourceGenerator generator =
                new TopicsInformationResourceGenerator(ProcessGenerationUtils.execModelFromProcessFile(bpmnFile));
        assertThat(generator.getTriggers()).hasSize(expectedTriggers);
        final String source = generator.generate();
        assertThat(source).isNotNull();
        final ClassOrInterfaceDeclaration clazz = StaticJavaParser
                .parse(source)
                .getClassByName(generator.getClassName())
                .orElseThrow(() -> new IllegalArgumentException("Class does not exists"));
        return clazz;
    }
}