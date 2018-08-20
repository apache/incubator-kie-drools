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

import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.InformationRequirement;
import org.kie.dmn.model.api.KnowledgeRequirement;

public class TDecision extends TDRGElement implements Decision {

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

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public void setQuestion( final String value ) {
        this.question = value;
    }

    @Override
    public String getAllowedAnswers() {
        return allowedAnswers;
    }

    @Override
    public void setAllowedAnswers( final String value ) {
        this.allowedAnswers = value;
    }

    @Override
    public InformationItem getVariable() {
        return variable;
    }

    @Override
    public void setVariable(final InformationItem value) {
        this.variable = value;
    }

    @Override
    public List<InformationRequirement> getInformationRequirement() {
        if ( informationRequirement == null ) {
            informationRequirement = new ArrayList<>();
        }
        return this.informationRequirement;
    }

    @Override
    public List<KnowledgeRequirement> getKnowledgeRequirement() {
        if ( knowledgeRequirement == null ) {
            knowledgeRequirement = new ArrayList<>();
        }
        return this.knowledgeRequirement;
    }

    @Override
    public List<AuthorityRequirement> getAuthorityRequirement() {
        if ( authorityRequirement == null ) {
            authorityRequirement = new ArrayList<>();
        }
        return this.authorityRequirement;
    }

    @Override
    public List<DMNElementReference> getSupportedObjective() {
        if ( supportedObjective == null ) {
            supportedObjective = new ArrayList<>();
        }
        return this.supportedObjective;
    }

    @Override
    public List<DMNElementReference> getImpactedPerformanceIndicator() {
        if ( impactedPerformanceIndicator == null ) {
            impactedPerformanceIndicator = new ArrayList<>();
        }
        return this.impactedPerformanceIndicator;
    }

    @Override
    public List<DMNElementReference> getDecisionMaker() {
        if ( decisionMaker == null ) {
            decisionMaker = new ArrayList<>();
        }
        return this.decisionMaker;
    }

    @Override
    public List<DMNElementReference> getDecisionOwner() {
        if ( decisionOwner == null ) {
            decisionOwner = new ArrayList<>();
        }
        return this.decisionOwner;
    }

    @Override
    public List<DMNElementReference> getUsingProcess() {
        if ( usingProcess == null ) {
            usingProcess = new ArrayList<>();
        }
        return this.usingProcess;
    }

    @Override
    public List<DMNElementReference> getUsingTask() {
        if ( usingTask == null ) {
            usingTask = new ArrayList<>();
        }
        return this.usingTask;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(final Expression value) {
        this.expression = value;
    }

    @Override
    public String toString() {
        return getName();
    }
}
