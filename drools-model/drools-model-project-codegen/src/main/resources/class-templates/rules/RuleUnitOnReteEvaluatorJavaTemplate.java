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
package $Package$;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.common.ReteEvaluator;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.ruleunits.impl.sessions.RuleUnitSession;

import org.kie.kogito.drools.core.unit.AbstractRuleUnit;
import org.kie.kogito.rules.RuleEventListenerConfig;
import org.kie.kogito.rules.RuleUnits;

public class $Name$ extends AbstractRuleUnit<$ModelName$> {

    private static final InternalKnowledgeBase kb = createKnowledgeBase();

    private final org.kie.kogito.Application app;

    public $Name$(org.kie.kogito.Application app) {
        super($ModelName$.class.getCanonicalName(), app.get(RuleUnits.class));
        this.app = app;
    }

    public $InstanceName$ internalCreateInstance($ModelName$ value) {
        return new $InstanceName$( this, value, createReteEvaluator());
    }

    private ReteEvaluator createReteEvaluator() {
        SessionConfigurationImpl sessionConfig = new SessionConfigurationImpl();
        sessionConfig.setClockType($ClockType$);

        ReteEvaluator reteEvaluator = new RuleUnitSession( kb, sessionConfig, ruleUnits );

        org.kie.kogito.Config config = app.config();
        if (config != null) {
            RuleEventListenerConfig ruleEventListenerConfig = config.get(org.kie.kogito.rules.RuleConfig.class).ruleEventListeners();
            ruleEventListenerConfig.agendaListeners().forEach(reteEvaluator.getActivationsManager().getAgendaEventSupport()::addEventListener);
            ruleEventListenerConfig.ruleRuntimeListeners().forEach(reteEvaluator.getRuleRuntimeEventSupport()::addEventListener);
        }

        return reteEvaluator;
    }

    private static InternalKnowledgeBase createKnowledgeBase() {
        RuleBaseConfiguration ruleBaseConfig = new RuleBaseConfiguration();
        ruleBaseConfig.setEventProcessingMode($EventProcessingMode$);
        ruleBaseConfig.setSessionPoolSize($SessionPoolSize$);
        InternalKnowledgeBase kb =
                org.drools.modelcompiler.builder.KieBaseBuilder.createKieBaseFromModel(new $RuleModelName$(), ruleBaseConfig);
        return kb;
    }
}
