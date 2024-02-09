/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.builder.impl.resources;

import java.io.IOException;
import java.io.StringReader;
import java.util.function.Supplier;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.errors.MissingImplementationException;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.extensions.GuidedRuleTemplateFactory;
import org.drools.drl.extensions.GuidedRuleTemplateProvider;
import org.drools.drl.extensions.ResourceConversionResult;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

public class TemplateResourceHandler extends ResourceHandler {

    private Supplier<DefaultExpander> expander;

    public TemplateResourceHandler(KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId, Supplier<DefaultExpander> expander) {
        super(configuration, releaseId);
        this.expander = expander;
    }

    @Override
    public boolean handles(ResourceType type) {
        return type == ResourceType.TEMPLATE;
    }

    @Override
    public PackageDescr process(Resource resource, ResourceConfiguration configuration) throws DroolsParserException, IOException {
        GuidedRuleTemplateProvider guidedRuleTemplateProvider = GuidedRuleTemplateFactory.getGuidedRuleTemplateProvider();
        if (guidedRuleTemplateProvider == null) {
            throw new MissingImplementationException(resource, "drools-workbench-models-guided-template");
        }
        ResourceConversionResult conversionResult = guidedRuleTemplateProvider.loadFromInputStream(resource.getInputStream());
        return conversionResultToPackageDescr(resource, conversionResult, expander.get());
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