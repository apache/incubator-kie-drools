/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.impl;

import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.core.spi.Activation;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;

public interface InternalRuleUnitExecutor extends RuleUnitExecutor {

    void cancelActivation( Activation activation );

    void onSuspend();
    void onResume();

    RuleUnitDescr switchToRuleUnit( Class<? extends RuleUnit> ruleUnitClass );
    RuleUnitDescr switchToRuleUnit( RuleUnit ruleUnit );

    void guardRuleUnit( Class<? extends RuleUnit> ruleUnitClass, Activation activation);
    void guardRuleUnit( RuleUnit ruleUnit, Activation activation );

    RuleUnit getCurrentRuleUnit();
}
