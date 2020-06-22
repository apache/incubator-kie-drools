/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.tracing.decision;

import java.util.function.BiFunction;

import org.kie.kogito.Application;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.modelsupplier.ApplicationModelSupplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class SpringBootDecisionTracingCollector {

    private final DecisionTracingCollector collector;

    public SpringBootDecisionTracingCollector(BiFunction<String, String, org.kie.dmn.api.core.DMNModel> modelSupplier, KafkaTemplate<String, String> template, String kafkaTopicName) {
        collector = new DecisionTracingCollector((payload) -> template.send(kafkaTopicName, payload), modelSupplier);
    }

    @Autowired
    public SpringBootDecisionTracingCollector(
            Application application,
            KafkaTemplate<String, String> template,
            @Value(value = "${kogito.addon.tracing.decision.kafka.topic.name:kogito-tracing-decision}") String kafkaTopicName
    ) {
        this(new ApplicationModelSupplier(application), template, kafkaTopicName);
    }

    @Async("kogitoTracingDecisionAddonTaskExecutor")
    @EventListener
    public void onApplicationEvent(EvaluateEvent event) {
        collector.addEvent(event);
    }

}
