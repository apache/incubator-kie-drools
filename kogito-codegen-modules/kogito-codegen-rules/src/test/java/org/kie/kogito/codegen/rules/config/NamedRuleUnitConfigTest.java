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
package org.kie.kogito.codegen.rules.config;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.drools.ruleunits.api.conf.ClockType;
import org.drools.ruleunits.api.conf.EventProcessingType;
import org.junit.jupiter.api.Test;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;
import org.kie.kogito.codegen.api.context.impl.JavaKogitoBuildContext;
import org.kie.kogito.rules.RuleUnitConfig;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

public class NamedRuleUnitConfigTest {

    @Test
    public void singleUnit() {
        Properties properties = new Properties();
        properties.put("kogito.rules.\"my.rule.Unit\".event-processing-type", EventProcessingType.CLOUD.name());
        properties.put("kogito.rules.\"my.rule.Unit\".clock-type", ClockType.REALTIME.name());
        properties.put("kogito.rules.\"my.rule.Unit\".sessions-pool", "10");

        KogitoBuildContext context = JavaKogitoBuildContext.builder()
                .withApplicationProperties(properties)
                .build();

        List<NamedRuleUnitConfig> namedRuleUnitConfigs = NamedRuleUnitConfig.fromContext(context);
        assertThat(namedRuleUnitConfigs).hasSize(1);
        NamedRuleUnitConfig namedRuleUnitConfig = namedRuleUnitConfigs.get(0);
        assertThat(namedRuleUnitConfig.getCanonicalName()).isEqualTo("my.rule.Unit");
        assertThat(namedRuleUnitConfig.getConfig().getDefaultedEventProcessingType()).isEqualTo(EventProcessingType.CLOUD);
        assertThat(namedRuleUnitConfig.getConfig().getDefaultedClockType()).isEqualTo(ClockType.REALTIME);
        assertThat(namedRuleUnitConfig.getConfig().getSessionPool().getAsInt()).isEqualTo(10);
    }

    @Test
    public void multiUnit() {
        Properties properties = new Properties();
        properties.put("kogito.rules.some.other.config", "ignore me");

        properties.put("kogito.rules.\"my.rule.Unit\".event-processing-type", EventProcessingType.CLOUD.name());
        properties.put("kogito.rules.\"my.rule.Unit\".clock-type", ClockType.PSEUDO.name());
        properties.put("kogito.rules.\"my.rule.Unit\".sessions-pool", "10");

        properties.put("kogito.rules.\"my.rule.Unit2\".event-processing-type", EventProcessingType.STREAM.name());

        KogitoBuildContext context = JavaKogitoBuildContext.builder()
                .withApplicationProperties(properties)
                .build();

        List<NamedRuleUnitConfig> namedRuleUnitConfigs = NamedRuleUnitConfig.fromContext(context);

        assertThat(namedRuleUnitConfigs).hasSize(2);

        Map<String, RuleUnitConfig> map =
                namedRuleUnitConfigs.stream()
                        .collect(toMap(NamedRuleUnitConfig::getCanonicalName, NamedRuleUnitConfig::getConfig));

        RuleUnitConfig myRuleUnitConfig = map.get("my.rule.Unit");
        assertThat(myRuleUnitConfig).isNotNull();
        assertThat(myRuleUnitConfig.getDefaultedEventProcessingType()).isEqualTo(EventProcessingType.CLOUD);
        assertThat(myRuleUnitConfig.getDefaultedClockType()).isEqualTo(ClockType.PSEUDO);
        assertThat(myRuleUnitConfig.getSessionPool().getAsInt()).isEqualTo(10);

        RuleUnitConfig myRuleUnit2Config = map.get("my.rule.Unit2");
        assertThat(myRuleUnit2Config).isNotNull();

        assertThat(myRuleUnit2Config.getDefaultedEventProcessingType()).isEqualTo(EventProcessingType.STREAM);
        assertThat(myRuleUnit2Config.getDefaultedClockType()).isEqualTo(ClockType.REALTIME);
        assertThat(myRuleUnit2Config.getSessionPool()).isEmpty();
    }

    @Test
    public void unbalancedParentheses() {
        Properties properties = new Properties();
        properties.put("kogito.rules.some.other.config", "ignore me");

        properties.put("kogito.rules.\"my.rule.Unit", EventProcessingType.CLOUD.name());

        KogitoBuildContext context = JavaKogitoBuildContext.builder()
                .withApplicationProperties(properties)
                .build();
        List<NamedRuleUnitConfig> namedRuleUnitConfigs = NamedRuleUnitConfig.fromContext(context);

        assertThat(namedRuleUnitConfigs).isEmpty();
    }
}
