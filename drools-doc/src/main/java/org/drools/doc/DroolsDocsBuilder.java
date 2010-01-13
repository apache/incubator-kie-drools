package org.drools.doc;

import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.drools.verifier.misc.DrlPackageParser;
import org.drools.verifier.misc.DrlRuleParser;


import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
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

    protected DroolsDocsBuilder(String packageDrl) {
        this.packageData = DrlPackageParser.findPackageDataFromDrl( packageDrl );
    }

    protected DroolsDocsBuilder(DrlPackageParser packageData) {
        this.packageData = packageData;
    }

    public static DroolsDocsBuilder getInstance(String packageDrl) {
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
