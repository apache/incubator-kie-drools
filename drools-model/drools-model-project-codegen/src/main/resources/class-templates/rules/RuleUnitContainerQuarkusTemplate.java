/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package $Package$;

import org.kie.kogito.rules.RuleUnit;

@javax.enterprise.context.ApplicationScoped
public class RuleUnits extends org.kie.kogito.drools.core.unit.AbstractRuleUnits implements org.kie.kogito.rules.RuleUnits {

    @javax.inject.Inject
    javax.enterprise.inject.Instance<org.kie.kogito.rules.RuleUnit<? extends org.kie.kogito.rules.RuleUnitData>> ruleUnits;

    private java.util.Map<String, org.kie.kogito.rules.RuleUnit<? extends org.kie.kogito.rules.RuleUnitData>> mappedRuleUnits = new java.util.HashMap<>();

    @javax.annotation.PostConstruct
    public void setup() {
        for (org.kie.kogito.rules.RuleUnit<? extends org.kie.kogito.rules.RuleUnitData> ruleUnit : ruleUnits) {
            mappedRuleUnits.put(ruleUnit.id(), ruleUnit);
        }
    }

    protected org.kie.kogito.rules.RuleUnit<?> create(String fqcn) {
        return mappedRuleUnits.get(fqcn);
    }

}
