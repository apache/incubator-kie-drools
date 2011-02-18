/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.verifier.builder;

import java.io.Reader;
import java.io.StringReader;

import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.compiler.BusinessRuleProvider;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.compiler.PackageBuilderErrors;
import org.drools.io.Resource;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.dsl.DefaultExpander;

/**
 * Wraps the PackageBuilder for Verifier.
 * Used to build PackageDescrs.
 */
class VerifierPackageBuilder {

    private InnerBuilder innerBuilder = new InnerBuilder();

    private PackageDescr packageDescr;

    public void addKnowledgeResource(Resource resource,
                                     ResourceType type,
                                     ResourceConfiguration configuration) {
        innerBuilder.addKnowledgeResource( resource,
                                           type,
                                           configuration );
    }

    public PackageDescr getPackageDescr() {
        return packageDescr;
    }

    public boolean hasErrors() {
        return innerBuilder.hasErrors();
    }

    public PackageBuilderErrors getErrors() {
        return innerBuilder.getErrors();
    }

    class InnerBuilder extends PackageBuilder {
        public InnerBuilder() {
            super( new PackageBuilderConfiguration() );
        }

        @Override
        public void addPackage(PackageDescr pDescr) {
            packageDescr = pDescr;
        }
    }
}
