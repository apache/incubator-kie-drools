package org.drools.decisiontable.parser;


/*
 * Copyright 2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */



import java.util.List;

import junit.framework.TestCase;

import org.drools.decisiontable.model.Import;
import org.drools.decisiontable.model.Variable;

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
        Variable var = (Variable) varList.get(0);
        assertEquals("Var1", var.getClassName());
        var = (Variable) varList.get(2);
        assertEquals("Var3", var.getClassName());
        assertEquals("var3", var.getIdentifier());
    }
}

