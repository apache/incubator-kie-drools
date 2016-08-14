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
import org.junit.Test;
import org.kie.dmn.feel.lang.runtime.FEEL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

public class ExamplesTest {

    private static final Logger logger = LoggerFactory.getLogger( ExamplesTest.class );
    public static final String DEFAULT_IDENT = "    ";

    @Test
    public void testLoadApplicantContext() {
        String expression = loadExpression( "applicant.feel" );
        Map applicant = (Map) FEEL.evaluate( expression );
        System.out.println( printContext( applicant ) );
    }

    @Test
    public void testLoadExample_10_6_1() {
        String expression = loadExpression( "example_10_6_1.feel" );
        Map context = (Map) FEEL.evaluate( expression );
        System.out.println( printContext( context ) );
    }

    @Test
    public void testLoadExample_10_6_2() {
        String expression = loadExpression( "example_10_6_1.feel" );
        Map context = (Map) FEEL.evaluate( expression );
        Number yearlyIncome = (Number) FEEL.evaluate( "monthly income * 12", context );
        System.out.println( "Yearly income = " + yearlyIncome );
    }

    @Test
    public void testLoadExample_10_6_3() {
        String expression = loadExpression( "example_10_6_1.feel" );
        Map context = (Map) FEEL.evaluate( expression );
        String validMaritalStatus = (String) FEEL.evaluate( "if applicant.marital status in (\"M\",\"S\") then \"valid\" else \"not valid\"", context );
        System.out.println( "Marital status = " + validMaritalStatus );
    }

    private String loadExpression(String fileName) {
        try {
            return new String( Files.readAllBytes( Paths.get( getClass().getResource( fileName ).toURI() ) ) );
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
