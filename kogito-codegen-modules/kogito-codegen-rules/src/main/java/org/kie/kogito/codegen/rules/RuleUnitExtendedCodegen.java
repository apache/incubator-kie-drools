/*
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
package org.kie.kogito.codegen.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.codegen.common.GeneratedFile;
import org.kie.kogito.codegen.api.context.KogitoBuildContext;

public class RuleUnitExtendedCodegen {
    private final RuleUnitQueryDashboardCodegen dashboards;
    private final RuleUnitQueryEventCodegen events;

    public RuleUnitExtendedCodegen(
            KogitoBuildContext context,
            Collection<QueryGenerator> validQueries) {
        this.events = new RuleUnitQueryEventCodegen(context, validQueries);
        Collection<QueryEndpointGenerator> endpointGenerators = validQueries.stream()
                .map(QueryEndpointGenerator::new)
                .collect(Collectors.toUnmodifiableList());
        this.dashboards = new RuleUnitQueryDashboardCodegen(context, endpointGenerators);
    }

    Collection<GeneratedFile> generate() {
        List<GeneratedFile> generatedFiles = new ArrayList<>();

        generatedFiles.addAll(events.generate());
        generatedFiles.addAll(dashboards.generate());

        return generatedFiles;
    }

}
