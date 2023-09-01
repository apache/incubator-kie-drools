package org.drools.verifier.doc;

import java.io.OutputStream;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.drools.verifier.misc.DrlPackageParser;
import org.drools.verifier.misc.DrlRuleParser;

import static org.drools.util.Config.getConfig;
import static org.drools.verifier.doc.DroolsDocsComponentFactory.*;

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

        try (PDDocument doc = new PDDocument()) {
            PDDocumentInformation info = new PDDocumentInformation();
            info.setTitle(packageData.getName().toUpperCase());
            doc.setDocumentInformation(info);

            createFirstPage(doc, currentDate, packageData);
            int pageNumber = 2;
            createToC(doc, pageNumber, packageData);
            for ( DrlRuleParser ruleData : packageData.getRules() ) {
                pageNumber++;
                createRulePage(doc, pageNumber, packageData.getName(), ruleData);
            }

            doc.save(out);
        } catch (Exception ex){
            System.err.println( ex.getMessage() );
        }
    }

    public static String getDateFormatMask() {
        String fmt = getConfig( "drools.dateformat" );
        if ( fmt == null ) {
            fmt = "dd-MMM-yyyy";
        }
        return fmt;
    }
}
