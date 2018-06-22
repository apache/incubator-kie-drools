/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.casemgmt.cmmn.xml;

import org.drools.core.xml.DefaultSemanticModule;
import org.drools.core.xml.SemanticModule;

public class CMMNSemanticModule extends DefaultSemanticModule implements SemanticModule {

    public static final String CMMN_URI = "http://www.omg.org/spec/CMMN/20151109/MODEL";

    public CMMNSemanticModule() {
        super(CMMN_URI);

        addHandler("definitions", new DefinitionsHandler());
        addHandler("caseFileItemDefinition", new FileItemDefinitionHandler());
        addHandler("caseFileItem", new FileItemHandler());
        addHandler("case", new CaseHandler());
        addHandler("role", new CaseRoleHandler());
        addHandler("planItem", new PlanItemHandler());
        addHandler("sentry", new SentryHandler());
        addHandler("humanTask", new HumanTaskHandler());
        addHandler("milestone", new MilestoneHandler());
        addHandler("stage", new StageHandler());
        addHandler("processTask", new ProcessTaskHandler());
        addHandler("caseTask", new CaseTaskHandler());
        addHandler("decisionTask", new DecisionTaskHandler());

        addHandler("process", new ProcessElementHandler());
        addHandler("decision", new DecisionElementHandler());
    }
}
