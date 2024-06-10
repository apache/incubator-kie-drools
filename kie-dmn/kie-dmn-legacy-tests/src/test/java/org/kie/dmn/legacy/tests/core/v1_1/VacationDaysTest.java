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
package org.kie.dmn.legacy.tests.core.v1_1;

import java.math.BigDecimal;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class VacationDaysTest extends BaseDMN1_1VariantTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void solutionCase1() {
        executeTest( 16, 1, 27 );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void solutionCase2() {
        executeTest( 25, 5, 22 );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void solutionCase3() {
        executeTest( 44, 20, 24 );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void solutionCase4() {
        executeTest( 44, 30, 30 );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void solutionCase5() {
        executeTest( 50, 20, 24 );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void solutionCase6() {
        executeTest( 50, 30, 30 );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("params")
    void solutionCase7() {
        executeTest( 60, 20, 30 );
    }

    private void executeTest(final int age, final int yearsService, final int expectedVacationDays ) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime("0020-vacation-days.dmn", this.getClass() );
        final DMNModel dmnModel = runtime.getModel("https://www.drools.org/kie-dmn", "0020-vacation-days" );
        assertThat(dmnModel).isNotNull();

        final DMNContext context = DMNFactory.newContext();

        context.set( "Age", age );
        context.set( "Years of Service", yearsService );

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context );

        final DMNContext result = dmnResult.getContext();

        assertThat(result.get("Total Vacation Days")).isEqualTo(BigDecimal.valueOf(expectedVacationDays));
    }
}

