/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core;

import org.junit.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.*;
import org.kie.dmn.core.util.DMNRuntimeUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class VacationDaysTest {

    @Test
    public void testSolutionCase1() {
        executeTest( 16, 1, 27 );
    }

    @Test
    public void testSolutionCase2() {
        executeTest( 25, 5, 22 );
    }

    @Test
    public void testSolutionCase3() {
        executeTest( 44, 20, 24 );
    }

    @Test
    public void testSolutionCase4() {
        executeTest( 44, 30, 30 );
    }

    @Test
    public void testSolutionCase5() {
        executeTest( 50, 20, 24 );
    }

    @Test
    public void testSolutionCase6() {
        executeTest( 50, 30, 30 );
    }

    @Test
    public void testSolutionCase7() {
        executeTest( 60, 20, 30 );
    }

    private void executeTest( int age, int yearsService, int expectedVacationDays ) {
        DMNRuntime runtime = DMNRuntimeUtil.createRuntime( "0020-vacation-days.dmn", this.getClass() );
        DMNModel dmnModel = runtime.getModel( "https://www.drools.org/kie-dmn", "0020-vacation-days" );
        assertThat( dmnModel, notNullValue() );

        DMNContext context = DMNFactory.newContext();

        context.set( "Age", age );
        context.set( "Years of Service", yearsService );

        DMNResult dmnResult = runtime.evaluateAll( dmnModel, context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Total Vacation Days" ), is( BigDecimal.valueOf( expectedVacationDays ) ) );
    }
}

