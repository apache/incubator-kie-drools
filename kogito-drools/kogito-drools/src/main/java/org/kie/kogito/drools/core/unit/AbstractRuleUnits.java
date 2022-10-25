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
package org.kie.kogito.drools.core.unit;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.impl.InternalRuleUnit;
import org.drools.ruleunits.impl.sessions.RuleUnitExecutorImpl;
import org.kie.kogito.Application;
import org.kie.kogito.rules.RuleEventListenerConfig;
import org.kie.kogito.rules.RuleUnits;

public abstract class AbstractRuleUnits implements RuleUnits {

    protected final Map<Class<? extends RuleUnitData>, RuleUnit<? extends RuleUnitData>> ruleUnitsMap = new HashMap<>();

    protected final Map<String, RuleUnitInstance<?>> unitRegistry = new HashMap<>();

    @Override
    public void register(String name, RuleUnitInstance<?> unitInstance) {
        if (name == null) {
            throw new IllegalArgumentException("Cannot register a RuleUnitInstance with a null name");
        }
        unitRegistry.put(name, unitInstance);
    }

    @Override
    public RuleUnitInstance<?> getRegisteredInstance(String name) {
        return unitRegistry.get(name);
    }

    @Override
    public <T extends RuleUnitData> RuleUnit<T> create(Class<T> clazz) {
        RuleUnit<T> unit = (RuleUnit<T>) ruleUnitsMap.get(clazz);
        if (unit == null) {
            unit = internalCreate(clazz);
        }
        return unit;
    }

    protected abstract <T extends RuleUnitData> RuleUnit<T> internalCreate(Class<T> clazz);

    protected void registerRuleUnit(Application application, InternalRuleUnit<?> unit) {
        ruleUnitsMap.put(unit.getRuleUnitDataClass(), unit);
        unit.setEvaluatorConfigurator(reteEvaluator -> configureReteEvaluator(application, reteEvaluator));
    }

    protected ReteEvaluator configureReteEvaluator(Application application, ReteEvaluator reteEvaluator) {
        org.kie.kogito.Config config = application.config();
        if (config != null) {
            RuleEventListenerConfig ruleEventListenerConfig = config.get(org.kie.kogito.rules.RuleConfig.class).ruleEventListeners();
            ruleEventListenerConfig.agendaListeners().forEach(reteEvaluator.getActivationsManager().getAgendaEventSupport()::addEventListener);
            ruleEventListenerConfig.ruleRuntimeListeners().forEach(reteEvaluator.getRuleRuntimeEventSupport()::addEventListener);
        }
        ((RuleUnitExecutorImpl) reteEvaluator).setRuleUnits(this);
        return reteEvaluator;
    }
}
