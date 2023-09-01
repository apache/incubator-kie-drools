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

import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.InternalRuleBase;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.drools.ruleunits.impl.factory.AbstractRuleUnit;
import org.drools.ruleunits.impl.sessions.RuleUnitExecutorImpl;
import org.kie.api.builder.Message;

import static org.drools.compiler.kproject.models.KieBaseModelImpl.defaultKieBaseModel;
import static org.drools.ruleunits.impl.RuleUnitProviderImpl.createRuleUnitKieModule;
import static org.drools.ruleunits.impl.RuleUnitProviderImpl.createRuleUnitKieProject;

/**
 * A fully-runtime, reflective implementation of a rule unit, useful for testing
 */
public class InterpretedRuleUnit<T extends RuleUnitData> extends AbstractRuleUnit<T> {

    public static <T extends RuleUnitData> RuleUnitInstance<T> instance(T ruleUnitData) {
        InterpretedRuleUnit<T> interpretedRuleUnit = new InterpretedRuleUnit<>((Class<T>) ruleUnitData.getClass());
        return interpretedRuleUnit.createInstance(ruleUnitData);
    }

    public static <T extends RuleUnitData> RuleUnitInstance<T> instance(T ruleUnitData, RuleConfig ruleConfig) {
        InterpretedRuleUnit<T> interpretedRuleUnit = new InterpretedRuleUnit<>((Class<T>) ruleUnitData.getClass());
        return interpretedRuleUnit.createInstance(ruleUnitData, ruleConfig);
    }

    private InterpretedRuleUnit(Class<T> ruleUnitDataClass) {
        super(ruleUnitDataClass);
    }

    @Override
    public RuleUnitInstance<T> internalCreateInstance(T data, RuleConfig ruleConfig) {
        InternalRuleBase ruleBase = createRuleBase(data);
        ReteEvaluator reteEvaluator = new RuleUnitExecutorImpl(ruleBase);
        return new InterpretedRuleUnitInstance<>(this, data, reteEvaluator, ruleConfig);
    }

    private InternalRuleBase createRuleBase(T data) {
        InternalKieModule kieModule = createRuleUnitKieModule(data.getClass(), false);
        KieModuleKieProject kieProject = createRuleUnitKieProject(kieModule, false);

        BuildContext buildContext = new BuildContext();
        InternalRuleBase kBase = kieModule.createKieBase((KieBaseModelImpl) defaultKieBaseModel(), kieProject, buildContext, null);
        if (kBase == null) {
            // build error, throw runtime exception
            throw new RuntimeException("Error while creating KieBase" + buildContext.getMessages().filterMessages(Message.Level.ERROR));
        }
        return kBase;
    }
}
