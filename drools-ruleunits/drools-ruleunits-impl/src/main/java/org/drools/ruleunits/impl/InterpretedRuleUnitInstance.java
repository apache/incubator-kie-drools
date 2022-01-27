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
package org.drools.ruleunits.impl;

import java.lang.reflect.Field;

import org.drools.core.common.ReteEvaluator;
import org.kie.api.runtime.rule.EntryPoint;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;

public class InterpretedRuleUnitInstance<T extends RuleUnitData> extends ReteEvaluatorBasedRuleUnitInstance<T> {

    InterpretedRuleUnitInstance(RuleUnit<T> unit, T workingMemory, ReteEvaluator reteEvaluator) {
        super(unit, workingMemory, reteEvaluator);
    }

    protected void bind(ReteEvaluator reteEvaluator, T workingMemory) {
        try {
            for (Field f : workingMemory.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object v = f.get(workingMemory);
                String dataSourceName = String.format(
                        "%s.%s", workingMemory.getClass().getCanonicalName(), f.getName());
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
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
    }
}
