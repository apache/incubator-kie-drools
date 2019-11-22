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

package org.drools.ruleunit.impl;

import org.drools.ruleunit.command.pmml.ApplyPmmlModelCommandExecutorImpl;
import org.kie.api.definition.KiePackage;
import org.kie.internal.ruleunit.ApplyPmmlModelCommandExecutor;
import org.kie.internal.ruleunit.RuleUnitComponentFactory;
import org.kie.internal.ruleunit.RuleUnitDescription;
import org.drools.ruleunit.RuleUnit;

public class RuleUnitComponentFactoryImpl implements RuleUnitComponentFactory {

    @Override
    public RuleUnitDescription createRuleUnitDescription( KiePackage pkg, Class<?> ruleUnitClass ) {
        return new RuleUnitDescriptionImpl( pkg, ruleUnitClass );
    }

    @Override
    public ApplyPmmlModelCommandExecutor newApplyPmmlModelCommandExecutor() {
        return new ApplyPmmlModelCommandExecutorImpl();
    }

    @Override
    public boolean isRuleUnitClass( Class<?> ruleUnitClass ) {
        return RuleUnit.class.isAssignableFrom(ruleUnitClass);
    }
}
