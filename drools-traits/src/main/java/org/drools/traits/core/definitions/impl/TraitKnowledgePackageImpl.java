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
package org.drools.traits.core.definitions.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import org.drools.base.common.DroolsObjectInputStream;
import org.drools.base.common.DroolsObjectOutputStream;
import org.drools.base.definitions.ResourceTypePackageRegistry;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.base.rule.DialectRuntimeRegistry;
import org.drools.base.rule.Function;
import org.drools.base.rule.ImportDeclaration;
import org.drools.base.rule.WindowDeclaration;
import org.drools.mvel.MVELKnowledgePackageImpl;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.traits.core.factmodel.TraitRegistryImpl;
import org.drools.traits.core.reteoo.TraitRuntimeComponentFactory;
import org.drools.base.RuleBase;
import org.kie.api.runtime.rule.AccumulateFunction;

public class TraitKnowledgePackageImpl extends MVELKnowledgePackageImpl {

    private TraitRegistryImpl traitRegistry;

    private static final String[] implicitImports = new String[]{
            "org.kie.api.definition.rule.*",
            "org.kie.api.definition.type.*",
            "org.drools.base.factmodel.traits.Alias",
            "org.drools.base.factmodel.traits.Trait",
            "org.drools.base.factmodel.traits.Traitable",
            "org.drools.tms.beliefsystem.abductive.Abductive",
            "org.drools.tms.beliefsystem.abductive.Abducible"};

    public TraitKnowledgePackageImpl() {
    }

    public TraitKnowledgePackageImpl(String name) {
        super(name);
    }

    public TraitRegistryImpl getTraitRegistry() {
        if (traitRegistry == null) {
            traitRegistry = new TraitRegistryImpl();
        }
        return traitRegistry;
    }

    @Override
    public void mergeTraitRegistry(RuleBase knowledgeBase) {
        if (traitRegistry != null) {
            ((TraitRuntimeComponentFactory) RuntimeComponentFactory.get()).getTraitRegistry(knowledgeBase).merge(traitRegistry);
        }
    }

    @Override
    protected String[] getImplicitImports() {
        return implicitImports;
    }

    @Override
    public void writeExternal(ObjectOutput stream) throws IOException {
        ByteArrayOutputStream bytes = null;
        ObjectOutput out;

        if (stream instanceof DroolsObjectOutputStream) {
            out = stream;
        } else {
            bytes = new ByteArrayOutputStream();
            out = new DroolsObjectOutputStream(bytes);
        }

        try {
            out.writeObject(this.name);
            out.writeObject(this.classFieldAccessorStore);
            out.writeObject(this.dialectRuntimeRegistry);
            out.writeObject(this.typeDeclarations);
            out.writeObject(this.imports);
            out.writeObject(this.staticImports);
            out.writeObject(this.functions);
            out.writeObject(this.accumulateFunctions);
            out.writeObject(this.prototypes);
            out.writeObject(this.globals);
            out.writeBoolean(this.valid);
            out.writeBoolean(this.needStreamMode);
            out.writeObject(this.rules);
            out.writeObject(this.entryPointsIds);
            out.writeObject(this.windowDeclarations);
            out.writeObject(this.traitRegistry);
            out.writeObject(this.resourceTypePackages);
        } finally {
            // writing the whole stream as a byte array
            if (bytes != null) {
                bytes.flush();
                bytes.close();
                stream.writeObject(bytes.toByteArray());
            }
        }
    }

    @Override
    public void readExternal(ObjectInput stream) throws IOException,
            ClassNotFoundException {
        boolean isDroolsStream = stream instanceof DroolsObjectInputStream;
        DroolsObjectInputStream in = isDroolsStream ? (DroolsObjectInputStream) stream
                : new DroolsObjectInputStream(
                new ByteArrayInputStream(
                        (byte[]) stream.readObject()));

        this.name = (String) in.readObject();
        this.classFieldAccessorStore = (ClassFieldAccessorStore) in.readObject();
        in.setStore(this.classFieldAccessorStore);

        this.dialectRuntimeRegistry = (DialectRuntimeRegistry) in.readObject();
        this.typeDeclarations = (Map) in.readObject();
        this.imports = (Map<String, ImportDeclaration>) in.readObject();
        this.staticImports = (Set) in.readObject();
        this.functions = (Map<String, Function>) in.readObject();
        this.accumulateFunctions = (Map<String, AccumulateFunction>) in.readObject();
        this.prototypes = (Map) in.readObject();
        this.globals = (Map<String, Type>) in.readObject();
        this.valid = in.readBoolean();
        this.needStreamMode = in.readBoolean();
        this.rules = (Map<String, RuleImpl>) in.readObject();
        this.entryPointsIds = (Set<String>) in.readObject();
        this.windowDeclarations = (Map<String, WindowDeclaration>) in.readObject();
        this.traitRegistry = (TraitRegistryImpl) in.readObject();
        this.resourceTypePackages = (ResourceTypePackageRegistry) in.readObject();

        in.setStore(null);

        if (!isDroolsStream) {
            in.close();
        }
    }
}
