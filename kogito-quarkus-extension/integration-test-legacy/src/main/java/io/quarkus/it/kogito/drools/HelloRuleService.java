/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.quarkus.it.kogito.drools;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;
import org.kie.kogito.rules.units.SessionData;

@ApplicationScoped
public class HelloRuleService {

    @Inject
    @Named("simpleKS")
    RuleUnit<SessionData> ruleUnit;

    public String run() {

        Result result = new Result();
        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        SessionData memory = new SessionData();
        memory.add(result);
        memory.add(mark);
        memory.add(edson);
        memory.add(mario);

        RuleUnitInstance<SessionData> instance = ruleUnit.createInstance(memory);
        instance.fire();

        return result.toString();
    }
}
