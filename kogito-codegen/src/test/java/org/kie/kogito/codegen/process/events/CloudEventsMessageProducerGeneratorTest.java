package org.kie.kogito.codegen.process.events;

import org.assertj.core.api.Assertions;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.AddonsConfig;
import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.kogito.codegen.process.MessageDataEventGenerator;
import org.kie.kogito.codegen.process.MessageProducerGenerator;
import org.kie.kogito.codegen.process.ProcessExecutableModelGenerator;
import org.kie.kogito.codegen.process.ProcessGenerationUtils;

import java.util.List;

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