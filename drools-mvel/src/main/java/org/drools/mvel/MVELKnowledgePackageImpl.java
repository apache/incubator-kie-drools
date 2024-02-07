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
package org.drools.mvel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.drools.mvel.accessors.ClassFieldAccessor;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.mvel.accessors.ClassFieldAccessorStore;
import org.drools.base.common.DroolsObjectInputStream;
import org.drools.base.common.DroolsObjectOutputStream;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.ResourceTypePackageRegistry;
import org.drools.base.definitions.impl.KnowledgePackageImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.factmodel.ClassDefinition;
import org.drools.base.factmodel.FieldDefinition;
import org.drools.base.rule.DialectRuntimeRegistry;
import org.drools.base.rule.Function;
import org.drools.base.rule.ImportDeclaration;
import org.drools.base.rule.TypeDeclaration;
import org.drools.base.rule.WindowDeclaration;
import org.drools.base.base.AcceptsClassObjectType;
import org.drools.base.rule.accessor.AcceptsReadAccessor;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.base.ObjectType;
import org.drools.util.ClassUtils;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class MVELKnowledgePackageImpl extends KnowledgePackageImpl {

    protected ClassFieldAccessorStore classFieldAccessorStore;

    public MVELKnowledgePackageImpl() {
        this(null);
    }

    public MVELKnowledgePackageImpl(String name) {
        super(name);
        this.classFieldAccessorStore = new ClassFieldAccessorStore();
    }

    public ClassFieldAccessorStore getClassFieldAccessorStore() {
        return classFieldAccessorStore;
    }

    public void setClassFieldAccessorCache(Object classFieldAccessorCache) {
        this.classFieldAccessorStore.setClassFieldAccessorCache( (ClassFieldAccessorCache) classFieldAccessorCache);
    }

    @Override
    protected void removeTypeFromStore(TypeDeclaration type) {
        classFieldAccessorStore.removeType(type);
    }

    @Override
    public void mergeStore(InternalKnowledgePackage newPkg) {
        classFieldAccessorStore.merge(((MVELKnowledgePackageImpl) newPkg).getClassFieldAccessorStore());
    }

    @Override
    public void wireStore() {
        classFieldAccessorStore.wire();
    }

    @Override
    public void buildFieldAccessors(TypeDeclaration type) {
        ClassDefinition cd = type.getTypeClassDef();
        for ( FieldDefinition attrDef : cd.getFieldsDefinitions() ) {
            ClassFieldAccessor accessor = classFieldAccessorStore.getAccessor( cd.getDefinedClass().getName(), attrDef.getName() );
            attrDef.setReadWriteAccessor( accessor );
        }
    }

    @Override
    public void removeClass( Class<?> cls ) {
        classFieldAccessorStore.removeClass(cls);
    }

    @Override
    public ObjectType wireObjectType(ObjectType objectType, AcceptsClassObjectType extractor) {
        return classFieldAccessorStore.wireObjectType(objectType, extractor);
    }

    @Override
    public Class<?> getFieldType(Class<?> clazz, String leftValue) {
        return classFieldAccessorStore.getFieldType(clazz, leftValue);
    }

    @Override
    public ReadAccessor getReader(String className, String fieldName, AcceptsReadAccessor target) {
        return classFieldAccessorStore.getReader(className, fieldName, target);
    }

    @Override
    public Collection<KnowledgeBuilderResult> getWiringResults(Class<?> classType, String fieldName) {
        return classFieldAccessorStore.getWiringResults(classType, fieldName);
    }

    @Override
    public ReadAccessor getFieldExtractor( TypeDeclaration type, String timestampField, Class<?> returnType ) {
        ReadAccessor reader = classFieldAccessorStore.getMVELReader( ClassUtils.getPackage( type.getTypeClass() ),
                                                                             type.getTypeClass().getName(),
                                                                             timestampField,
                                                                             type.isTypesafe(),
                                                                             returnType);

        getDialectRuntimeRegistry().getDialectData("mvel").compile( reader );
        return reader;
    }

    /**
     * Handles the write serialization of the Package. Patterns in Rules may
     * reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the
     * generated bytecode. The generated bytecode must be restored before any
     * Rules.
     *
     * @param stream out the stream to write the object to; should be an instance
     *               of DroolsObjectOutputStream or OutputStream
     */
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

    /**
     * Handles the read serialization of the Package. Patterns in Rules may
     * reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the
     * generated bytecode; which must be restored before any Rules. A custom
     * ObjectInputStream, able to resolve classes against the bytecode in the
     * PackageCompilationData, is used to restore the Rules.
     *
     * @param stream, the stream to read data from in order to restore the object;
     *                should be an instance of DroolsObjectInputStream or
     *                InputStream
     */
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
        this.resourceTypePackages = (ResourceTypePackageRegistry) in.readObject();

        in.setStore(null);

        if (!isDroolsStream) {
            in.close();
        }
    }
}
