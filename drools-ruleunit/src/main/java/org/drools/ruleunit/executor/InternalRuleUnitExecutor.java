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

package org.drools.ruleunit.executor;

import java.util.Collection;

import org.drools.core.spi.Activation;
import org.drools.ruleunit.datasources.InternalDataSource;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.ObjectFilter;
import org.drools.ruleunit.RuleUnit;
import org.drools.ruleunit.RuleUnitExecutor;

public interface InternalRuleUnitExecutor extends RuleUnitExecutor {

    void cancelActivation( Activation activation );

    void onSuspend();
    void onResume();

    void switchToRuleUnit( Class<? extends RuleUnit> ruleUnitClass, Activation activation );
    void switchToRuleUnit( RuleUnit ruleUnit, Activation activation );

    void guardRuleUnit( Class<? extends RuleUnit> ruleUnitClass, Activation activation);
    void guardRuleUnit( RuleUnit ruleUnit, Activation activation );

    RuleUnit getCurrentRuleUnit();

    KieRuntimeLogger addConsoleLogger();
    KieRuntimeLogger addFileLogger(String fileName);
    KieRuntimeLogger addFileLogger(String fileName, int maxEventsInMemory);
    KieRuntimeLogger addThreadedFileLogger(String fileName, int interval);
    
    Collection<?> getSessionObjects();
    Collection<?> getSessionObjects(ObjectFilter filter);

    void bindDataSource(InternalDataSource dataSource );
}
