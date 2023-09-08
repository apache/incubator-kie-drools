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

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class HierarchyRulesTest extends BaseModelTest {

    public HierarchyRulesTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void test() {
        // DROOLS-7470
        String str =
                "rule R1 when \n" +
                "    $a : Boolean() from true\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R2 extends R1 when\n" +
                "    $b: Boolean() from ($a == true)\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R3 extends R2 when\n" +
                "    Boolean(this==false) from $b\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R4 extends R2 when\n" +
                "    Boolean(this==true) from $b\n" +
                "    $c: Boolean() from true\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R5 extends R4 when\n" +
                "    Boolean(this==true) from $c\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R6 extends R4 when\n" +
                "    Boolean(this==false) from $c\n" +
                "    $d: Boolean() from ($a == true)\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R7 extends R6 when\n" +
                "    Boolean(this==false) from $d\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R8 extends R6 when\n" +
                "    Boolean(this==true) from $d\n" +
                "    $data: Boolean() from true\n" +
                "    $f: Boolean() from true\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R9 extends R8 when\n" +
                "    Boolean(this==true) from $f\n" +
                "    $h : Boolean() from ($a == true)\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R11 extends R9 when\n" +
                "    Boolean(this==true) from $h\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R12 extends R9 when\n" +
                "    Boolean(this==false) from $h\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R10 extends R8 when\n" +
                "    Boolean(this==false) from $f\n" +
                "    $i: Boolean() from ($data == true)\n" +
                "    $g: Boolean() from true\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R13 extends R10 when\n" +
                "    Boolean(this==false) from $g\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( str );
        assertThat( ksession.fireAllRules() ).isEqualTo(4);
    }
}
