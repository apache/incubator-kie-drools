/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.model.api;

import java.util.List;

public interface Decision extends DRGElement {

    String getQuestion();

    void setQuestion(String value);

    String getAllowedAnswers();

    void setAllowedAnswers(String value);

    InformationItem getVariable();

    void setVariable(InformationItem value);

    List<InformationRequirement> getInformationRequirement();

    List<KnowledgeRequirement> getKnowledgeRequirement();

    List<AuthorityRequirement> getAuthorityRequirement();

    List<DMNElementReference> getSupportedObjective();

    List<DMNElementReference> getImpactedPerformanceIndicator();

    List<DMNElementReference> getDecisionMaker();

    List<DMNElementReference> getDecisionOwner();

    List<DMNElementReference> getUsingProcess();

    List<DMNElementReference> getUsingTask();

    Expression getExpression();

    void setExpression(Expression value);

    String toString();

}
