package org.drools.guvnor.models.guided.template.backend;

import org.drools.compiler.kie.builder.impl.FormatConversionResult;
import org.drools.compiler.kie.builder.impl.FormatConverter;
import org.drools.guvnor.models.commons.backend.BaseConverter;
import org.drools.guvnor.models.guided.template.shared.TemplateModel;

public class GuidedRuleTemplateConverter extends BaseConverter implements FormatConverter {

    @Override
    public FormatConversionResult convert( String name,
                                           byte[] input ) {
        String xml = new String( input );
        TemplateModel model = (TemplateModel) BRDRTXMLPersistence.getInstance().unmarshal( xml );

        String drl = new StringBuilder().append( BRDRTPersistence.getInstance().marshal( model ) ).toString();

        return new FormatConversionResult( getDestinationName( name, model.hasDSLSentences() ), drl.getBytes() );
    }
}
