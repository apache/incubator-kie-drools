package org.drools.compiler.builder.impl;

import org.drools.compiler.builder.impl.errors.MissingImplementationException;
import org.drools.compiler.compiler.*;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

import java.io.*;

public class ProcessorTemplate extends Processor{

    public ProcessorTemplate(KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId) {
        super(configuration, releaseId);
    }

    public PackageDescr process(Resource resource) throws DroolsParserException, IOException {
        GuidedRuleTemplateProvider guidedRuleTemplateProvider = GuidedRuleTemplateFactory.getGuidedRuleTemplateProvider();
        if (guidedRuleTemplateProvider == null) {
            throw new MissingImplementationException(resource, "drools-workbench-models-guided-template");
        }
        ResourceConversionResult conversionResult = guidedRuleTemplateProvider.loadFromInputStream(resource.getInputStream());
        return conversionResultToPackageDescr(resource, conversionResult);
    }

    private PackageDescr conversionResultToPackageDescr(Resource resource, ResourceConversionResult resourceConversionResult)
            throws DroolsParserException {
        ResourceType resourceType = resourceConversionResult.getType();
        if (ResourceType.DSLR.equals(resourceType)) {
            return generatedDslrToPackageDescr(resource, resourceConversionResult.getContent());
        } else if (ResourceType.DRL.equals(resourceType)) {
            return generatedDrlToPackageDescr(resource, resourceConversionResult.getContent());
        } else {
            throw new RuntimeException("Converting generated " + resourceType + " into PackageDescr is not supported!");
        }
    }

    PackageDescr generatedDslrToPackageDescr(Resource resource, String dslr) throws DroolsParserException {
        return dslrReaderToPackageDescr(resource, new StringReader(dslr));
    }

}

