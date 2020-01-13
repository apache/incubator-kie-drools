/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.rules.units.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.kie.api.definition.KiePackage;
import org.kie.internal.ruleunit.ApplyPmmlModelCommandExecutor;
import org.kie.internal.ruleunit.RuleUnitComponentFactory;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.kie.kogito.rules.DataSource;
import org.kie.kogito.rules.RuleUnitData;
import org.kie.kogito.rules.units.GeneratedRuleUnitDescription;
import org.kie.kogito.rules.units.ReflectiveRuleUnitDescription;

public class RuleUnitComponentFactoryImpl implements RuleUnitComponentFactory {

    private final Map<String, GeneratedRuleUnitDescription> generatedRuleUnitDescriptions = new HashMap<>();

    public void registerRuleUnitDescription(GeneratedRuleUnitDescription ruleUnitDescription) {
        generatedRuleUnitDescriptions.put(ruleUnitDescription.getCanonicalName(), ruleUnitDescription);
    }

    @Override
    public RuleUnitDescription createRuleUnitDescription(KiePackage pkg, Class<?> ruleUnitClass ) {
        return new ReflectiveRuleUnitDescription((InternalKnowledgePackage) pkg, (Class<? extends RuleUnitData>) ruleUnitClass );
    }

    @Override
    public RuleUnitDescription createRuleUnitDescription(KiePackage pkg, String ruleUnitSimpleName ) {
        return generatedRuleUnitDescriptions.get(pkg.getName() + '.' + ruleUnitSimpleName);
    }

    @Override
    public ApplyPmmlModelCommandExecutor newApplyPmmlModelCommandExecutor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRuleUnitClass( Class<?> ruleUnitClass ) {
        return RuleUnitData.class.isAssignableFrom(ruleUnitClass);
    }

    @Override
    public boolean isDataSourceClass( Class<?> ruleUnitClass ) {
        return DataSource.class.isAssignableFrom(ruleUnitClass);
    }

    @Override
    public boolean isLegacyRuleUnit() {
        return false;
    }
}
