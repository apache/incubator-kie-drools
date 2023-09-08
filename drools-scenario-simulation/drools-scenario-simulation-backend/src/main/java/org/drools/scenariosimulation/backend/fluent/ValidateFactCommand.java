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
package org.drools.scenariosimulation.backend.fluent;

import java.util.Collection;
import java.util.List;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class ValidateFactCommand implements ExecutableCommand<Void> {

    private final List<FactCheckerHandle> factToCheck;

    public ValidateFactCommand(List<FactCheckerHandle> factToCheck) {
        this.factToCheck = factToCheck;
    }

    @Override
    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup(KieSession.class);
        Collection<?> objects = ksession.getObjects(new ConditionFilter(factToCheck));
        if (!objects.isEmpty()) {
            factToCheck.forEach(fact -> fact.getScenarioResult().setResult(true));
        } else {
            factToCheck.forEach(fact -> fact.getScenarioResult().getFactMappingValue().setExceptionMessage("There is no instance which satisfies the expected conditions"));
        }
        return null;
    }
}
