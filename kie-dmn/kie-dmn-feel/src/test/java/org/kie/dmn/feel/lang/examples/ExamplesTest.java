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
package org.kie.dmn.feel.lang.examples;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.impl.FEELBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest
        extends ExamplesBaseTest {

    private static final Logger logger        = LoggerFactory.getLogger( ExamplesTest.class );

    private static Map  context;
    private static FEEL feel;

    @BeforeAll
    static void setupTest() {
        String expression = loadExpression( "example_10_6_1.feel" );
        feel = FEELBuilder.builder().build();
        context = (Map) feel.evaluate( expression );
    }

    @Test
    void loadApplicantContext() {
        String expression = loadExpression( "applicant.feel" );
        Map applicant = (Map) feel.evaluate( expression );
        System.out.println( printContext( applicant ) );

        assertThat(applicant).hasSize(5);
    }

    @Test
    void loadExample1061() {
        System.out.println( printContext( context ) );
        assertThat(context).hasSize(6);
    }

    @Test
    void loadExample1062() {
        Number yearlyIncome = (Number) feel.evaluate( "monthly income * 12", context );

        System.out.println( "Yearly income = " + yearlyIncome );

        assertThat(yearlyIncome).isEqualTo(new BigDecimal( "120000.00" ) );
    }

    @Test
    void loadExample1063() {
        String expression = loadExpression( "example_10_6_3.feel" );

        String maritalStatus = (String) feel.evaluate( expression, context );

        System.out.println( "Marital status = " + maritalStatus );

        assertThat(maritalStatus).isEqualTo("valid" );
    }

    @Test
    void loadExample1064() {
        Number totalExpenses = (Number) feel.evaluate( "sum( monthly outgoings )", context );

        System.out.println( "Monthly total expenses = " + totalExpenses );

        assertThat(totalExpenses).isEqualTo(new BigDecimal( "5500.00" ) );
    }

    @Test
    void loadExample1065() {
        String expression = loadExpression( "example_10_6_5.feel" );

        Number pmt = (Number) feel.evaluate( expression, context );

        System.out.println( "PMT = " + pmt );

        assertThat(pmt).isEqualTo(new BigDecimal( "3975.982590125552338278440100112431" ) );
    }

    @Test
    void loadExample1066() {
        String expression = loadExpression( "example_10_6_6.feel" );

        Number total = (Number) feel.evaluate( expression, context );

        System.out.println( "Weight = " + total );

        assertThat(total).isEqualTo(new BigDecimal( "150" ) );
    }

    @Test
    void loadExample1067() {
        String expression = loadExpression( "example_10_6_7.feel" );

        Boolean bankrupcy = (Boolean) feel.evaluate( expression, context );

        System.out.println( "Is there bankrupcy event? " + bankrupcy );

        assertThat(bankrupcy).isFalse();
    }

    @Test
    void javaCall() {
        String expression = loadExpression( "javacall.feel" );

        Map context = (Map) feel.evaluate( expression );

        System.out.println( printContext( context ) );
    }

    @Test
    void adhocExpression() {
        String expression = loadExpression( "custom.feel" );

        Object result = feel.evaluate( expression );

        if ( result instanceof Map ) {
            System.out.println( printContext( (Map) result ) );
        } else {
            System.out.println( "Result: " + result );
        }
    }
    


}
