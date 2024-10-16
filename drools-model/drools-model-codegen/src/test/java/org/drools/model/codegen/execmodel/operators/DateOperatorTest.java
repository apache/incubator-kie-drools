/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.operators;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.drools.model.codegen.execmodel.BaseModelTest2;
import org.drools.model.codegen.execmodel.domain.SimpleDateHolder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class DateOperatorTest extends BaseModelTest2 {

    @ParameterizedTest
	@MethodSource("parameters")
    public void dateBetweenGlobals(RUN_TYPE runType) throws ParseException {
        String str =
                "import " + SimpleDateHolder.class.getCanonicalName() + ";" +
                        "global java.util.Date $startDate;\n" +
                        "global java.util.Date $endDate;\n" +
                        "rule R when\n" +
                        "  SimpleDateHolder(date > $startDate && date < $endDate)\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        ksession.setGlobal("$startDate", sdf.parse("04/01/2019"));
        ksession.setGlobal("$endDate", sdf.parse("05/01/2019"));

        SimpleDateHolder holder = new SimpleDateHolder("A", sdf.parse("04/15/2019"));
        ksession.insert(holder);
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void dateBetweenVariables(RUN_TYPE runType) throws ParseException {
        String str =
                "import " + SimpleDateHolder.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  SimpleDateHolder(id == \"A\", $startDate : date)\n" +
                        "  SimpleDateHolder(id == \"B\", $endDate : date)\n" +
                        "  SimpleDateHolder(id == \"C\", date > $startDate && date < $endDate)\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        SimpleDateHolder holderA = new SimpleDateHolder("A", sdf.parse("04/01/2019"));
        SimpleDateHolder holderB = new SimpleDateHolder("B", sdf.parse("05/01/2019"));
        SimpleDateHolder holderC = new SimpleDateHolder("C", sdf.parse("04/15/2019"));

        ksession.insert(holderA);
        ksession.insert(holderB);
        ksession.insert(holderC);
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);
    }
}
