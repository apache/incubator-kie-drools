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
import org.kie.dmn.model.api.KnowledgeSource;

public class TKnowledgeSource extends TDRGElement implements KnowledgeSource {

    private List<AuthorityRequirement> authorityRequirement;
    private String type;
    private DMNElementReference owner;
    private String locationURI;

    @Override
    public List<AuthorityRequirement> getAuthorityRequirement() {
        if ( authorityRequirement == null ) {
            authorityRequirement = new ArrayList<>();
        }
        return this.authorityRequirement;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType( final String value ) {
        this.type = value;
    }

    @Override
    public DMNElementReference getOwner() {
        return owner;
    }

    @Override
    public void setOwner(final DMNElementReference value) {
        this.owner = value;
    }

    @Override
    public String getLocationURI() {
        return locationURI;
    }

    @Override
    public void setLocationURI( final String value ) {
        this.locationURI = value;
    }

}
