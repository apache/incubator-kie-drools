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
package org.drools.model.codegen.execmodel.bigintegertest;

import java.math.BigInteger;

import org.drools.model.codegen.execmodel.BaseModelTest;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class BigIntegerTest extends BaseModelTest {

    public BigIntegerTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    public static class BiHolder {

        private BigInteger bi1;
        private BigInteger bi2;

        public BiHolder() {
            super();
        }

        public BiHolder(BigInteger bi1, BigInteger bi2) {
            super();
            this.bi1 = bi1;
            this.bi2 = bi2;
        }

        public BigInteger getBi1() {
            return bi1;
        }

        public void setBi1(BigInteger bi1) {
            this.bi1 = bi1;
        }

        public BigInteger getBi2() {
            return bi2;
        }

        public void setBi2(BigInteger bi2) {
            this.bi2 = bi2;
        }
    }

    @Test
    public void testBigIntegerLiteralLhsNegative() {
        // DROOLS-6596
        String str =
                "package org.drools.modelcompiler.bigintegerss\n" +
                     "import " + BiHolder.class.getCanonicalName() + ";\n" +
                     "rule R1 dialect \"mvel\" when\n" +
                     "    $holder : BiHolder(bi1 > -10I)\n" +
                     "then\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        BiHolder holder = new BiHolder();
        holder.setBi1(new BigInteger("10"));
        ksession.insert(holder);
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);
    }

    @Test
    public void testBigIntegerLiteralRhsNegative() {
        // DROOLS-6596
        String str =
                "package org.drools.modelcompiler.bigdecimals\n" +
                     "import " + BiHolder.class.getCanonicalName() + ";\n" +
                     "rule R1 dialect \"mvel\" when\n" +
                     "    $holder : BiHolder()\n" +
                     "then\n" +
                     "    $holder.bi1 = -10I;\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        BiHolder holder = new BiHolder();
        ksession.insert(holder);
        ksession.fireAllRules();

        assertThat(holder.getBi1()).isEqualTo(new BigInteger("-10"));
    }
}
