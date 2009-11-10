package org.drools.doc;

import java.io.OutputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    private final DrlPackageData packageData;

    protected DroolsDocsBuilder(String packageDrl) {
        this.packageData = DrlPackageData.findPackageDataFromDrl( packageDrl );
    }

    protected DroolsDocsBuilder(DrlPackageData packageData) {
        this.packageData = packageData;
    }

    public static DroolsDocsBuilder getInstance(String packageDrl) {
        return new DroolsDocsBuilder( packageDrl );
    }

    public static DroolsDocsBuilder getInstance(DrlPackageData packageData) {
        return new DroolsDocsBuilder( packageData );
    }

    public void writePDF(OutputStream out) {

        // TODO: Use i18n!

        Document document = new Document();

        try {
            PdfWriter.getInstance( document,
                                   out );

            HeaderFooter footer = DroolsDocsComponentFactory.createFooter( packageData.packageName );

            document.setFooter( footer );

            document.addTitle( packageData.packageName.toUpperCase() );
            document.open();

            // First page, documentation info.            
            DroolsDocsComponentFactory.createFirstPage( document,
                                                        currentDate,
                                                        packageData );

            document.newPage();

            // List index of the rules            
            document.add( new Phrase( "Table of Contents" ) );
            document.add( DroolsDocsComponentFactory.createContents( packageData.rules ) );

            document.newPage();

            for ( DrlRuleData ruleData : packageData.rules ) {
                DroolsDocsComponentFactory.newRulePage( document,
                                                           packageData.packageName,
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
