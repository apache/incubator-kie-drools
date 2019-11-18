/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.model.v1_3;

import org.kie.dmn.model.api.AuthorityRequirement;
import org.kie.dmn.model.api.DMNElementReference;

public class TAuthorityRequirement extends TDMNElement implements AuthorityRequirement {

    protected DMNElementReference requiredDecision;
    protected DMNElementReference requiredInput;
    protected DMNElementReference requiredAuthority;

    @Override
    public DMNElementReference getRequiredDecision() {
        return requiredDecision;
    }

    @Override
    public void setRequiredDecision(DMNElementReference value) {
        this.requiredDecision = value;
    }

    @Override
    public DMNElementReference getRequiredInput() {
        return requiredInput;
    }

    @Override
    public void setRequiredInput(DMNElementReference value) {
        this.requiredInput = value;
    }

    @Override
    public DMNElementReference getRequiredAuthority() {
        return requiredAuthority;
    }

    @Override
    public void setRequiredAuthority(DMNElementReference value) {
        this.requiredAuthority = value;
    }

}
