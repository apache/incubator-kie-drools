/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen;

import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.codegen.data.Person;
import org.kie.kogito.codegen.unit.AnnotatedRules;
import org.kie.kogito.rules.DataHandle;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitInstance;

public class AnnotatedRuleUnitCompilerTest extends AbstractCodegenTest {

    @Test
    public void testAnnotatedRuleUnit() throws Exception {
        Application application = generateRulesFromJava("org/kie/kogito/codegen/unit/AnnotatedRules.java");

        AnnotatedRules adults = new AnnotatedRules();

        adults.getPersons().add(new Person( "Mario", 45 ));
        adults.getPersons().add(new Person( "Marilena", 47 ));

        Person sofia = new Person( "Sofia", 7 );
        DataHandle dhSofia = adults.getPersons().add(sofia);

        RuleUnit<AnnotatedRules> unit = application.ruleUnits().create(AnnotatedRules.class);
        RuleUnitInstance<AnnotatedRules> instance = unit.createInstance(adults);

    }

}
