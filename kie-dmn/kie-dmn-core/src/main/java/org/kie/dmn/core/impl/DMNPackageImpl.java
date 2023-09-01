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
package org.kie.dmn.core.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.base.common.DroolsObjectInputStream;
import org.drools.base.common.DroolsObjectOutputStream;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNPackage;
import org.kie.dmn.core.compiler.DMNProfile;

public class DMNPackageImpl implements DMNPackage, Externalizable {

    private String namespace;

    private Map<String, DMNModel> models = new HashMap<>(  );
    private List<DMNProfile> profiles = new ArrayList<>();

    // adding something here? don't forget to update:
    // 1. merge of DMNWeaverService
    // 2. for Business Central and Workbench WBCommonServicesBackend scope, the #readExternal below (in-memory identity cloning for BC/WB purposes, RHDM-969)

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

    public DMNModel lookup( String name ) {
        return getModel(name);
    }

    @Override
    public void add(DMNModel processedResource) {
        addModel(processedResource.getName(), processedResource);
    }

    @Override
    public Iterator<DMNModel> iterator() {
        return getAllModels().values().iterator();
    }

    public DMNModel addModel(String name, DMNModel model ) {
        return models.put( name, model );
    }

    @Override
    public DMNModel getModel(String name){
        return models.get( name );
    }
    
    @Override
    public DMNModel getModelById(String id){
        for (DMNModel model : models.values()) {
            if (model.getDefinitions().getId().equals(id)) {
                return model;
            }
        }
        
        return null;
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
        if (out instanceof DroolsObjectOutputStream && (( DroolsObjectOutputStream ) out).isCloning()) {
            (( DroolsObjectOutputStream ) out).addCloneByIdentity( namespace, this );
        } else {
            out.writeObject( this.models );
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.namespace = (String) in.readObject();
        if (in instanceof DroolsObjectInputStream && (( DroolsObjectInputStream ) in).isCloning()) {
            DMNPackageImpl clone = (( DroolsObjectInputStream ) in).getCloneByKey( this.namespace );
            this.models = clone.models;
            this.profiles = clone.profiles;
        } else {
            this.models = ( Map<String, DMNModel> ) in.readObject();
        }
    }

    public void addProfiles(List<DMNProfile> profiles) {
        this.profiles.addAll(profiles);
    }

    public List<DMNProfile> getProfiles() {
        return profiles;
    }

    // adding something here? don't forget to update:
    // 1. merge of DMNWeaverService
    // 2. for Business Central and Workbench WBCommonServicesBackend scope, the #readExternal in this class (in-memory identity cloning for BC/WB purposes, RHDM-969)
}
