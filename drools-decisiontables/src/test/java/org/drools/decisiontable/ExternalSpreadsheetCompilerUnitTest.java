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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *         basic unit tests for converter utility. Note that some of this may
 *         still use the drools 2.x syntax, as it is not compiled, only tested
 *         that it generates DRL in the correct structure (not that the DRL
 *         itself is correct).
 */
public class ExternalSpreadsheetCompilerUnitTest {
    @Test
    public void testLoadFromClassPath() {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final String drl = converter.compile( "/data/MultiSheetDST.xls",
                                              "/templates/test_template1.drl",
                                              11,
                                              2 );
        assertNotNull( drl );

        // System.out.println(drl);

        assertTrue( drl.indexOf( "rule \"How cool is Shaun 12\"" ) > 0 );
        assertTrue( drl.indexOf( "rule \"How cool is Kumar 11\"" ) > 0 );
        assertTrue( drl.indexOf( "import example.model.User;" ) > -1 );
        assertTrue( drl.indexOf( "import example.model.Car;" ) > -1 );
    }

    @Test
    public void testLoadSpecificWorksheet() {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final String drl = converter.compile( "/data/MultiSheetDST.xls",
                                              "Another Sheet",
                                              "/templates/test_template1.drl",
                                              11,
                                              2 );
        // System.out.println(drl);
        assertNotNull( drl );
    }

    @Test
    public void testLoadCsv() {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final String drl = converter.compile( "/data/ComplexWorkbook.csv",
                                              "/templates/test_template2.drl",
                                              InputType.CSV,
                                              10,
                                              2 );
        assertNotNull( drl );

        assertTrue( drl.indexOf( "myObject.setIsValid(1, 2)" ) > 0 );
        assertTrue( drl.indexOf( "myObject.size () > 2" ) > 0 );

        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red),\n        myObject.size () > 1" ) > 0 );
    }

    @Test
    public void testLoadBasicWithMergedCells() {
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final String drl = converter.compile( "/data/BasicWorkbook.xls",
                                              "/templates/test_template3.drl",
                                              InputType.XLS,
                                              10,
                                              2 );

        final String drl1 = converter.compile( "/data/BasicWorkbook.xls",
                                               "/templates/test_template3.drl",
                                               InputType.XLS,
                                               21,
                                               2 );

        assertNotNull( drl );

        Pattern p = Pattern.compile( ".*setIsValid\\(Y\\).*setIsValid\\(Y\\).*setIsValid\\(Y\\).*",
                                     Pattern.DOTALL | Pattern.MULTILINE );
        Matcher m = p.matcher( drl );
        assertTrue( m.matches() );

        assertTrue( drl.indexOf( "This is a function block" ) > -1 );
        assertTrue( drl.indexOf( "global Class1 obj1;" ) > -1 );
        assertTrue( drl1.indexOf( "myObject.setIsValid(10-Jul-1974)" ) > -1 );
        assertTrue( drl.indexOf( "myObject.getColour().equals(blue)" ) > -1 );
        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red), myObject.size() > 12\")" ) > -1 );

        assertTrue( drl.indexOf( "b: Bar()\n        eval(myObject.size() < 3)" ) > -1 );
        assertTrue( drl.indexOf( "b: Bar()\n        eval(myObject.size() < 9)" ) > -1 );

        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red), myObject.size() > 1)" ) < drl.indexOf( "b: Bar()\n        eval(myObject.size() < 3)" ) );

    }

    @Test
    public void testLoadBasicWithExtraCells() {
        final ExternalSpreadsheetCompiler compiler = new ExternalSpreadsheetCompiler();
        final String drl = compiler.compile( "/data/BasicWorkbook.xls",
                                             "/templates/test_template4.drl",
                                             InputType.XLS,
                                             10,
                                             2 );
        assertNotNull( drl );

        assertTrue( drl.indexOf( "This is a function block" ) > -1 );
        assertTrue( drl.indexOf( "global Class1 obj1;" ) > -1 );
        assertTrue( drl.indexOf( "myObject.getColour().equals(blue)" ) > -1 );
        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red), myObject.size() > 12\")" ) > -1 );

        assertTrue( drl.indexOf( "b: Bar()\n        eval(myObject.size() < 3)" ) > -1 );
        assertTrue( drl.indexOf( "b: Bar()\n        eval(myObject.size() < 9)" ) > -1 );

        assertTrue( drl.indexOf( "Foo(myObject.getColour().equals(red), myObject.size() > 1)" ) < drl.indexOf( "b: Bar()\n        eval(myObject.size() < 3)" ) );
    }

}
