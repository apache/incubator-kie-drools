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
import java.util.HashMap;
import java.util.Map;

import org.drools.decisiontable.model.DRLOutput;
import org.drools.decisiontable.model.Package;
import org.drools.decisiontable.parser.DecisionTableParser;
import org.drools.decisiontable.parser.RuleSheetListener;
import org.drools.decisiontable.parser.xls.ExcelParser;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 * 
 * This class handles the input XLS and CSV and extracts the rule DRL, ready for
 * pumping into drools.
 */
public class SpreadsheetCompiler {

    /**
     * Generates DRL from the input stream containing the spreadsheet.
     * 
     * @param xlsStream
     *            The stream to the spreadsheet. Uses the first worksheet found
     *            for the decision tables, ignores others.
     * @return DRL xml, ready for use in drools.
     */
    public String compile(InputStream xlsStream,
                               InputType type) {
        RuleSheetListener listener = getRuleSheetListener( xlsStream,
                                                           type );
        Package rulePackage = listener.getRuleSet();
        DRLOutput out = new DRLOutput();
        rulePackage.renderDRL( out );
        return out.getDRL();
    }

    /**
     * Convenience implementation, taking rules from the classpath. It is
     * recommended to use the stream version, as you can then change rules
     * dynamically. (that is a lot of the benefit of rule engines !).
     * 
     * @param classPathResource
     *            full class path to the spreadsheet you wish to convert to DRL.
     *            Uses the first worksheet for the decision tables.
     * @return DRL.
     */
    public String compile(String classPathResource,
                               InputType inputType) {
        InputStream stream = this.getClass().getResourceAsStream( classPathResource );
        try {
            String drl = compile( stream,
                                       inputType );
            return drl;
        } finally {
            closeStream( stream );
        }
    }

    /**
     * Looks for a named worksheet to find the decision tables on. Only works
     * with XLS format spreadsheets (as they have multiple worksheets).
     * 
     * @param stream
     *            The stream of the decision tables (spreadsheet) IN XLS format !!
     * @param worksheetName
     *            The name of the worksheet that the decision tables live on.
     * @return DRL, ready to go.
     */
    public String compile(InputStream stream,
                               String worksheetName) {
        RuleSheetListener listener = getRuleSheetListener( stream,
                                                           worksheetName );
        Package rulePackage = listener.getRuleSet();
        DRLOutput out = new DRLOutput();
        rulePackage.renderDRL( out );
        return out.getDRL();
    }

    private RuleSheetListener getRuleSheetListener(InputStream stream,
                                                   InputType type) {
        RuleSheetListener listener = new RuleSheetListener();

        DecisionTableParser parser = type.createParser( listener );
        parser.parseFile( stream );
        return listener;
    }

    private RuleSheetListener getRuleSheetListener(InputStream stream,
                                                   String worksheetName) {
        RuleSheetListener listener = new RuleSheetListener();
        Map listeners = new HashMap();
        listeners.put( worksheetName,
                       listener );
        ExcelParser parser = new ExcelParser( listeners );
        parser.parseFile( stream );
        return listener;
    }

    private void closeStream(InputStream stream) {
        try {
            stream.close();
        } catch ( Exception e ) {
            System.err.print( "WARNING: Wasn't able to " + "correctly close stream for decision table. " + e.getMessage() );
        }
    }

}
