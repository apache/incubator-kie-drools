package org.drools.workbench.models.guided.scorecard.backend;

import org.drools.compiler.kie.builder.impl.FormatConversionResult;
import org.drools.compiler.kie.builder.impl.FormatConverter;
import org.drools.workbench.models.commons.backend.BaseConverter;
import org.drools.workbench.models.guided.scorecard.shared.ScoreCardModel;

public class GuidedScoreCardConverter extends BaseConverter implements FormatConverter {

    @Override
    public FormatConversionResult convert( String name,
                                           byte[] input ) {
        String xml = new String( input );
        ScoreCardModel model = GuidedScoreCardXMLPersistence.getInstance().unmarshall( xml );

        String drl = new StringBuilder().append( GuidedScoreCardDRLPersistence.marshal( model ) ).toString();

        return new FormatConversionResult( getDestinationName( name ), drl.getBytes() );
    }
}
