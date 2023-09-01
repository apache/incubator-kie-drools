package org.drools.compiler.builder.impl.resources;

import org.drools.compiler.builder.conf.DecisionTableConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.extensions.DecisionTableFactory;
import org.drools.drl.parser.DroolsParserException;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.conf.TrimCellsInDTableOption;

import java.util.List;

public class DecisionTableResourceHandler extends ResourceHandler {

    public DecisionTableResourceHandler(KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId) {
        super(configuration, releaseId);
    }

    @Override
    public boolean handles(ResourceType type) {
        return type == ResourceType.DTABLE;
    }

    public PackageDescr process(Resource resource, ResourceConfiguration rConfiguration) throws DroolsParserException {
        DecisionTableConfiguration dtableConfiguration = rConfiguration instanceof DecisionTableConfiguration ?
                (DecisionTableConfiguration) rConfiguration :
                new DecisionTableConfigurationImpl();

        if (!dtableConfiguration.getRuleTemplateConfigurations().isEmpty()) {
            List<String> generatedDrls = DecisionTableFactory.loadFromInputStreamWithTemplates(resource, dtableConfiguration);
            if (generatedDrls.size() == 1) {
                return generatedDrlToPackageDescr(resource, generatedDrls.get(0));
            }
            CompositePackageDescr compositePackageDescr = null;
            for (String generatedDrl : generatedDrls) {
                PackageDescr packageDescr = generatedDrlToPackageDescr(resource, generatedDrl);
                if (packageDescr != null) {
                    if (compositePackageDescr == null) {
                        compositePackageDescr = new CompositePackageDescr(resource, packageDescr);
                    } else {
                        compositePackageDescr.addPackageDescr(resource, packageDescr);
                    }
                }
            }
            return compositePackageDescr;
        }

        dtableConfiguration.setTrimCell(this.configuration.getOption(TrimCellsInDTableOption.KEY).isTrimCellsInDTable());

        String generatedDrl = DecisionTableFactory.loadFromResource(resource, dtableConfiguration);
        return generatedDrlToPackageDescr(resource, generatedDrl);
    }
}