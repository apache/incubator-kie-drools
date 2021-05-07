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
package org.kie.kogito.codegen.api.utils;

import java.util.function.Predicate;

import org.kie.kogito.codegen.api.AddonsConfig;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that performs automatic addons discovery
 */
public class AddonsConfigDiscovery {

    public static final String PERSISTENCE_FACTORY_CLASS = "org.kie.kogito.persistence.KogitoProcessInstancesFactory";
    public static final String PROMETHEUS_CLASS = "org.kie.kogito.monitoring.prometheus.common.rest.MetricsResource";
    public static final String MONITORING_CORE_CLASS = "org.kie.kogito.monitoring.core.common.MonitoringRegistry";
    public static final String TRACING_CLASS = "org.kie.kogito.tracing.decision.DecisionTracingListener";
    public static final String QUARKUS_CLOUD_EVENTS = "org.kie.kogito.addon.cloudevents.quarkus.QuarkusCloudEventEmitter";
    public static final String SPRING_CLOUD_EVENTS = "org.kie.kogito.addon.cloudevents.spring.SpringKafkaCloudEventEmitter";
    public static final String QUARKUS_EXPLAINABILITY = "org.kie.kogito.explainability.QuarkusExplainableResource";
    public static final String SPRING_EXPLAINABILITY = "org.kie.kogito.explainability.SpringBootExplainableResource";
    public static final String QUARKUS_PROCESS_SVG = "org.kie.kogito.svg.service.QuarkusProcessSvgService";
    public static final String SPRING_PROCESS_SVG = "org.kie.kogito.svg.service.SpringBootProcessSvgService";
    private static final Logger LOGGER = LoggerFactory.getLogger(AddonsConfigDiscovery.class);

    private AddonsConfigDiscovery() {
        // utility class
    }

    public static AddonsConfig discover(KogitoBuildContext context) {
        return discover(context::hasClassAvailable);
    }

    public static AddonsConfig discover(Predicate<String> classAvailabilityResolver) {
        boolean usePersistence = classAvailabilityResolver.test(PERSISTENCE_FACTORY_CLASS);
        boolean usePrometheusMonitoring = classAvailabilityResolver.test(PROMETHEUS_CLASS);
        boolean useMonitoring = usePrometheusMonitoring || classAvailabilityResolver.test(MONITORING_CORE_CLASS);
        boolean useTracing = classAvailabilityResolver.test(TRACING_CLASS);
        boolean useCloudEvents = classAvailabilityResolver.test(QUARKUS_CLOUD_EVENTS) || classAvailabilityResolver.test(SPRING_CLOUD_EVENTS);
        boolean useExplainability = classAvailabilityResolver.test(QUARKUS_EXPLAINABILITY) || classAvailabilityResolver.test(SPRING_EXPLAINABILITY);
        boolean useProcessSVG = classAvailabilityResolver.test(QUARKUS_PROCESS_SVG) || classAvailabilityResolver.test(SPRING_PROCESS_SVG);

        AddonsConfig addonsConfig = AddonsConfig.builder()
                .withPersistence(usePersistence)
                .withMonitoring(useMonitoring)
                .withPrometheusMonitoring(usePrometheusMonitoring)
                .withTracing(useTracing)
                .withCloudEvents(useCloudEvents)
                .withExplainability(useExplainability)
                .withProcessSVG(useProcessSVG)
                .build();

        LOGGER.info("Performed addonsConfig discovery, found: {}", addonsConfig);

        return addonsConfig;
    }
}
