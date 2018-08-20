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

import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.OrganizationUnit;

public class TOrganizationUnit extends TBusinessContextElement implements OrganizationUnit {

    private List<DMNElementReference> decisionMade;
    private List<DMNElementReference> decisionOwned;

    @Override
    public List<DMNElementReference> getDecisionMade() {
        if ( decisionMade == null ) {
            decisionMade = new ArrayList<>();
        }
        return this.decisionMade;
    }

    @Override
    public List<DMNElementReference> getDecisionOwned() {
        if ( decisionOwned == null ) {
            decisionOwned = new ArrayList<>();
        }
        return this.decisionOwned;
    }

}
