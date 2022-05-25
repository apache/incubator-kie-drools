/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.drools.ruleunits.dsl;

import java.util.Map;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.RuleBase;
import org.drools.model.Global;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.impl.EntryPointDataProcessor;
import org.drools.ruleunits.impl.ReteEvaluatorBasedRuleUnitInstance;
import org.drools.ruleunits.impl.factory.AbstractRuleUnit;
import org.drools.ruleunits.impl.factory.AbstractRuleUnits;
import org.drools.ruleunits.impl.sessions.RuleUnitExecutorImpl;
import org.kie.api.runtime.rule.EntryPoint;

public class DSLRuleUnit {

    public static <T extends RuleUnitDefinition> RuleUnitInstance<T> instance(T ruleUnit) {
        RulesContext rulesContext = new RulesContext(ruleUnit);
        ruleUnit.defineRules(rulesContext);
        return new ModelRuleUnit<>((Class<T>) ruleUnit.getClass(), rulesContext).createInstance(ruleUnit);
    }

    public static class ModelRuleUnit<T extends RuleUnitData> extends AbstractRuleUnit<T> {

        private final RulesContext rulesContext;
        private final RuleBase ruleBase;

        private ModelRuleUnit(Class<T> type, RulesContext rulesContext) {
            super(type.getCanonicalName(), DummyRuleUnits.INSTANCE);
            this.rulesContext = rulesContext;
            this.ruleBase = KieBaseBuilder.createKieBaseFromModel( rulesContext.toModel() );
        }

        @Override
        public RuleUnitInstance<T> internalCreateInstance(T data) {
            ReteEvaluator reteEvaluator = new RuleUnitExecutorImpl(ruleBase);
            return new DSLRuleUnitInstance<>(this, data, reteEvaluator, rulesContext);
        }
    }

    public static class DummyRuleUnits extends AbstractRuleUnits {

        static final DummyRuleUnits INSTANCE = new DummyRuleUnits();

        @Override
        protected RuleUnit<?> create(String fqcn) {
            throw new UnsupportedOperationException();
        }
    }

    public static class DSLRuleUnitInstance<T extends RuleUnitData> extends ReteEvaluatorBasedRuleUnitInstance<T> {

        public DSLRuleUnitInstance(RuleUnit<T> unit, T workingMemory, ReteEvaluator reteEvaluator, RulesContext rulesContext) {
            super(unit, workingMemory, reteEvaluator);
            internalBind(reteEvaluator, rulesContext);
        }

        protected void bind(ReteEvaluator reteEvaluator, T workingMemory) {
            // empty to allow a subsequent bind also using the RulesContext
        }

        private void internalBind(ReteEvaluator reteEvaluator, RulesContext rulesContext) {
            for (Map.Entry<Object, Global> entry : rulesContext.getGlobals().entrySet()) {
                String dataSourceName = entry.getValue().getName();
                Object v = entry.getKey();
                if (v instanceof DataSource) {
                    DataSource<?> o = (DataSource<?>) v;
                    EntryPoint ep = reteEvaluator.getEntryPoint(dataSourceName);
                    o.subscribe(new EntryPointDataProcessor(ep));
                }
                try {
                    reteEvaluator.setGlobal(dataSourceName, v);
                } catch (RuntimeException e) {
                    // ignore if the global doesn't exist
                }
            }
        }
    }
}
