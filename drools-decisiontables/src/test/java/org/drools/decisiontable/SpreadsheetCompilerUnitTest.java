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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.decisiontable.parser.RuleMatrixSheetListener;
import org.junit.Test;

/**
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

    @Test
    public void testDeclaresXLS() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile( "DeclaresWorkbook.xls",
                                        InputType.XLS );

        assertNotNull( drl );
        
        assertTrue( drl.indexOf( "declare Smurf name : String end" ) > -1 );
    }
    
    @Test
    public void testDeclaresCSV() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile( "DeclaresWorkbook.csv",
                                        InputType.CSV );
        
        assertNotNull( drl );
        
        assertTrue( drl.indexOf( "declare Smurf name : String end" ) > -1 );
    }

    @Test
    public void testAttributesXLS() {
        final SpreadsheetCompiler converter = new SpreadsheetCompiler();
        String drl = converter.compile( "Attributes.xls",
                                        InputType.XLS );

        assertNotNull( drl );

        int rule1 = drl.indexOf( "rule \"N1\"" );
        assertFalse( rule1 == -1 );

        assertTrue( drl.indexOf( "no-loop true",
                                 rule1 ) > -1 );
        assertTrue( drl.indexOf( "duration 100",
                                 rule1 ) > -1 );
        assertTrue( drl.indexOf( "salience 1",
                                 rule1 ) > -1 );
        assertTrue( drl.indexOf( "ruleflow-group \"RFG1\"",
                                 rule1 ) > -1 );
        assertTrue( drl.indexOf( "agenda-group \"AG1\"",
                                 rule1 ) > -1 );
        assertTrue( drl.indexOf( "timer (T1)",
                                 rule1 ) > -1 );
        assertTrue( drl.indexOf( "lock-on-active true",
                                 rule1 ) > -1 );
        assertTrue( drl.indexOf( "activation-group \"g1\"",
                                 rule1 ) > -1 );
        assertTrue( drl.indexOf( "auto-focus true",
                                 rule1 ) > -1 );
        assertTrue( drl.indexOf( "calendars \"CAL1\"",
                                 rule1 ) > -1 );

        int rule2 = drl.indexOf( "rule \"N2\"" );
        assertFalse( rule2 == -1 );

        assertTrue( drl.indexOf( "no-loop false",
                                 rule2 ) > -1 );
        assertTrue( drl.indexOf( "duration 200",
                                 rule2 ) > -1 );
        assertTrue( drl.indexOf( "salience 2",
                                 rule2 ) > -1 );
        assertTrue( drl.indexOf( "ruleflow-group \"RFG2\"",
                                 rule2 ) > -1 );
        assertTrue( drl.indexOf( "agenda-group \"AG2\"",
                                 rule2 ) > -1 );
        assertTrue( drl.indexOf( "timer (T2)",
                                 rule2 ) > -1 );
        assertTrue( drl.indexOf( "lock-on-active false",
                                 rule2 ) > -1 );
        assertTrue( drl.indexOf( "activation-group \"g2\"",
                                 rule2 ) > -1 );
        assertTrue( drl.indexOf( "auto-focus false",
                                 rule2 ) > -1 );
        assertTrue( drl.indexOf( "calendars \"CAL2\"",
                                 rule2 ) > -1 );
    }
    
}
