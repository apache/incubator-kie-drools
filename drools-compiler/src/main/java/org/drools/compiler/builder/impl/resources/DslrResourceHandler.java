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
import java.util.function.Supplier;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.drl.parser.lang.dsl.DefaultExpander;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;

public class DslrResourceHandler extends ResourceHandler {
    private Supplier<DefaultExpander> expander;

    public DslrResourceHandler(KnowledgeBuilderConfigurationImpl configuration, Supplier<DefaultExpander> expander) {
        super(configuration);
        this.expander = expander;
    }

    @Override
    public boolean handles(ResourceType type) {
        return type == ResourceType.DSLR || type == ResourceType.RDSLR;
    }

    @Override
    public PackageDescr process(Resource resource, ResourceConfiguration configuration) throws DroolsParserException, IOException {
        return dslrReaderToPackageDescr(resource, resource.getReader(), expander.get());
    }
}