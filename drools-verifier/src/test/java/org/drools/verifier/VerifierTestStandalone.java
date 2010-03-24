package org.drools.verifier;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.verifier.builder.VerifierBuilderFactory;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.VerifierReportWriter;
import org.drools.verifier.report.VerifierReportWriterFactory;
import org.drools.verifier.report.components.Cause;
import org.drools.verifier.report.components.Severity;
import org.drools.verifier.report.components.VerifierMessage;
import org.drools.verifier.report.components.VerifierMessageBase;
import org.drools.verifier.report.components.VerifierRangeCheckMessage;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
class VerifierTestStandalone {

    public static final void main(String[] args) {
        try {

            Collection<String> fileNames = new ArrayList<String>();

            // Test data
            // fileNames.add("MissingRangesForDates.drl");
            // fileNames.add("MissingRangesForDoubles.drl");
            // fileNames.add("MissingRangesForInts.drl");
            // fileNames.add("MissingRangesForVariables.drl");
            // fileNames.add("Misc.drl");
            // fileNames.add("Misc2.drl");
            // fileNames.add("Misc3.drl");
            fileNames.add( "Enums.drl" );
            // fileNames.add("ConsequenceTest.drl");
            // fileNames.add("optimisation/OptimisationRestrictionOrderTest.drl");
            // fileNames.add("optimisation/OptimisationPatternOrderTest.drl");

            Verifier verifier = VerifierBuilderFactory.newVerifierBuilder().newVerifier();

            for ( String s : fileNames ) {
                verifier.addResourcesToVerify( ResourceFactory.newClassPathResource( s,
                                                                                     Verifier.class ),
                                               ResourceType.DRL );
            }

            verifier.fireAnalysis();
            // System.out.print(a.getResultAsPlainText());
            // System.out.print(a.getResultAsXML());
            // a.writeComponentsHTML("/stash/");
            // a.writeComponentsHTML("/Users/michaelneale/foo.html");
            //			a.writeComponentsHTML("/home/trikkola/");
            // a.writeComponentsHTML("c:/");

            VerifierReport result = verifier.getResult();

            VerifierReportWriter reportwriter = VerifierReportWriterFactory.newHTMLReportWriter();
            FileOutputStream out = new FileOutputStream( "/Users/rikkola/Desktop/testReport.zip" );

            reportwriter.writeReport( out,
                                      result );

            Collection<VerifierMessageBase> msgs = result.getBySeverity( Severity.ERROR );

            for ( Iterator iterator = msgs.iterator(); iterator.hasNext(); ) {
                VerifierMessageBase msg = (VerifierMessageBase) iterator.next();
                System.out.println( "ERR: " + msg.getMessage() );
            }

            msgs = result.getBySeverity( Severity.WARNING );
            for ( Iterator iterator = msgs.iterator(); iterator.hasNext(); ) {
                VerifierMessageBase msg = (VerifierMessageBase) iterator.next();
                System.out.println( "WARN (" + msg.getClass().getSimpleName() + "): " + msg.getMessage() );
                System.out.println( "\t FAULT: [" + msg.getClass().getSimpleName() + "] " + msg.getFaulty() );
                if ( msg instanceof VerifierMessage ) {
                    System.out.println( "\t CAUSES (message):" );
                    VerifierMessage amsg = (VerifierMessage) msg;
                    for ( Iterator iterator2 = amsg.getCauses().iterator(); iterator2.hasNext(); ) {
                        Cause c = (Cause) iterator2.next();
                        System.out.println( "\t\t [" + c.getClass().getSimpleName() + "]" + c );

                    }

                } else if ( msg instanceof VerifierRangeCheckMessage ) {
                    System.out.println( "\t CAUSES (range):" );
                    VerifierRangeCheckMessage amsg = (VerifierRangeCheckMessage) msg;
                    for ( Iterator iterator2 = amsg.getCauses().iterator(); iterator2.hasNext(); ) {
                        Cause c = (Cause) iterator2.next();
                        System.out.println( "\t\t" + c );

                    }

                }
            }

            msgs = result.getBySeverity( Severity.NOTE );
            for ( Iterator iterator = msgs.iterator(); iterator.hasNext(); ) {
                VerifierMessageBase msg = (VerifierMessageBase) iterator.next();
                System.out.println( "NOTE: " + msg.getMessage() );
                System.out.println( "\t" + msg.getFaulty() );
            }

            Collection<ObjectType> classes = result.getVerifierData().getAll( VerifierComponentType.OBJECT_TYPE );
            for ( Iterator iterator = classes.iterator(); iterator.hasNext(); ) {
                ObjectType c = (ObjectType) iterator.next();

                Collection<VerifierRule> cr = result.getVerifierData().getRulesByObjectTypePath( c.getPath() );
                System.err.println( "Class rules:" + cr );
                Set<Field> flds = c.getFields();
                for ( Iterator iterator2 = flds.iterator(); iterator2.hasNext(); ) {
                    Field f = (Field) iterator2.next();
                    cr = result.getVerifierData().getRulesByFieldPath( f.getPath() );
                    System.err.println( "Field rules: " + cr );

                }
            }

            // System.err.println(a.getResultAsPlainText());
            // System.out.println(result.toString());
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }
}
