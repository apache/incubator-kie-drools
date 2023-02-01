/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.rule.accessor;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;

import org.drools.core.base.AcceptsClassObjectType;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.ObjectType;
import org.drools.core.base.extractors.BaseObjectClassFieldReader;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.facttemplates.Fact;
import org.drools.util.ClassUtils;

/**
 * This is a global variable extractor used to get a global variable value
 */
public class GlobalExtractor extends BaseObjectClassFieldReader
    implements
    ReadAccessor,
        AcceptsClassObjectType,
    Externalizable {

    private static final long serialVersionUID = 510l;
    private ObjectType objectType;
    private String            identifier;

    public GlobalExtractor() {
    }

    public GlobalExtractor(final String identifier,
                           final ObjectType objectType) {
        super( -1, ((ClassObjectType) objectType).getClassType(), objectType.getValueType() );
        this.identifier = identifier;
        this.objectType = objectType;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        identifier = in.readUTF();
        objectType = (ObjectType) in.readObject();
        setIndex( -1 );
        setFieldType( ((ClassObjectType) objectType).getClassType() );
        setValueType( objectType.getValueType() );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( identifier );
        out.writeObject( objectType );
    }

    public void setClassObjectType(ClassObjectType objectType) {
        this.objectType = objectType;
        setIndex( -1 );
        setFieldType( objectType.getClassType() );
        setValueType( objectType.getValueType() );
    }

    @Override
    public Object getValue(ReteEvaluator reteEvaluator,
                           final Object object) {
        return reteEvaluator.getGlobal( identifier );
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }

    public Class< ? > getExtractToClass() {
        // @todo : this is a bit nasty, but does the trick
        if ( this.objectType instanceof ClassObjectType ) {
            return ((ClassObjectType) this.objectType).getClassType();
        } else {
            return Fact.class;
        }
    }

    public String getExtractToClassName() {
        Class< ? > clazz;
        // @todo : this is a bit nasty, but does the trick
        if ( this.objectType instanceof ClassObjectType ) {
            clazz = ((ClassObjectType) this.objectType).getClassType();
        } else {
            clazz = Fact.class;
        }
        return ClassUtils.canonicalName( clazz );
    }

    public Method getNativeReadMethod() {
        try {
            return this.getClass().getDeclaredMethod("getValue",
                                                     ReteEvaluator.class, Object.class);
        } catch ( final Exception e ) {
            throw new RuntimeException( "This is a bug. Please report to development team: " + e.getMessage(),
                                        e );
        }
    }

    public String getNativeReadMethodName() {
        return "getValue";
    }

    public int hashCode() {
        return this.objectType.hashCode();
    }

    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( !(obj instanceof GlobalExtractor) ) {
            return false;
        }
        final GlobalExtractor other = (GlobalExtractor) obj;
        return this.objectType.equals( other.objectType );
    }

    public boolean isGlobal() {
        return true;
    }

    public boolean isSelfReference() {
        return false;
    }

    public int getHashCode(Object object) {
        throw new RuntimeException( "Can't extract a value from global " + identifier + " without a working memory reference" );
    }

    public int getIndex() {
        return -1;
    }

    public Object getValue(Object object) {
        throw new RuntimeException( "Can't extract a value from global " + identifier + " without a working memory reference" );
    }

    public boolean isNullValue(Object object) {
        throw new RuntimeException( "Can't extract a value from global " + identifier + " without a working memory reference" );
    }
}
