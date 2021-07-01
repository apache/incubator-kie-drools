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
package org.kie.kogito.legacy.rules;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.kogito.rules.RuleConfig;
import org.kie.kogito.rules.RuleUnitData;

public interface KieRuntimeBuilder {
    KieBase getKieBase();

    KieBase getKieBase(String name);

    default KieBase getKieBase(Class<? extends RuleUnitData> ruleUnit) {
        return getKieBase(ruleUnit.getName().replace('.', '$') + "KieBase");
    }

    KieSession newKieSession();

    KieSession newKieSession(String sessionName);

    KieSession newKieSession(String sessionName, RuleConfig ruleConfig);

    default KieSession newKieSession(Class<? extends RuleUnitData> ruleUnit) {
        return newKieSession(ruleUnit.getName().replace('.', '$') + "KieSession");
    }
}
