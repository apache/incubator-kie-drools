/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.workitem.bpmn2;

import org.jbpm.process.workitem.core.util.Wid;
import org.jbpm.process.workitem.core.util.WidMavenDepends;
import org.jbpm.process.workitem.core.util.WidParameter;
import org.jbpm.process.workitem.core.util.service.WidAction;
import org.jbpm.process.workitem.core.util.service.WidAuth;
import org.jbpm.process.workitem.core.util.service.WidService;

/**
 * Additional BusinessRuleTask support that allows to decouple rules from processes - as default BusinessRuleTask
 * uses exact same working memory (kie session) as process which essentially means same kbase.
 * To allow better separation and maintainability BusinessRuleTaskHandler is provided that supports:
 * <ul>
 * <li>DRL stateful</li>
 * <li>DRL stateless</li>
 * </ul>
 * <p>
 * Session type can be given by KieSessionType data input and session name can be given as KieSessionName property -these apply to DRL only.
 * <p>
 * Results returned will be then put back into the data outputs. <br/>
 * <br/>
 * DRL handling is based on same names for data input and output as that is then used as correlation.<br/>
 */

@Wid(widfile = "DecisionTaskDefinitions.wid", name = "DecisionTask",
        displayName = "Decision Task",
        defaultHandler = "mvel: new org.jbpm.process.workitem.bpmn2.DecisionTaskHandler()",
        documentation = "${artifactId}/index.html",
        category = "${artifactId}",
        icon = "DecisionTask.png",
        parameters = {
                @WidParameter(name = "Namespace", required = true),
                @WidParameter(name = "Model", required = true),
                @WidParameter(name = "Decision")
        },
        mavenDepends = {
                @WidMavenDepends(group = "${groupId}", artifact = "${artifactId}", version = "${version}")
        },
        serviceInfo = @WidService(category = "${name}", description = "${description}",
                keywords = "decision,dmn,rule,task",
                action = @WidAction(title = "Execute a DMN decision task"),
                authinfo = @WidAuth(required = true, params = {"groupId", "artifactId", "version"},
                paramsdescription = {"Group Id", "Artifact Id", "Version"})
        ))
public class DecisionTaskHandler extends AbstractRuleTaskHandler {
   
    public DecisionTaskHandler(String groupId,
                                   String artifactId,
                                   String version) {
        super(groupId, artifactId, version);
    }

    public DecisionTaskHandler(String groupId,
                                   String artifactId,
                                   String version,
                                   long scannerInterval) {
        super(groupId, artifactId, version, scannerInterval);
    }

    @Override
    public String getRuleLanguage() {
        return DMN_LANG;
    }

}
