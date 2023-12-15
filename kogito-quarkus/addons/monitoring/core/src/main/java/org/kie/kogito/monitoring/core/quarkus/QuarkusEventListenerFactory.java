/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.monitoring.core.quarkus;

import org.kie.kogito.KogitoGAV;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.drools.core.config.DefaultRuleEventListenerConfig;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.monitoring.core.common.Constants;
import org.kie.kogito.monitoring.core.common.process.MetricsProcessEventListener;
import org.kie.kogito.monitoring.core.common.rule.RuleMetricsListenerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Metrics;
import io.quarkus.arc.properties.IfBuildProperty;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

@Dependent
public class QuarkusEventListenerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuarkusEventListenerFactory.class);

    ConfigBean configBean;

    @Inject
    public QuarkusEventListenerFactory(ConfigBean configBean) {
        this.configBean = configBean;
    }

    @Produces
    @IfBuildProperty(name = Constants.MONITORING_RULE_USE_DEFAULT, stringValue = "true", enableIfMissing = true)
    public DefaultRuleEventListenerConfig produceRuleListener() {
        LOGGER.debug("Producing default listener for rule monitoring.");
        return new RuleMetricsListenerConfig(configBean.getGav().orElse(KogitoGAV.EMPTY_GAV), Metrics.globalRegistry);
    }

    @Produces
    @IfBuildProperty(name = Constants.MONITORING_PROCESS_USE_DEFAULT, stringValue = "true", enableIfMissing = true)
    public KogitoProcessEventListener produceProcessListener() {
        LOGGER.debug("Producing default listener for process monitoring.");
        return new MetricsProcessEventListener("default-process-monitoring-listener",
                configBean.getGav().orElse(KogitoGAV.EMPTY_GAV), Metrics.globalRegistry);
    }
}
