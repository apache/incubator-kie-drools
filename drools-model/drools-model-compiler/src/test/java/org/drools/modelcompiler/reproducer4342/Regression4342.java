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

package org.drools.modelcompiler.reproducer4342;

import java.math.BigDecimal;

import org.drools.modelcompiler.BaseModelTest;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

public class Regression4342 extends BaseModelTest {

    public Regression4342(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Test
    public void testPropertyReactvity() {
        String str =
                "import java.lang.Number;\n" +
                        "import java.math.BigDecimal;\n" +
                        "import org.drools.modelcompiler.reproducer4342.Bill;" +
                        "import org.drools.modelcompiler.reproducer4342.Voucher;" +
                        "rule \"rule\"\n" +
                        "  dialect \"mvel\"\n" +
                        "  when\n" +
                        "    voucher : Voucher( )\n" +
                        "    sumBilledAmounts : Number( ) from accumulate ( bill : Bill( billedAmount != null ),\n" +
                        "      sum(bill.billedAmount)) \n" +
                        "  then\n" +
                        "    modify( voucher ) {\n" +
                        "        setTotal( BigDecimal.valueOf(sumBilledAmounts.doubleValue()) )\n" +
                        "    }\n" +
                        "end\n";

        KieSession ksession = getKieSession(str);

        Bill bill1 = new Bill(BigDecimal.valueOf(1000));
        ksession.insert(bill1);

        Bill bill2 = new Bill(BigDecimal.valueOf(2000));
        ksession.insert(bill2);

        Voucher vaucher = new Voucher();
        ksession.insert(vaucher);

        int rulesFired = ksession.fireAllRules();

        assertEquals(1, rulesFired);
        assertEquals(BigDecimal.valueOf(3000.0), vaucher.getTotal());
    }
}