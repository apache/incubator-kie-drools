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
package org.drools.model.codegen.execmodel;

import org.drools.model.codegen.execmodel.domain.Child;
import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MixedArgumentTest extends BaseModelTest {

    private static final String RULE_STRING = "package mixedarguments\n" +
            "\n" +
            "import " + Person.class.getCanonicalName() + "\n" +
            "import " + Child.class.getCanonicalName() + "\n" +
            "import java.util.List; \n" +
            "global java.util.List result; \n" +
            "rule \"mixedArguments\"\n" +
            "when \n" +
            "    $person1 : Person(age == 37)\n" +
            "    $child1 : Child(age == 5 && parent == \"John\")\n" +
            "    $person2 : Person(age == 40)\n" +
            "    $child2 : Child(age == 5 && parent == \"Bob\")\n" +
            "    $personNo : Integer() from doNothing($person1, $child1.getName, $child2.getName, $person2.getName)\n" +
            "then \n" +
            "     result.add($personNo); \n " +
            "end \n" +
            "\n" +
            "function Integer doNothing(Person person, String firstName, String secondName, String thirdName) {\n" +
            "    return 1; \n" +
            "}";


    @ParameterizedTest
    @MethodSource("parameters")
    void mixedArgumentsTest(RUN_TYPE runType){

        KieSession ksession = getKieSession(runType, RULE_STRING);
        List<Integer> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("John", 37));
        ksession.insert(new Child("Johnny", 5, "John"));
        ksession.insert(new Person("Bob", 40));
        ksession.insert(new Child("Bobby", 5, "Bob"));

        int rulesFired = ksession.fireAllRules();

        assertThat(rulesFired).isEqualTo(1);
    }
}
