/**
 * Copyright 2010 JBoss Inc
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

package org.drools.doc;

import java.io.OutputStream;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.drools.verifier.misc.DrlPackageParser;
import org.drools.verifier.misc.DrlRuleParser;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 * @author Toni Rikkola
 *
 */
public class DroolsDocsBuilder {

    protected final String currentDate = getFormatter().format( new Date() );

    protected static Format getFormatter() {
        return new SimpleDateFormat( getDateFormatMask() );
    }

    private final DrlPackageParser packageData;

    public DroolsDocsBuilder(String packageDrl) throws ParseException {
        this.packageData = DrlPackageParser.findPackageDataFromDrl( packageDrl );
    }

    protected DroolsDocsBuilder(DrlPackageParser packageData) {
        this.packageData = packageData;
    }

    public static DroolsDocsBuilder getInstance(String packageDrl) throws ParseException {
        return new DroolsDocsBuilder( packageDrl );
    }

    public static DroolsDocsBuilder getInstance(DrlPackageParser packageData) {
        return new DroolsDocsBuilder( packageData );
    }

    public void writePDF(OutputStream out) {

        // TODO: Use i18n!

        Document document = new Document();

        try {
            PdfWriter.getInstance( document,
                                   out );

            HeaderFooter footer = DroolsDocsComponentFactory.createFooter( packageData.getName() );

            document.setFooter( footer );

            document.addTitle( packageData.getName().toUpperCase() );
            document.open();

            // First page, documentation info.            
            DroolsDocsComponentFactory.createFirstPage( document,
                                                        currentDate,
                                                        packageData );

            document.newPage();

            // List index of the rules            
            document.add( new Phrase( "Table of Contents" ) );
            document.add( DroolsDocsComponentFactory.createContents( packageData.getRules() ) );

            document.newPage();

            for ( DrlRuleParser ruleData : packageData.getRules() ) {
                DroolsDocsComponentFactory.newRulePage( document,
                                                        packageData.getName(),
                                                        ruleData );
            }

        } catch ( DocumentException de ) {
            System.err.println( de.getMessage() );
        }

        document.close();
    }

    public static String getDateFormatMask() {
        String fmt = System.getProperty( "drools.dateformat" );
        if ( fmt == null ) {
            fmt = "dd-MMM-yyyy";
        }
        return fmt;
    }
}
