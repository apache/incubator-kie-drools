package org.drools.decisiontable;


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



import java.io.InputStream;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 * Some basic unit tests for converter utility.
 */
public class SpreadsheetCompilerUnitTest extends TestCase
{

    public void testLoadFromClassPath()
    {
        SpreadsheetCompiler converter = new SpreadsheetCompiler( );
        String drl = converter.compile( "/data/MultiSheetDST.xls", InputType.XLS );
        
        assertNotNull( drl );
        
        assertTrue(drl.indexOf( "rule \"How cool am I_12\"") > drl.indexOf( "rule \"How cool am I_11\"") );
        assertTrue(drl.indexOf("import example.model.User;") > -1);
        assertTrue(drl.indexOf("import example.model.Car;") > -1);
    }

    public void testLoadSpecificWorksheet()
    {
        SpreadsheetCompiler converter = new SpreadsheetCompiler( );
        InputStream stream = this.getClass( ).getResourceAsStream( "/data/MultiSheetDST.xls" );
        String drl = converter.compile( stream,
                                             "Another Sheet" );
        assertNotNull( drl );
    }
    
    public void testLoadCsv() {
        SpreadsheetCompiler converter = new SpreadsheetCompiler( );
        InputStream stream = this.getClass( ).getResourceAsStream( "/data/ComplexWorkbook.csv" );
        String drl = converter.compile( stream,
                                             InputType.CSV );
        assertNotNull( drl );
        assertTrue(drl.indexOf("myObject.setIsValid(1, 2)") > 0);
        assertTrue(drl.indexOf("myObject.size () > 50") > 0);
        //System.out.println(drl);
    }
    
    public void testLoadBasic() {
        SpreadsheetCompiler converter = new SpreadsheetCompiler( );
        InputStream stream = this.getClass( ).getResourceAsStream( "/data/BasicWorkbook.xls" );
        String drl = converter.compile( stream,
                                             InputType.XLS );
        
        assertNotNull( drl );
        assertTrue(drl.indexOf( "This is a function block" ) > -1);
        assertTrue(drl.indexOf( "global Class1 obj1;" ) > -1);
        
    }

}

