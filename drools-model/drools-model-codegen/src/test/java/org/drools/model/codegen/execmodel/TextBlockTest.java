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

import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class TextBlockTest extends OnlyExecModelTest {

    public TextBlockTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testMultiLineStrings() {
        final String str =
                "package org.drools.mvel.compiler\n" +
                        "global java.util.List list;\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule r1 when\n" +
                        "    $p : Person( )\n" +
                        "then\n" +
                        // to avoid indentation of the output string
                        // the closing """ needs to be indented
                        // at least to the first character of the string
                        "  java.lang.String name = \"\"\"\n   " +
                        "         name\n   " +
                        "         with multi line content\n   " +
                        "         \"\"\";  " +
                        "  $p.setName(name);" +
                        "end\n";


        KieSession ksession = getKieSession(str );
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Person person = new Person("A");
        ksession.insert(person);
        int i = ksession.fireAllRules();
        assertThat(person.getName()).isEqualTo("name\nwith multi line content\n");
    }
}
