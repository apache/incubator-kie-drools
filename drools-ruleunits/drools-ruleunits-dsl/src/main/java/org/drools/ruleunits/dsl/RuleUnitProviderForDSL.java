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
import org.drools.core.reteoo.ReteDumper;
import org.drools.model.Global;
import org.drools.modelcompiler.KieBaseBuilder;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.impl.EntryPointDataProcessor;
import org.drools.ruleunits.impl.ReteEvaluatorBasedRuleUnitInstance;
import org.drools.ruleunits.impl.RuleUnitProviderImpl;
import org.drools.ruleunits.impl.factory.AbstractRuleUnit;
import org.drools.ruleunits.impl.factory.AbstractRuleUnits;
import org.drools.ruleunits.impl.sessions.RuleUnitExecutorImpl;
import org.kie.api.runtime.rule.EntryPoint;

public class RuleUnitProviderForDSL extends RuleUnitProviderImpl {

    private static final boolean DUMP_GENERATED_RETE = false;

    @Override
    public int servicePriority() {
        return 1;
    }

    @Override
    protected <T extends RuleUnitData> Map<String, RuleUnit> generateRuleUnit(T ruleUnitData) {
        if (ruleUnitData instanceof RuleUnitDefinition) {
            RuleUnitDefinition ruleUnitDef = (RuleUnitDefinition) ruleUnitData;
            RulesFactory rulesFactory = new RulesFactory(ruleUnitDef);
            ruleUnitDef.defineRules(rulesFactory);
            RuleUnit<T> ruleUnit = new ModelRuleUnit<>((Class<T>) ruleUnitData.getClass(), rulesFactory);
            return Map.of(ruleUnitData.getClass().getCanonicalName(), ruleUnit);
        }
        return super.generateRuleUnit(ruleUnitData);
    }

    public static class ModelRuleUnit<T extends RuleUnitData> extends AbstractRuleUnit<T> {

        private final RulesFactory rulesFactory;
        private final RuleBase ruleBase;

        private ModelRuleUnit(Class<T> type, RulesFactory rulesFactory) {
            super(type.getCanonicalName(), DummyRuleUnits.INSTANCE);
            this.rulesFactory = rulesFactory;
            this.ruleBase = KieBaseBuilder.createKieBaseFromModel( rulesFactory.toModel() );
            if (DUMP_GENERATED_RETE) {
                ReteDumper.dumpRete(this.ruleBase);
            }
        }

        @Override
        public RuleUnitInstance<T> internalCreateInstance(T data) {
            ReteEvaluator reteEvaluator = new RuleUnitExecutorImpl(ruleBase);
            return new DSLRuleUnitInstance<>(this, data, reteEvaluator, rulesFactory);
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

        public DSLRuleUnitInstance(RuleUnit<T> unit, T workingMemory, ReteEvaluator reteEvaluator, RulesFactory rulesFactory) {
            super(unit, workingMemory, reteEvaluator);
            internalBind(reteEvaluator, rulesFactory);
        }

        protected void bind(ReteEvaluator reteEvaluator, T workingMemory) {
            // empty to allow a subsequent bind also using the RulesContext
        }

        private void internalBind(ReteEvaluator reteEvaluator, RulesFactory rulesFactory) {
            for (Map.Entry<Object, Global> entry : rulesFactory.getGlobals().entrySet()) {
                String dataSourceName = entry.getValue().getName();
                Object v = entry.getKey();
                if (v instanceof DataSource) {
                    DataSource<?> o = (DataSource<?>) v;
                    EntryPoint ep = reteEvaluator.getEntryPoint(dataSourceName);
                    if (ep != null) { // can be null if this DataSource isn't used in the LHS of any rule
                        o.subscribe(new EntryPointDataProcessor(ep));
                    }
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
