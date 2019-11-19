/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

public class CustomerCaseTest extends OnlyPatternModelTest {


    @Test
    public void testCustomer() throws Exception {
        String str =

                "import " + DataType.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                        "global org.drools.modelcompiler.domain.Result result;\n" +
                "rule \"rule1\"\n" +
                "when org.drools.modelcompiler.DataType (\n" +
                "        field1 == \"FF\"\n" +
                "        , field2 == \"BBB\"\n" +
                ")\n" +
                "then\n" +
                "    result.setValue(0);\n" +
                "end\n" +
                "rule \"rule2\"\n" +
                "when org.drools.modelcompiler.DataType (\n" +
                "        field2 == \"BBB\"\n" +
                ")\n" +
                "then\n" +
                "    result.setValue(0);\n" +
                "end\n";

        KieSession ksession = getKieSession(str);

        DataType st = new DataType("FF", "BBB");
        DataType st2 = new DataType("FF", "CCC");
        ksession.insert(st);
        ksession.insert(st2);

        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        String name = reader.readLine();
        System.out.println(name);

        Result r = new Result();
        ksession.setGlobal("result", r);
        Assertions.assertThat(ksession.fireAllRules()).isEqualTo(2);
        assertEquals(0, r.getValue());


    }
}
