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

import java.time.Instant;
import java.util.Date;

import org.drools.model.codegen.execmodel.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class MaterializedLambdaTest extends BaseModelTest {

    public MaterializedLambdaTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testMaterializeLambda() {
        String str =

                "import " + DataType.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                "global Result result;\n" +
                "rule \"rule1\"\n" +
                "when " + DataType.class.getCanonicalName() + " (\n" +
                "        field1 == \"FF\"\n" +
                "        , field2 == \"BBB\"\n" +
                ")\n" +
                "then\n" +
                "    result.setValue(0);\n" +
                "end\n" +
                "rule \"rule2\"\n" +
                "when " + DataType.class.getCanonicalName() + " (\n" +
                "        field2 == \"BBB\"\n" +
                "        , fieldDate >= \"27-Oct-2019\"\n" +
                ")\n" +
                "then\n" +
                "    result.setValue(0);\n" +
                "end\n";

        KieSession ksession = getKieSession(str);

        DataType st = new DataType("FF", "BBB");
        DataType st2 = new DataType("FF", "CCC");
        ksession.insert(st);
        ksession.insert(st2);

        DataType st3 = new DataType("AA", "CCC", Date.from(Instant.parse("2018-11-30T18:35:24Z")));
        ksession.insert(st3);

        Result r = new Result();
        ksession.setGlobal("result", r);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(r.getValue()).isEqualTo(0);
    }

    public static class Executor {

        public static void execute(Runnable r) {
            r.run();
        }
    }

    // DROOLS-4858
    @Test
    public void testMaterializeLambdaWithNested() {
        String str =
                "import " + Executor.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                "global Result result;\n" +
                "rule LambdaProblem when\n" +
                "  $i :Integer()\n" +
                "then\n" +
                "    System.out.println($i);\n" +
                "    Executor.execute(() -> { " +
                "       System.out.println(\"Integer is \" + $i);" +
                "        result.setValue($i);" +
                "    });" +
                "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(42);

        Result r = new Result();
        ksession.setGlobal("result", r);

        int actual = ksession.fireAllRules();
        assertThat(actual).isEqualTo(1);
        assertThat(r.getValue()).isEqualTo(42);
    }
}
