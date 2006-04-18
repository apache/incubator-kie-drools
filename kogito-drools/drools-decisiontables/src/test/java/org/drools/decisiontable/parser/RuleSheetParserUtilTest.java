package org.drools.decisiontable.parser;
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








import java.util.List;

import junit.framework.TestCase;

import org.drools.decisiontable.model.Import;
import org.drools.decisiontable.model.Global;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 * Nuff said...
 */
public class RuleSheetParserUtilTest extends TestCase
{

    public void testRuleName()
    {
        String row = "  RuleTable       This is my rule name";
        String result = RuleSheetParserUtil.getRuleName( row );
        assertEquals( "This is my rule name",
                      result );
    }
    
    /**
     * This is hear as the old way was to do this.
     */
    public void testInvalidRuleName() {
        String row = "RuleTable       This is my rule name (type class)";
        try {
        	String result = RuleSheetParserUtil.getRuleName( row );
        	fail("should have failed, but get result: " + result);
        } catch (IllegalArgumentException e) {
        	assertNotNull(e.getMessage());
        }
    }



    public void testIsStringMeaningTrue()
    {
        assertTrue( RuleSheetParserUtil.isStringMeaningTrue( "true" ) );
        assertTrue( RuleSheetParserUtil.isStringMeaningTrue( "TRUE" ) );
        assertTrue( RuleSheetParserUtil.isStringMeaningTrue( "yes" ) );
        assertTrue( RuleSheetParserUtil.isStringMeaningTrue( "oN" ) );

        assertFalse( RuleSheetParserUtil.isStringMeaningTrue( "no" ) );
        assertFalse( RuleSheetParserUtil.isStringMeaningTrue( "false" ) );
        assertFalse( RuleSheetParserUtil.isStringMeaningTrue( null ) );
    }

    public void testListImports()
    {
        String cellVal = null;
        List list = RuleSheetParserUtil.getImportList( cellVal );
        assertNotNull( list );
        assertEquals( 0,
                      list.size( ) );

        assertEquals( 0,
                      RuleSheetParserUtil.getImportList( "" ).size( ) );

        cellVal = "com.something.Yeah, com.something.No,com.something.yeah.*";
        list = RuleSheetParserUtil.getImportList( cellVal );
        assertEquals( 3,
                      list.size( ) );
        assertEquals( "com.something.Yeah",
                      ((Import) list.get( 0 )).getClassName( ) );
        assertEquals( "com.something.No",
                      ((Import) list.get( 1 )).getClassName( ) );
        assertEquals( "com.something.yeah.*",
                      ((Import) list.get( 2 )).getClassName( ) );
    }
    
    public void testListVariables() 
    {
        List varList = RuleSheetParserUtil.getVariableList("Var1 var1, Var2 var2,Var3 var3");
        assertNotNull(varList);
        assertEquals(3, varList.size());
        Global var = (Global) varList.get(0);
        assertEquals("Var1", var.getClassName());
        var = (Global) varList.get(2);
        assertEquals("Var3", var.getClassName());
        assertEquals("var3", var.getIdentifier());
    }
    
    public void testBadVariableFormat() {
        String bad = "class1, object2";
        try {
            RuleSheetParserUtil.getVariableList( bad );
            fail("should not work");
        } catch (DecisionTableParseException e) {
            assertNotNull(e.getMessage());
        }
    }
}