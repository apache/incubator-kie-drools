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

import java.util.function.Function;
import java.util.function.Supplier;

import org.drools.core.command.BindVariableToUnitCommand;
import org.drools.core.command.RunUnitCommand;
import org.drools.core.command.runtime.DisposeCommand;
import org.drools.core.command.runtime.GetGlobalCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.builder.DataSourceFluent;
import org.kie.api.runtime.builder.ExecutableBuilder;
import org.kie.api.runtime.builder.RuleUnitExecutorFluent;
import org.kie.api.runtime.rule.RuleUnit;

public class RuleUnitExecutorFluentImpl extends BaseBatchWithProcessFluent<RuleUnitExecutorFluent, ExecutableBuilder> implements RuleUnitExecutorFluent {

    public RuleUnitExecutorFluentImpl(ExecutableImpl fluentCtx) {
        super(fluentCtx);
    }

    @Override
    public RuleUnitExecutorFluent setGlobal(String identifier, Object object) {
        return this;
    }

    @Override
    public RuleUnitExecutorFluent getGlobal(String identifier) {
        fluentCtx.addCommand(new GetGlobalCommand(identifier));
        return this;
    }

    @Override
    public RuleUnitExecutorFluent bindVariableByExpression(String name, Function<Context, Object> expression) {
        fluentCtx.addCommand(new BindVariableToUnitCommand(name, expression));
        return this;
    }

    @Override
    public <E> RuleUnitExecutorFluent bindVariable(String name, E variable) {
        fluentCtx.addCommand(new BindVariableToUnitCommand(name, variable));
        return this;
    }

    @Override
    public <E> DataSourceFluent<E, RuleUnitExecutorFluent> createDataSource(Class<E> type) {
        return new DataSourceFluentImpl<>(fluentCtx, this, type);
    }

    @Override
    public ExecutableBuilder dispose() {
        fluentCtx.addCommand(new DisposeCommand());
        return fluentCtx.getExecutableBuilder();
    }

    @Override
    public <E extends RuleUnit> RuleUnitExecutorFluent run(Class<E> unit) {
        fluentCtx.addCommand(new RunUnitCommand<>(unit));
        return this;
    }

    @Override
    public <E extends RuleUnit> RuleUnitExecutorFluent run(Supplier<E> unitSupplier) {
        fluentCtx.addCommand(new RunUnitCommand<>(unitSupplier));
        return this;
    }
}