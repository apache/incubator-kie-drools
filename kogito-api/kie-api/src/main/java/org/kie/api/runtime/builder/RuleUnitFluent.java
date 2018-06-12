/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.api.runtime.builder;

import java.util.function.Function;
import java.util.function.Supplier;

import org.kie.api.runtime.Context;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;

/**
 * See {@link RuleUnit} and {@link RuleUnitExecutor}
 */
public interface RuleUnitFluent<T extends RuleUnitFluent, U> {

    <E extends RuleUnit> T run(Class<E> unit);

    <E extends RuleUnit> T run(Supplier<E> unitSupplier);

    T bindVariableByExpression(String name, Function<Context, Object> expression);

    <E> T bindVariable(String name, E variableValue);

    <E> DataSourceFluent<E, T> createDataSource(Class<E> type);

    U dispose();

    T setGlobal(String identifier, Object object);

    T getGlobal(String identifier);

}
