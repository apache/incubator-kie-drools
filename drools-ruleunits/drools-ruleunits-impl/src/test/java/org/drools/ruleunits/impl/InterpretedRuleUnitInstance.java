/**
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
package org.drools.ruleunits.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.drools.core.common.ReteEvaluator;
import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.runtime.rule.EntryPoint;

public class InterpretedRuleUnitInstance<T extends RuleUnitData> extends ReteEvaluatorBasedRuleUnitInstance<T> {

    InterpretedRuleUnitInstance(RuleUnit<T> unit, T workingMemory, ReteEvaluator reteEvaluator, RuleConfig ruleConfig) {
        super(unit, workingMemory, reteEvaluator, ruleConfig);
    }

    protected void bind(ReteEvaluator reteEvaluator, T workingMemory) {
        try {
            for (PropertyDescriptor prop : Introspector.getBeanInfo(workingMemory.getClass()).getPropertyDescriptors()) {
                Field f;
                try {
                    f = workingMemory.getClass().getDeclaredField(prop.getName());
                } catch (NoSuchFieldException noSuchFieldException) {
                    // ignore not existing fields
                    continue;
                }
                f.setAccessible(true);
                Object v = f.get(workingMemory);
                String dataSourceName = f.getName();
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
        } catch (IntrospectionException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
