package org.drools.verifier.report.html;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

import org.drools.verifier.components.Field;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.VerifierReportWriter;
import org.drools.verifier.report.components.Severity;

public class HTMLReportWriter extends ReportModeller
    implements
    VerifierReportWriter {

    public void writeReport(OutputStream out,
                            VerifierReport result) throws IOException {

        zout = new ZipOutputStream( out );

        VerifierData data = result.getVerifierData();

        // Base files
        // index.htm
        writeToFile( UrlFactory.SOURCE_FOLDER + File.separator + UrlFactory.HTML_FILE_INDEX,
                     formPage( UrlFactory.THIS_FOLDER,
                               ComponentsReportVisitor.visitObjectTypeCollection( UrlFactory.THIS_FOLDER,
                                                                                  data.<ObjectType> getAll( VerifierComponentType.OBJECT_TYPE ) ) ) );

        // packages.htm
        writeToFile( UrlFactory.SOURCE_FOLDER + File.separator + UrlFactory.HTML_FILE_PACKAGES,
                     formPage( UrlFactory.THIS_FOLDER,
                               ComponentsReportVisitor.visitRulePackageCollection( UrlFactory.THIS_FOLDER,
                                                                                   data.<RulePackage> getAll( VerifierComponentType.RULE_PACKAGE ) ) ) );

        // Rules
        String ruleFolder = UrlFactory.SOURCE_FOLDER + File.separator + UrlFactory.RULE_FOLDER;
        for ( VerifierRule rule : data.<VerifierRule> getAll( VerifierComponentType.RULE ) ) {
            writeToFile( ruleFolder + File.separator + rule.getPath() + ".htm",
                         formPage( UrlFactory.PREVIOUS_FOLDER,
                                   ComponentsReportVisitor.visitRule( UrlFactory.PREVIOUS_FOLDER,
                                                                      rule,
                                                                      data ) ) );
        }

        // ObjectTypes
        String objectTypeFolder = UrlFactory.SOURCE_FOLDER + File.separator + UrlFactory.OBJECT_TYPE_FOLDER;
        for ( ObjectType objectType : data.<ObjectType> getAll( VerifierComponentType.OBJECT_TYPE ) ) {
            writeToFile( objectTypeFolder + File.separator + objectType.getPath() + ".htm",
                         formPage( UrlFactory.PREVIOUS_FOLDER,
                                   ComponentsReportVisitor.visitObjectType( UrlFactory.PREVIOUS_FOLDER,
                                                                            objectType,
                                                                            data ) ) );
        }

        // Fields
        String fieldFolder = UrlFactory.SOURCE_FOLDER + File.separator + UrlFactory.FIELD_FOLDER;
        for ( Field field : data.<Field> getAll( VerifierComponentType.FIELD ) ) {
            writeToFile( fieldFolder + File.separator + field.getPath() + ".htm",
                         formPage( UrlFactory.PREVIOUS_FOLDER,
                                   ComponentsReportVisitor.visitField( UrlFactory.PREVIOUS_FOLDER,
                                                                       field,
                                                                       result ) ) );
        }

        // Verifier messages
        writeMessages( result );

        // css files
        String cssFolder = UrlFactory.SOURCE_FOLDER + File.separator + UrlFactory.CSS_FOLDER;
        writeToFile( cssFolder + File.separator + UrlFactory.CSS_BASIC,
                     ComponentsReportVisitor.getCss( UrlFactory.CSS_BASIC ) );

        // Image files
        String imagesFolder = UrlFactory.SOURCE_FOLDER + File.separator + UrlFactory.IMAGES_FOLDER;

        copyFile( imagesFolder,
                  "hdrlogo_drools50px.gif" );
        copyFile( imagesFolder,
                  "jbossrules_hdrbkg_blue.gif" );

        zout.close();
    }

    private void writeMessages(VerifierReport result) throws IOException {
        VerifierData data = result.getVerifierData();

        String errors = VerifierMessagesVisitor.visitVerifierMessagesCollection( Severity.ERROR.getTuple(),
                                                                                 result.getBySeverity( Severity.ERROR ),
                                                                                 data );
        String warnings = VerifierMessagesVisitor.visitVerifierMessagesCollection( Severity.WARNING.getTuple(),
                                                                                   result.getBySeverity( Severity.WARNING ),
                                                                                   data );
        String notes = VerifierMessagesVisitor.visitVerifierMessagesCollection( Severity.NOTE.getTuple(),
                                                                                result.getBySeverity( Severity.NOTE ),
                                                                                data );

        writeToFile( UrlFactory.SOURCE_FOLDER + File.separator + UrlFactory.HTML_FILE_VERIFIER_MESSAGES,
                     formPage( UrlFactory.THIS_FOLDER,
                               errors + warnings + notes ) );
    }
}
