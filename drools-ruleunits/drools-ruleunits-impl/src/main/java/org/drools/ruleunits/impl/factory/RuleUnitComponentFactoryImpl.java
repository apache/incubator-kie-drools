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
package org.drools.ruleunits.impl.factory;

import java.util.HashMap;
import java.util.Map;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.impl.GeneratedRuleUnitDescription;
import org.drools.ruleunits.impl.ReflectiveRuleUnitDescription;
import org.kie.api.definition.KiePackage;
import org.kie.internal.ruleunit.RuleUnitComponentFactory;
import org.kie.internal.ruleunit.RuleUnitDescription;

public class RuleUnitComponentFactoryImpl implements RuleUnitComponentFactory {

    private final Map<String, GeneratedRuleUnitDescription> generatedRuleUnitDescriptions = new HashMap<>();

    public void registerRuleUnitDescription(GeneratedRuleUnitDescription ruleUnitDescription) {
        generatedRuleUnitDescriptions.put(ruleUnitDescription.getCanonicalName(), ruleUnitDescription);
    }

    @Override
    public RuleUnitDescription createRuleUnitDescription(KiePackage pkg, Class<?> ruleUnitClass) {
        return new ReflectiveRuleUnitDescription((Class<? extends RuleUnitData>) ruleUnitClass);
    }

    @Override
    public RuleUnitDescription createRuleUnitDescription(KiePackage pkg, String ruleUnitSimpleName) {
        return generatedRuleUnitDescriptions.get(pkg.getName() + '.' + ruleUnitSimpleName);
    }

    @Override
    public boolean isRuleUnitClass(Class<?> ruleUnitClass) {
        return RuleUnitData.class.isAssignableFrom(ruleUnitClass);
    }

    @Override
    public boolean isDataSourceClass(Class<?> ruleUnitClass) {
        return DataSource.class.isAssignableFrom(ruleUnitClass);
    }
}
