/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.rules.impl;

import java.lang.reflect.Field;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;
import org.kie.kogito.rules.RuleUnitMemory;

public class AbstractRuleUnitInstance<T extends RuleUnitMemory> implements RuleUnitInstance<T> {

    private final T unitMemory;
    private final RuleUnit<T> unit;
    private final KieSession runtime;

    public AbstractRuleUnitInstance( RuleUnit<T> unit, T unitMemory, KieSession runtime ) {
        this.unit = unit;
        this.runtime = runtime;
        this.unitMemory = unitMemory;
        bind( runtime, unitMemory );
    }

    public int fire() {
        return runtime.fireAllRules();
    }

    @Override
    public RuleUnit<T> unit() {
        return unit;
    }

    public T workingMemory() {
        return unitMemory;
    }

    protected void bind(KieSession runtime, T workingMemory) {
        try {
            for (Field f : workingMemory.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object v = null;
                v = f.get(workingMemory);
                String dataSourceName = f.getName();
                if ( v instanceof DataSource ) {
                    DataSource<?> o = ( DataSource<?> ) v;
                    EntryPoint ep = runtime.getEntryPoint(dataSourceName);
                    o.subscribe(ep::insert);
                }
                try {
                    runtime.setGlobal( dataSourceName, v );
                } catch (RuntimeException e) {
                    // ignore if the global doesn't exist
                }
            }
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
    }
}
