/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.fluent.impl;

import java.util.function.BiFunction;

import org.drools.core.command.NewKieSessionCommand;
import org.drools.core.command.NewRuleUnitExecutorCommand;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.builder.fluent.DMNRuntimeFluent;
import org.kie.internal.builder.fluent.ExecutableBuilder;
import org.kie.internal.builder.fluent.KieContainerFluent;
import org.kie.internal.builder.fluent.KieSessionFluent;
import org.kie.internal.builder.fluent.RuleUnitExecutorFluent;

public class KieContainerFluentImpl extends BaseBatchFluent<ExecutableBuilder, ExecutableBuilder> implements KieContainerFluent {

    private ExecutableImpl ctx;

    public KieContainerFluentImpl(ExecutableImpl ctx) {
        super(ctx);
        this.ctx = ctx;
    }

    @Override
    public KieSessionFluent newSession() {
        return newSession(null);
    }

    @Override
    public KieSessionFluent newSession(String sessionId) {
        NewKieSessionCommand cmd = new NewKieSessionCommand(sessionId);
        ctx.addCommand(cmd);
        return new KieSessionFluentImpl(ctx);
    }

    @Override
    public KieSessionFluent newSessionCustomized(String sessionId, BiFunction<String, KieContainer, KieContainer> customizer) {
        NewKieSessionCommand cmd = new NewKieSessionCommand(sessionId);
        cmd.setBeforeSessionCreation(customizer);
        ctx.addCommand(cmd);
        return new KieSessionFluentImpl(ctx);
    }

    @Override
    public RuleUnitExecutorFluent newRuleUnitExecutor() {
        return newRuleUnitExecutor(null);
    }

    @Override
    public RuleUnitExecutorFluent newRuleUnitExecutor(String sessionName) {
        NewRuleUnitExecutorCommand cmd = new NewRuleUnitExecutorCommand(sessionName);
        ctx.addCommand(cmd);
        return new RuleUnitExecutorFluentImpl(ctx);
    }

    @Override
    public DMNRuntimeFluent newDMNRuntime() {
        return DMNRuntimeFluent.create(ctx);
    }
}
