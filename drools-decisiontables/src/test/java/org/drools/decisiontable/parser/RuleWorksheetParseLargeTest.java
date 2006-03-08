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



import junit.framework.TestCase;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 * A special test for parsing a large workbook, to see how it scales.
 * 
 */
public class RuleWorksheetParseLargeTest extends TestCase
{

    private long startTimer;

    private long endTimer;

    /**
     * Tests parsing a large spreadsheet into an in memory ruleset. This doesn't
     * really do anything much at present. Takes a shed-load of memory to dump
     * out this much XML as a string, so really should think of using a stream
     * in some cases... (tried StringWriter, but is still in memory, so doesn't
     * help).
     * 
     * Stream to a temp file would work: return a stream from that file
     * (decorate FileInputStream such that when you close it, it deletes the
     * temp file).... must be other options.
     * 
     * @throws Exception
     */
    public void testLargeWorkSheetParseToRuleset() throws Exception
    {
//  Test removed until have streaming sorted in future. No one using Uber Tables just yet !        
//        InputStream stream = RuleWorksheetParseLargeTest.class.getResourceAsStream( "/data/VeryLargeWorkbook.xls" );
//
//        startTimer( );
//        RuleSheetListener listener = RuleWorksheetParseTest.getRuleSheetListener( stream );
//        stopTimer( );
//
//        System.out.println( "Time to parse large table : " + getTime( ) );
//        Ruleset ruleset = listener.getRuleSet( );
//        assertNotNull( ruleset );
        /*
         * System.out.println("Time taken for 20K rows parsed: " + getTime());
         * 
         * startTimer(); String xml = listener.getRuleSet().toXML();
         * stopTimer(); System.out.println("Time taken for rendering to XML: " +
         * getTime());
         */
    }

    private void startTimer()
    {
        startTimer = System.currentTimeMillis( );
    }

    private void stopTimer()
    {
        endTimer = System.currentTimeMillis( );
    }

    private long getTime()
    {
        return endTimer - startTimer;
    }

}

