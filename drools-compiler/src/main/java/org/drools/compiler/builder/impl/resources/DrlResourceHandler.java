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

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.ParserError;
import org.drools.io.DescrResource;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.conf.LanguageLevelOption;

public class DrlResourceHandler extends ResourceHandler {
    public DrlResourceHandler(KnowledgeBuilderConfigurationImpl configuration) {
        super(configuration);
    }

    @Override
    public boolean handles(ResourceType type) {
        return type == ResourceType.DRL
            || type ==  ResourceType.GDRL
            || type ==  ResourceType.RDRL
            || type ==  ResourceType.DESCR
            || type ==  ResourceType.TDRL;
    }

    public PackageDescr process(Resource resource, ResourceConfiguration resourceConfig) throws DroolsParserException, IOException {
        PackageDescr pkg;
        boolean hasErrors = false;
        if (resource instanceof DescrResource) {
            pkg = (PackageDescr) ((DescrResource) resource).getDescr();
        } else {
            final DrlParser parser = new DrlParser(this.configuration.getOption(LanguageLevelOption.KEY));
            pkg = parser.parse(resource);
            this.results.addAll(parser.getErrors());
            if (pkg == null) {
                this.results.add(new ParserError(resource, "Parser returned a null Package", 0, 0));
            }
            hasErrors = parser.hasErrors();
        }
        if (pkg != null) {
            pkg.setResource(resource);
        }
        return hasErrors ? null : pkg;
    }

}