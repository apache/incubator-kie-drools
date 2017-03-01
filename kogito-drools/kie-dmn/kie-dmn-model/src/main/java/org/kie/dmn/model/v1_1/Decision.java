/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.model.v1_1;

import java.util.ArrayList;
import java.util.List;

public class Decision extends DRGElement {

    private String question;
    private String allowedAnswers;
    private InformationItem variable;
    private List<InformationRequirement> informationRequirement;
    private List<KnowledgeRequirement> knowledgeRequirement;
    private List<AuthorityRequirement> authorityRequirement;
    private List<DMNElementReference> supportedObjective;
    private List<DMNElementReference> impactedPerformanceIndicator;
    private List<DMNElementReference> decisionMaker;
    private List<DMNElementReference> decisionOwner;
    private List<DMNElementReference> usingProcess;
    private List<DMNElementReference> usingTask;
    private Expression expression;

    public String getQuestion() {
        return question;
    }

    public void setQuestion( final String value ) {
        this.question = value;
    }

    public String getAllowedAnswers() {
        return allowedAnswers;
    }

    public void setAllowedAnswers( final String value ) {
        this.allowedAnswers = value;
    }

    public InformationItem getVariable() {
        return variable;
    }

    public void setVariable( final InformationItem value ) {
        this.variable = value;
    }

    public List<InformationRequirement> getInformationRequirement() {
        if ( informationRequirement == null ) {
            informationRequirement = new ArrayList<>();
        }
        return this.informationRequirement;
    }

    public List<KnowledgeRequirement> getKnowledgeRequirement() {
        if ( knowledgeRequirement == null ) {
            knowledgeRequirement = new ArrayList<>();
        }
        return this.knowledgeRequirement;
    }

    public List<AuthorityRequirement> getAuthorityRequirement() {
        if ( authorityRequirement == null ) {
            authorityRequirement = new ArrayList<>();
        }
        return this.authorityRequirement;
    }

    public List<DMNElementReference> getSupportedObjective() {
        if ( supportedObjective == null ) {
            supportedObjective = new ArrayList<>();
        }
        return this.supportedObjective;
    }

    public List<DMNElementReference> getImpactedPerformanceIndicator() {
        if ( impactedPerformanceIndicator == null ) {
            impactedPerformanceIndicator = new ArrayList<>();
        }
        return this.impactedPerformanceIndicator;
    }

    public List<DMNElementReference> getDecisionMaker() {
        if ( decisionMaker == null ) {
            decisionMaker = new ArrayList<>();
        }
        return this.decisionMaker;
    }

    public List<DMNElementReference> getDecisionOwner() {
        if ( decisionOwner == null ) {
            decisionOwner = new ArrayList<>();
        }
        return this.decisionOwner;
    }

    public List<DMNElementReference> getUsingProcess() {
        if ( usingProcess == null ) {
            usingProcess = new ArrayList<>();
        }
        return this.usingProcess;
    }

    public List<DMNElementReference> getUsingTask() {
        if ( usingTask == null ) {
            usingTask = new ArrayList<>();
        }
        return this.usingTask;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression( final Expression value ) {
        this.expression = value;
    }

}
