package org.drools.workbench.models.guided.template.backend;

import org.drools.compiler.kie.builder.impl.FormatConversionResult;
import org.drools.compiler.kie.builder.impl.FormatConverter;
import org.drools.workbench.models.commons.backend.BaseConverter;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.datamodel.oracle.PackageDataModelOracle;
import org.drools.workbench.models.guided.template.shared.TemplateModel;

public class GuidedRuleTemplateConverter extends BaseConverter implements FormatConverter {

    private final PackageDataModelOracle dmo;

    public GuidedRuleTemplateConverter(final PackageDataModelOracle dmo) {
        this.dmo = dmo;
    }

    @Override
    public FormatConversionResult convert( String name,
                                           byte[] input ) {
        String xml = new String( input );
        TemplateModel model = (TemplateModel) BRDRTXMLPersistence.getInstance().unmarshal( xml, dmo );

        String drl = new StringBuilder().append( BRDRTPersistence.getInstance().marshal( model ) ).toString();

        return new FormatConversionResult( getDestinationName( name, model.hasDSLSentences() ), drl.getBytes() );
    }
}
