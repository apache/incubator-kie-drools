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

import java.util.function.Supplier;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.internal.command.RegistryContext;
import org.drools.ruleunit.RuleUnit;
import org.drools.ruleunit.RuleUnitExecutor;

public class RunUnitCommand<T extends RuleUnit>
        implements
        ExecutableCommand<Integer> {

    private static final long serialVersionUID = 626194815589613487L;
    private Class<T> unitClass;
    private Supplier<T> unitSupplier;

    public RunUnitCommand(Class<T> unitClass) {
        this.unitClass = unitClass;
    }

    public RunUnitCommand(Supplier<T> unitSupplier) {
        this.unitSupplier = unitSupplier;
    }

    @Override
    public Integer execute(Context context) {
        RuleUnitExecutor ruleUnitExecutor = ((RegistryContext) context).lookup(RuleUnitExecutor.class);

        if (ruleUnitExecutor == null) {
            throw new IllegalStateException("RuleUnitExecutor si not present in the Registry");
        }

        int firedRules;

        if (unitClass != null) {
            firedRules = ruleUnitExecutor.run(unitClass);
        } else if (unitSupplier != null) {
            firedRules = ruleUnitExecutor.run(unitSupplier.get());
        } else {
            throw new IllegalStateException("State is inconsistent because there is no unitClass nor unitSupplier");
        }

        return firedRules;
    }

    @Override
    public String toString() {
        return "RunUnitCommand{" +
                "unitClass='" + unitClass + '\'' +
                ", unitSupplier=" + unitSupplier +
                '}';
    }
}
