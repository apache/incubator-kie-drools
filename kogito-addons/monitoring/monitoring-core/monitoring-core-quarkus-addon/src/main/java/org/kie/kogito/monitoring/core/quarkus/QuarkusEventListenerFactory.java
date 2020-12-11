/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.monitoring.core.quarkus;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.ws.rs.ext.Provider;

import io.quarkus.arc.properties.IfBuildProperty;
import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.kie.kogito.monitoring.core.common.Constants;
import org.kie.kogito.monitoring.core.common.process.MonitoringProcessEventListenerConfig;
import org.kie.kogito.monitoring.core.common.rule.RuleMetricsListenerConfig;
import org.kie.kogito.monitoring.core.common.system.interceptor.MetricsInterceptor;
import org.kie.kogito.process.impl.DefaultProcessEventListenerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Dependent
public class QuarkusEventListenerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusEventListenerFactory.class);

    @Produces
    @IfBuildProperty(name = Constants.MONITORING_RULE_USE_DEFAULT, stringValue = "true", enableIfMissing = true)
    public DefaultRuleEventListenerConfig produceRuleListener() {
        LOGGER.debug("Producing default listener for rule monitoring.");
        return new RuleMetricsListenerConfig();
    }

    @Produces
    @IfBuildProperty(name = Constants.MONITORING_PROCESS_USE_DEFAULT, stringValue = "true", enableIfMissing = true)
    public DefaultProcessEventListenerConfig produceProcessListener() {
        LOGGER.debug("Producing default listener for process monitoring.");
        return new MonitoringProcessEventListenerConfig();
    }
}
