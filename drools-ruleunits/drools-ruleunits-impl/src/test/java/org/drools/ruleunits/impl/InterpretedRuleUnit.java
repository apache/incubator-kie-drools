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

import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.impl.RuleBase;
import org.drools.modelcompiler.ExecutableModelProject;
import org.drools.modelcompiler.builder.CanonicalModelKieProject;
import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitData;
import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.impl.factory.AbstractRuleUnit;
import org.drools.ruleunits.impl.sessions.RuleUnitExecutorImpl;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;

import static org.drools.compiler.kproject.models.KieBaseModelImpl.defaultKieBaseModel;

/**
 * A fully-runtime, reflective implementation of a rule unit, useful for testing
 */
public class InterpretedRuleUnit<T extends RuleUnitData> extends AbstractRuleUnit<T> {

    private final boolean useExecModel;

    public static <T extends RuleUnitData> RuleUnit<T> of(Class<T> type, boolean useExecModel) {
        return new InterpretedRuleUnit<>(type.getCanonicalName(), useExecModel);
    }

    public static <T extends RuleUnitData> RuleUnitInstance<T> instance(T ruleUnit, boolean useExecModel) {
        return of((Class<T>) ruleUnit.getClass(), useExecModel).createInstance(ruleUnit);
    }

    private InterpretedRuleUnit(String id, boolean useExecModel) {
        super(id);
        this.useExecModel = useExecModel;
    }

    @Override
    public RuleUnitInstance<T> internalCreateInstance(T data) {
        RuleBase ruleBase = createRuleBase(data);
        ReteEvaluator reteEvaluator = new RuleUnitExecutorImpl(ruleBase);
        return new InterpretedRuleUnitInstance<>(this, data, reteEvaluator);
    }

    private RuleBase createRuleBase(T data) {
        InternalKieModule kieModule = createRuleUnitKieModule(data.getClass(), useExecModel);
        KieModuleKieProject kieProject = createRuleUnitKieProject(kieModule, useExecModel);

        BuildContext buildContext = new BuildContext();
        RuleBase kBase = kieModule.createKieBase((KieBaseModelImpl) defaultKieBaseModel(), kieProject, buildContext, null);
        if (kBase == null) {
            // build error, throw runtime exception
            throw new RuntimeException("Error while creating KieBase" + buildContext.getMessages().filterMessages(Message.Level.ERROR));
        }
        return kBase;
    }

    private static KieModuleKieProject createRuleUnitKieProject(InternalKieModule kieModule, boolean useExecModel) {
        return useExecModel ?
                new CanonicalModelKieProject(kieModule, kieModule.getModuleClassLoader()) :
                new KieModuleKieProject(kieModule, kieModule.getModuleClassLoader());
    }

    private static InternalKieModule createRuleUnitKieModule(Class<?> unitClass, boolean useExecModel) {
        // transform foo.bar.Baz to /foo/bar/Baz.drl
        // this currently only works for single files
        String path = String.format("%s.drl", unitClass.getCanonicalName().replace('.', '/'));

        KieServices ks = KieServices.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ks.getResources().newClassPathResource(path));
        return (InternalKieModule) ks.newKieBuilder( kfs )
                .getKieModule(useExecModel ? ExecutableModelProject.class : DrlProject.class);
    }

    public static KieModuleKieProject createRuleUnitKieProject(Class<?> unitClass) {
        InternalKieModule kieModule = createRuleUnitKieModule(unitClass, true);
        return createRuleUnitKieProject(kieModule, true);
    }
}
