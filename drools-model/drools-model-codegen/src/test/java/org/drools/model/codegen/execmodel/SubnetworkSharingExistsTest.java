/*
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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reproducer for NPE in BetaNode.getStartTuple() when two rules share
 * subnetwork nodes via exists() with overlapping patterns but different
 * subnetwork entry points.
 */
public class SubnetworkSharingExistsTest extends BaseModelTest {

    public static class TextField {

        private String name;
        private String value;

        public TextField(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * Rule 1 has field1 in the main path and exists(field3 && field2).
     * Rule 2 has exists(field1 && field3 && field2).
     * The overlapping subnetwork suffix (field3 && field2) causes the
     * TupleToObjectNodes to be incorrectly shared, leading to NPE in
     * BetaNode.getStartTuple() when searching the peer chain.
     */
    @ParameterizedTest
    @MethodSource("parameters")
    public void testExistsWithDifferentSubnetworkEntryPoints(RUN_TYPE runType) {
        String str =
                "import " + TextField.class.getCanonicalName() + ";\n" +
                "rule \"Rule 1\"\n" +
                "no-loop true\n" +
                "    when\n" +
                "        $outputField: TextField(name == \"outputField\")\n" +
                "        TextField(name == \"field1\" && value == \"A\")\n" +
                "        exists(\n" +
                "            TextField(name == \"field3\" && value == \"1\")\n" +
                "            && TextField(name == \"field2\" && value == \"20\")\n" +
                "        )\n" +
                "    then\n" +
                "    end\n" +
                "\n" +
                "rule \"Rule 2\"\n" +
                "no-loop true\n" +
                "    when\n" +
                "        $outputField: TextField(name == \"outputField\")\n" +
                "        exists(\n" +
                "            TextField(name == \"field1\" && value == \"A\")\n" +
                "            && TextField(name == \"field3\" && value == \"1\")\n" +
                "            && TextField(name == \"field2\" && value == \"20\")\n" +
                "        )\n" +
                "    then\n" +
                "    end\n";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new TextField("field1", "A"));
        ksession.insert(new TextField("field2", "20"));
        ksession.insert(new TextField("field3", "1"));
        ksession.insert(new TextField("outputField", ""));

        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(2);

        ksession.dispose();
    }
}
