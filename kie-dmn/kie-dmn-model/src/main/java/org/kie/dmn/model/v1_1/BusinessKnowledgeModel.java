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

public class BusinessKnowledgeModel extends DRGElement {

    private FunctionDefinition encapsulatedLogic;
    private InformationItem variable;
    private List<KnowledgeRequirement> knowledgeRequirement;
    private List<AuthorityRequirement> authorityRequirement;

    public FunctionDefinition getEncapsulatedLogic() {
        return encapsulatedLogic;
    }

    public void setEncapsulatedLogic( final FunctionDefinition value ) {
        this.encapsulatedLogic = value;
    }

    public InformationItem getVariable() {
        return variable;
    }

    public void setVariable( final InformationItem value ) {
        this.variable = value;
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

}
