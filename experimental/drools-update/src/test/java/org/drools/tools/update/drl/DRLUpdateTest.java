/*
 * Copyright 2006 JBoss Inc
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
 *
 * Created on Jun 13, 2007
 */
package org.drools.tools.update.drl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import junit.framework.TestCase;

import org.drools.compiler.DroolsParserException;

/**
 * @author etirelli
 *
 */
public class DRLUpdateTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test method for {@link org.drools.tools.update.drl.DRLUpdate#updateDrl(java.io.Reader, java.io.Writer)}.
     * @throws DroolsParserException 
     * @throws IOException 
     */
    public void testUpdateDrl() throws DroolsParserException, IOException {
        Reader reader = new InputStreamReader( this.getClass().getResourceAsStream( "test_assert_modify_replacement_v3.drl" ));
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        Writer writer = new PrintWriter( result );
        
        DRLUpdate updater = new DRLUpdate();
        
        updater.updateDrl( reader, writer );
        reader.close();
        writer.close();
        
        reader = new InputStreamReader( this.getClass().getResourceAsStream( "test_assert_modify_replacement_v4.drl" ));
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        writer = new PrintWriter( expected );
        char[] bytebuf = new char[1024];
        int r = 0;
        while( ( r = reader.read( bytebuf )) > 0 ) {
            writer.write( bytebuf, 0, r );
        }
        writer.close();
        reader.close();
        
        assertEqualsIgnoreWhiteSpaces( expected.toString(), result.toString() );
    }
    
    public void assertEqualsIgnoreWhiteSpaces( String expected, String result ) {
        expected = expected.replaceAll( "\\s+", "" );
        result = result.replaceAll( "\\s+", "" );
        
//        System.out.println("["+expected+"]");
//        System.out.println("["+result+"]");
        assertEquals( expected, result );
    }

}
