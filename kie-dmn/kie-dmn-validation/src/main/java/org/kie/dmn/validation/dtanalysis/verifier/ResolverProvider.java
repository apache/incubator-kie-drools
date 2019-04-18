/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.validation.dtanalysis.verifier;

import org.drools.verifier.core.configuration.AnalyzerConfiguration;
import org.drools.verifier.core.index.Index;
import org.kie.dmn.model.api.DecisionTable;

public class ResolverProvider {

    private final Index index;
    private final HeaderMetadata headerMetadata;
    private final AnalyzerConfiguration configuration;
    private Util util;

    public ResolverProvider(final DecisionTable dt,
                            final Index index,
                            final Util util,
                            final AnalyzerConfiguration configuration) {
        this.index = index;
        headerMetadata = new HeaderMetadata(dt);
        this.util = util;
        this.configuration = configuration;
    }

    public DMNColumnHeaderInstanceResolver getDMNColumnHeaderInstanceResolver() {
        return new DMNColumnHeaderInstanceResolver(index,
                                                   headerMetadata,
                                                   configuration);
    }

    public DMNCellResolver getDMNCellResolver() {
        return new DMNCellResolver(this,
                                   configuration);
    }

    public StructureFieldResolver getStructureFieldResolver() {
        return new StructureFieldResolver(configuration);
    }

    public ColumnResolver getColumnResolver() {
        return new ColumnResolver(index,
                                  configuration);
    }

    public OutputBuilder getOutputResolver() {
        return new OutputBuilder(util,
                                 this,
                                 configuration);
    }

    public InputBuilder getInputBuilder() {
        return new InputBuilder(util,
                                this,
                                configuration);
    }
}
