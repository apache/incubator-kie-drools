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
package org.kie.dmn.model.v1_3;

import java.util.ArrayList;
import java.util.List;

import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.BusinessKnowledgeModel;
import org.kie.dmn.model.api.FunctionDefinition;
import org.kie.dmn.model.api.KnowledgeRequirement;

public class TBusinessKnowledgeModel extends TInvocable implements BusinessKnowledgeModel {

    protected FunctionDefinition encapsulatedLogic;
    protected List<KnowledgeRequirement> knowledgeRequirement;
    protected List<AuthorityRequirement> authorityRequirement;

    @Override
    public FunctionDefinition getEncapsulatedLogic() {
        return encapsulatedLogic;
    }

    @Override
    public void setEncapsulatedLogic(FunctionDefinition value) {
        this.encapsulatedLogic = value;
    }

    @Override
    public List<KnowledgeRequirement> getKnowledgeRequirement() {
        if (knowledgeRequirement == null) {
            knowledgeRequirement = new ArrayList<>();
        }
        return this.knowledgeRequirement;
    }

    @Override
    public List<AuthorityRequirement> getAuthorityRequirement() {
        if (authorityRequirement == null) {
            authorityRequirement = new ArrayList<>();
        }
        return this.authorityRequirement;
    }

}
