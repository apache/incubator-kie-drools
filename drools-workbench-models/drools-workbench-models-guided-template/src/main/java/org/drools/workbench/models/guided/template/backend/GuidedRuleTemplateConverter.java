package org.drools.workbench.models.guided.template.backend;

import org.drools.compiler.kie.builder.impl.FormatConversionResult;
import org.drools.compiler.kie.builder.impl.FormatConverter;
import org.drools.core.util.IoUtils;
import org.drools.workbench.models.commons.backend.BaseConverter;
import org.drools.workbench.models.guided.template.shared.TemplateModel;

public class GuidedRuleTemplateConverter extends BaseConverter implements FormatConverter {

    @Override
    public FormatConversionResult convert( String name,
                                           byte[] input ) {
        String xml = new String( input, IoUtils.UTF8_CHARSET );
        TemplateModel model = (TemplateModel) RuleTemplateModelXMLPersistenceImpl.getInstance().unmarshal( xml );
        String drl = new StringBuilder().append( RuleTemplateModelDRLPersistenceImpl.getInstance().marshal( model ) ).toString();

        return new FormatConversionResult( getDestinationName( name, model.hasDSLSentences() ), drl.getBytes( IoUtils.UTF8_CHARSET ) );
    }
}
