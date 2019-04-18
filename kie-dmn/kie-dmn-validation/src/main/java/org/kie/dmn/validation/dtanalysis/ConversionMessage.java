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
package org.kie.dmn.validation.dtanalysis;

import java.util.List;
import java.util.Set;

import org.kie.dmn.validation.dtanalysis.model.Subsumption;

public class ConversionMessage {

    private final Set<DMNDTAnalysisMessage> issues;
    private final List<Subsumption> subsumptions;

    public ConversionMessage(final Set<DMNDTAnalysisMessage> issues,
                             final List<Subsumption> subsumptions) {
        this.issues = issues;
        this.subsumptions = subsumptions;
    }

    public Set<DMNDTAnalysisMessage> getIssues() {
        return issues;
    }

    public List<Subsumption> getSubsumptions() {
        return subsumptions;
    }
}
