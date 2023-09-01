package org.drools.verifier.report;

import java.io.IOException;
import java.io.OutputStream;

import com.thoughtworks.xstream.XStream;
import org.drools.util.IoUtils;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.data.VerifierReport;
import org.drools.verifier.report.components.Gap;
import org.drools.verifier.report.components.MissingNumberPattern;
import org.drools.verifier.report.components.VerifierMessage;

import static org.kie.utll.xml.XStreamUtils.createNonTrustingXStream;

public class XMLReportWriter
    implements
    VerifierReportWriter {

    public void writeReport(OutputStream out,
                            VerifierReport result) throws IOException {
        XStream xstream = createNonTrustingXStream();

        xstream.alias( "result",
                       VerifierReport.class );
        xstream.alias( "message",
                       VerifierMessage.class );

        xstream.alias( "Gap",
                       Gap.class );
        xstream.alias( "MissingNumber",
                       MissingNumberPattern.class );

        xstream.alias( "Field",
                       org.drools.verifier.components.Field.class );

        xstream.alias( "LiteralRestriction",
                       LiteralRestriction.class );

        out.write( ("<?xml version=\"1.0\"?>\n" + xstream.toXML( result )).getBytes( IoUtils.UTF8_CHARSET ) );
    }

}
