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

import org.assertj.core.api.Assertions;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.MessageDataEventGenerator;
import org.kie.kogito.codegen.process.MessageProducerGenerator;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;
import org.kie.kogito.codegen.process.ProcessGenerationUtils;

class CloudEventsMessageProducerGeneratorTest {

    @Test
    void verifyKnativeAddonProcessing() {
        final List<ProcessExecutableModelGenerator> models =
                ProcessGenerationUtils.execModelFromProcessFile("/messageevent/IntermediateThrowEventMessage.bpmn2");
        final DependencyInjectionAnnotator annotator = new CDIDependencyInjectionAnnotator();
        Assertions.assertThat(models).isNotEmpty();
        models.forEach(m -> {
            final TriggerMetaData metaData = m.generate().getTriggers()
                    .stream()
                    .filter(t -> t.getType().equals(TriggerMetaData.TriggerType.ProduceMessage))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Process does not contains any message producers"));
            final MessageDataEventGenerator msgDataEventGenerator =
                    new MessageDataEventGenerator(m.process(), metaData).withDependencyInjection(annotator);
            final MessageProducerGenerator gen =
                    new CloudEventsMessageProducerGenerator(m.process(), "", "", msgDataEventGenerator.className(), metaData)
                            .withDependencyInjection(annotator);
            final String code = gen.generate();
            Assertions.assertThat(code).isNotBlank();
            Assertions.assertThat(code).contains("decorator.get().decorate");
            Assertions.assertThat(code).doesNotContain("$channel$");
        });

    }

}