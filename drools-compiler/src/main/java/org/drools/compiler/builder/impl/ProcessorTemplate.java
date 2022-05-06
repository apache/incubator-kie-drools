/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.builder.impl;

import org.drools.compiler.builder.impl.errors.MissingImplementationException;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.extensions.GuidedRuleTemplateFactory;
import org.drools.drl.extensions.GuidedRuleTemplateProvider;
import org.drools.drl.extensions.ResourceConversionResult;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;

import java.io.IOException;
import java.io.StringReader;

public class ProcessorTemplate extends Processor{

    public ProcessorTemplate(KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId) {
        super(configuration, releaseId);
    }

    public PackageDescr process(Resource resource, DefaultExpander expander) throws DroolsParserException, IOException {
        GuidedRuleTemplateProvider guidedRuleTemplateProvider = GuidedRuleTemplateFactory.getGuidedRuleTemplateProvider();
        if (guidedRuleTemplateProvider == null) {
            throw new MissingImplementationException(resource, "drools-workbench-models-guided-template");
        }
        ResourceConversionResult conversionResult = guidedRuleTemplateProvider.loadFromInputStream(resource.getInputStream());
        return conversionResultToPackageDescr(resource, conversionResult, expander);
    }

    private PackageDescr conversionResultToPackageDescr(Resource resource, ResourceConversionResult resourceConversionResult, DefaultExpander expander)
            throws DroolsParserException {
        ResourceType resourceType = resourceConversionResult.getType();
        if (ResourceType.DSLR.equals(resourceType)) {
            return generatedDslrToPackageDescr(resource, resourceConversionResult.getContent(), expander);
        } else if (ResourceType.DRL.equals(resourceType)) {
            return generatedDrlToPackageDescr(resource, resourceConversionResult.getContent());
        } else {
            throw new DroolsParserException("Converting generated " + resourceType + " into PackageDescr is not supported!");
        }
    }

    PackageDescr generatedDslrToPackageDescr(Resource resource, String dslr, DefaultExpander expander) throws DroolsParserException {
        return dslrReaderToPackageDescr(resource, new StringReader(dslr), expander);
    }
}