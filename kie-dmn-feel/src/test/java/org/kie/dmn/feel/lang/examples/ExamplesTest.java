/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.lang.examples;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.dmn.feel.lang.runtime.FEEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ExamplesTest {

    private static final Logger logger = LoggerFactory.getLogger( ExamplesTest.class );
    public static final String DEFAULT_IDENT = "    ";

    private static Map context;

    @BeforeClass
    public static void setupTest() {
        String expression = loadExpression( "example_10_6_1.feel" );
        context = (Map) FEEL.evaluate( expression );
    }

    @Test
    public void testLoadApplicantContext() {
        String expression = loadExpression( "applicant.feel" );
        Map applicant = (Map) FEEL.evaluate( expression );
        System.out.println( printContext( applicant ) );

        assertThat( applicant.size(), is( 5 ) );
    }

    @Test
    public void testLoadExample_10_6_1() {
        System.out.println( printContext( context ) );
        assertThat( context.size(), is( 6 ) );
    }

    @Test
    public void testLoadExample_10_6_2() {
        Number yearlyIncome = (Number) FEEL.evaluate( "monthly income * 12", context );

        System.out.println( "Yearly income = " + yearlyIncome );

        assertThat( yearlyIncome, is( new BigDecimal( "120000.00" ) ) );
    }

    @Test
    public void testLoadExample_10_6_3() {
        String expression = loadExpression( "example_10_6_3.feel" );

        String maritalStatus = (String) FEEL.evaluate( expression, context );

        System.out.println( "Marital status = " + maritalStatus );

        assertThat( maritalStatus, is( "valid" ) );
    }

    @Test
    public void testLoadExample_10_6_4() {
        Number totalExpenses = (Number) FEEL.evaluate( "sum( monthly outgoings )", context );

        System.out.println( "Monthly total expenses = " + totalExpenses );

        assertThat( totalExpenses, is( new BigDecimal( "5500.00" ) ) );
    }

    @Test
    public void testLoadExample_10_6_5() {
        String expression = loadExpression( "example_10_6_5.feel" );

        Number pmt = (Number) FEEL.evaluate( expression, context );

        System.out.println( "PMT = " + pmt );

        assertThat( pmt, is( new BigDecimal( "3975.982590125552338278440100112431" ) ) );
    }

    @Test
    public void testLoadExample_10_6_6() {
        String expression = loadExpression( "example_10_6_6.feel" );

        Number total = (Number) FEEL.evaluate( expression, context );

        System.out.println( "Weight = " + total );

        assertThat( total, is( new BigDecimal( "150" ) ) );
    }

    @Test
    public void testLoadExample_10_6_7() {
        String expression = loadExpression( "example_10_6_7.feel" );

        Boolean bankrupcy = (Boolean) FEEL.evaluate( expression, context );

        System.out.println( "Is there bankrupcy event? " + bankrupcy );

        assertThat( bankrupcy, is( Boolean.FALSE ) );
    }

    @Test
    public void testJavaCall() {
        String expression = loadExpression( "javacall.feel" );

        Map context = (Map) FEEL.evaluate( expression );

        System.out.println( printContext( context ) );
    }

    @Test
    public void testAdhocExpression() {
        String expression = loadExpression( "custom.feel" );

        Object result = FEEL.evaluate( expression );

        System.out.println( "Result: " + result );
    }

    private static String loadExpression(String fileName) {
        try {
            return new String( Files.readAllBytes( Paths.get( ExamplesTest.class.getResource( fileName ).toURI() ) ) );
        } catch ( Exception e ) {
            logger.error( "Error reading file " + fileName, e );
            Assert.fail("Error reading file "+fileName);
        }
        return null;
    }

    private String printContext( Map context ) {
        return printContext( context, "" );
    }

    private String printContext( Map<String, Object> context, String ident ) {
        StringBuilder builder = new StringBuilder(  );
        builder.append( "{\n" );
        for( Map.Entry e : context.entrySet() ) {
            builder.append( ident )
                    .append( DEFAULT_IDENT )
                    .append( e.getKey() )
                    .append( ": " );
            if( e.getValue() instanceof Map ) {
                builder.append( printContext( (Map<String, Object>) e.getValue(), ident + DEFAULT_IDENT ) );
            } else {
                builder.append( e.getValue() )
                        .append( "\n" );
            }
        }
        builder.append( ident+"}\n" );
        return builder.toString();
    }

}
