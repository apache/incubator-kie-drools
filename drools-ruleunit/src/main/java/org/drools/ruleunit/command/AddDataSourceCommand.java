/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.ruleunit.command;

import java.util.List;
import java.util.stream.Collectors;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.command.RegistryContext;
import org.drools.ruleunit.DataSource;
import org.drools.ruleunit.RuleUnitExecutor;

public class AddDataSourceCommand<E>
        implements
        ExecutableCommand<Void> {

    private static final long serialVersionUID = -5255184977368094272L;
    private final Class<E> clazz;
    private final List<String> names;
    private final DataSource<E> values;

    public AddDataSourceCommand(Class<E> clazz, List<String> names, DataSource<E> values) {
        this.clazz = clazz;
        this.names = names;
        this.values = values;
    }

    @Override
    public Void execute(Context context) {

        RuleUnitExecutor ruleUnitExecutor = ((RegistryContext) context).lookup(RuleUnitExecutor.class);
        if (ruleUnitExecutor == null) {
            throw new RuntimeException("No RuleUnitExecutor is present in the Registry");
        }

        for (String name : names) {
            ruleUnitExecutor.bindVariable(name, values);
        }

        return null;
    }

    @Override
    public String toString() {
        return "GenericExpressionVarBindingCommand{" +
                "names='" + names.stream().collect(Collectors.joining(",")) + "'}";
    }
}
