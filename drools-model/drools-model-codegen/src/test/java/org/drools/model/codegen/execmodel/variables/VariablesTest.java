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
package org.drools.model.codegen.execmodel.variables;

import java.util.Collection;

import org.drools.model.codegen.execmodel.BaseModelTest;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

public class VariablesTest extends BaseModelTest {

    public VariablesTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testThreeVariables() {

        String str = "import " + SimpleObject.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                "rule \"Insert Result when 2 SimpleObjectsObjects with same id and different value\"\n " +
                "when\n" +
                "SimpleObject ($id : id, $v1 : value)\n" +
                "SimpleObject (id == $id, $v2: value, value > $v1)\n" +
                "not Result( id == $id )\n" +
                "then\n" +
                "insert (new Result($id, $v1 + $v2));\n" +
                "end";

        KieSession ksession = getKieSession(str);

        SimpleObject m1 = new SimpleObject("id", 1);
        SimpleObject m2 = new SimpleObject("id", 2);
        ksession.insert(m1);
        ksession.insert(m2);

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(3);
    }

    @Test
    public void testFourVariables() {

        String str = "import " + SimpleObject.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                "rule \"Insert Result when 3 SimpleObjectsObjects with same id and different value\"\n " +
                "when\n" +
                "SimpleObject ($id : id, $v1 : value)\n" +
                "SimpleObject (id == $id, $v2: value, value > $v1)\n" +
                "SimpleObject (id == $id, $v3: value, value > $v2)\n" +
                "not Result( id == $id )\n" +
                "then\n" +
                "insert (new Result($id, $v1 + $v2 + $v3));\n" +
                "end";

        KieSession ksession = getKieSession(str);

        SimpleObject m1 = new SimpleObject("id", 1);
        SimpleObject m2 = new SimpleObject("id", 2);
        SimpleObject m3 = new SimpleObject("id", 3);
        ksession.insert(m1);
        ksession.insert(m2);
        ksession.insert(m3);

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(6);
    }

    @Test
    public void testFiveVariables() {

        String str = "import " + SimpleObject.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                "rule \"Insert Result when 4 SimpleObjectsObjects with same id and different value\"\n " +
                "when\n" +
                "SimpleObject ($id : id, $v1 : value)\n" +
                "SimpleObject (id == $id, $v2: value, value > $v1)\n" +
                "SimpleObject (id == $id, $v3: value, value > $v2)\n" +
                "SimpleObject (id == $id, $v4: value, value > $v3)\n" +
                "not Result( id == $id )\n" +
                "then\n" +
                "insert (new Result($id, $v1 + $v2 + $v3 + $v4));\n" +
                "end";

        KieSession ksession = getKieSession(str);

        SimpleObject m1 = new SimpleObject("id", 1);
        SimpleObject m2 = new SimpleObject("id", 2);
        SimpleObject m3 = new SimpleObject("id", 3);
        SimpleObject m4 = new SimpleObject("id", 4);
        ksession.insert(m1);
        ksession.insert(m2);
        ksession.insert(m3);
        ksession.insert(m4);

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(10);
    }

    @Test
    public void testSixVariables() {

        String str = "import " + SimpleObject.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                "rule \"Insert Result when 4 SimpleObjectsObjects with same id and different value\"\n " +
                "when\n" +
                "SimpleObject ($id : id, $v1 : value)\n" +
                "SimpleObject (id == $id, $v2: value, value > $v1)\n" +
                "SimpleObject (id == $id, $v3: value, value > $v2)\n" +
                "SimpleObject (id == $id, $v4: value, value > $v3)\n" +
                "SimpleObject (id == $id, $v5: value, value > $v4)\n" +
                "not Result( id == $id )\n" +
                "then\n" +
                "insert (new Result($id, $v1 + $v2 + $v3 + $v4 + $v5));\n" +
                "end";

        KieSession ksession = getKieSession(str);

        SimpleObject m1 = new SimpleObject("id", 1);
        SimpleObject m2 = new SimpleObject("id", 2);
        SimpleObject m3 = new SimpleObject("id", 3);
        SimpleObject m4 = new SimpleObject("id", 4);
        SimpleObject m5 = new SimpleObject("id", 5);
        ksession.insert(m1);
        ksession.insert(m2);
        ksession.insert(m3);
        ksession.insert(m4);
        ksession.insert(m5);

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo(15);
    }
}
