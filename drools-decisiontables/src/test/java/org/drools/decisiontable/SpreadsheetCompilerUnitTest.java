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

package org.drools.decisiontable;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.decisiontable.parser.RuleMatrixSheetListener;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 * Some basic unit tests for converter utility.
 * Note that some of this may still use the drools 2.x syntax, as it is not compiled,
 * only tested that it generates DRL in the correct structure (not that the DRL itself
 * is correct).
 */
public class SpreadsheetCompilerUnitTest {

    @Test
    public void testLoadFromClassPath() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile( "/data/MultiSheetDST.xls",
                                              InputType.XLS );

        assertNotNull( drl );

        assertTrue( drl.indexOf( "rule \"How cool am I_12\"" ) > drl.indexOf( "rule \"How cool am I_11\"" ) );
        assertTrue( drl.indexOf( "import example.model.User;" ) > -1 );
        assertTrue( drl.indexOf( "import example.model.Car;" ) > -1 );
        assertTrue( drl.indexOf("package ") > -1);
        InputStream ins = this.getClass().getResourceAsStream("/data/MultiSheetDST.xls");

        drl = converter.compile( false, ins,
                InputType.XLS );

        assertNotNull( drl );

        assertTrue( drl.indexOf( "rule \"How cool am I_12\"" ) > 0 );
        assertTrue( drl.indexOf( "import example.model.User;" ) > -1 );
        assertTrue( drl.indexOf( "import example.model.Car;" ) > -1 );
        assertTrue( drl.indexOf("package ") == -1);

    }

    @Test
    public void testLoadSpecificWorksheet() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final InputStream stream = this.getClass().getResourceAsStream( "/data/MultiSheetDST.xls" );
        final String drl = converter.compile( stream,
                                              "Another Sheet" );
        assertNotNull( drl );
    }

    @Test
    public void testLoadCustomListener() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final InputStream stream = this.getClass().getResourceAsStream( "/data/CustomWorkbook.xls" );
        final String drl = converter.compile( stream,
                                              InputType.XLS,
                                              new RuleMatrixSheetListener() );
        assertNotNull( drl );
        assertTrue( drl.indexOf( "\"matrix\"" ) != -1 );
        assertTrue( drl.indexOf( "$v : FundVisibility" ) != -1 );
        assertTrue( drl.indexOf( "FundType" ) != -1 );
        assertTrue( drl.indexOf( "Role" ) != -1 );
    }

    @Test
    public void testLoadCsv() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final InputStream stream = this.getClass().getResourceAsStream( "/data/ComplexWorkbook.csv" );
        final String drl = converter.compile( stream,
                                              InputType.CSV );
        assertNotNull( drl );

//        System.out.println( drl );

        assertTrue( drl.indexOf( "myObject.setIsValid(1, 2)" ) > 0 );
        assertTrue( drl.indexOf( "myObject.size () > 50" ) > 0 );

        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red), myObject.size () > 1)" ) > 0 );
    }

    @Test
    public void testLoadBasicWithMergedCells() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        final InputStream stream = this.getClass().getResourceAsStream( "/data/BasicWorkbook.xls" );
        final String drl = converter.compile( stream,
                                              InputType.XLS );

        assertNotNull( drl );

        System.out.println(drl);
        Pattern p = Pattern.compile( ".*setIsValid\\(Y\\).*setIsValid\\(Y\\).*setIsValid\\(Y\\).*",
                                     Pattern.DOTALL | Pattern.MULTILINE );
        Matcher m = p.matcher( drl );
        assertTrue( m.matches() );

        assertTrue( drl.indexOf( "This is a function block" ) > -1 );
        assertTrue( drl.indexOf( "global Class1 obj1;" ) > -1 );
        assertTrue( drl.indexOf( "myObject.setIsValid(10-Jul-1974)" ) > -1 );
        assertTrue( drl.indexOf( "myObject.getColour().equals(blue)" ) > -1 );
        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red), myObject.size () > 12\\\")" ) > -1 );

        assertTrue( drl.indexOf( "b: Bar() eval(myObject.size() < 3)" ) > -1 );
        assertTrue( drl.indexOf( "b: Bar() eval(myObject.size() < 9)" ) > -1 );

        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red), myObject.size () > 1)" ) < drl.indexOf( "b: Bar() eval(myObject.size() < 3)" ) );


        assertTrue( drl.indexOf( "myObject.setIsValid(\"19-Jul-1992\")" ) > -1 );

    }

}
