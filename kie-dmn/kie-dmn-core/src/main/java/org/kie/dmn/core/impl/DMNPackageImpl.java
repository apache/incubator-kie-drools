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

package org.kie.dmn.core.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNPackage;
import org.kie.dmn.core.compiler.DMNProfile;

public class DMNPackageImpl implements DMNPackage, Externalizable {

    private String namespace;

    private Map<String, DMNModel> models = new HashMap<>(  );
    private List<DMNProfile> profiles = new ArrayList<>();

    // adding something here? don't forget to update merge of DMNWeaverService

    public DMNPackageImpl() {
        this("");
    }

    public DMNPackageImpl(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace( String namespace ) {
        this.namespace = namespace;
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.DMN;
    }

    public DMNModel addModel( String name, DMNModel model ) {
        return models.put( name, model );
    }

    @Override
    public DMNModel getModel(String name){
        return models.get( name );
    }

    @Override
    public Map<String, DMNModel> getAllModels() {
        return Collections.unmodifiableMap( models );
    }

    @Override
    public boolean removeResource(Resource resource) {
        return models.entrySet().removeIf( kv -> resource.equals( kv.getValue().getResource() ) );
    }
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( this.namespace );
        out.writeObject( this.models );
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.namespace = (String) in.readObject();
        this.models = (Map<String, DMNModel>) in.readObject();
    }

    public void addProfiles(List<DMNProfile> profiles) {
        this.profiles.addAll(profiles);
    }

    public List<DMNProfile> getProfiles() {
        return profiles;
    }

    // adding something here? don't forget to update merge of DMNWeaverService
}
