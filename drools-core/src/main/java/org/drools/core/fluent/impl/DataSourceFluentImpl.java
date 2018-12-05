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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.drools.core.command.AddDataSourceCommand;
import org.kie.api.runtime.rule.DataSource;
import org.kie.internal.builder.fluent.DataSourceFluent;
import org.kie.internal.builder.fluent.RuleUnitFluent;

public class DataSourceFluentImpl<E, U extends RuleUnitFluent> implements DataSourceFluent<E, U> {

    protected ExecutableImpl fluentCtx;
    protected U ruleUnitFluent;
    private DataSource<E> values;
    private List<String> names = new LinkedList<>();
    private Class<E> clazz;

    public DataSourceFluentImpl(ExecutableImpl fluentCtx, U ruleUnitFluent, Class<E> clazz) {
        this.fluentCtx = fluentCtx;
        this.ruleUnitFluent = ruleUnitFluent;
        this.clazz = clazz;
        this.values = DataSource.create((E[]) Collections.emptyList().toArray());
    }

    public ExecutableImpl getFluentContext() {
        return fluentCtx;
    }

    @Override
    public DataSourceFluent<E, U> addBinding(String name) {
        this.names.add(name);
        return this;
    }

    @Override
    public DataSourceFluent<E, U> insert(E object) {
        values.insert(object);
        return this;
    }

    @Override
    public U buildDataSource() {
        fluentCtx.addCommand(new AddDataSourceCommand<>(clazz, names, values));
        return ruleUnitFluent;
    }
}
