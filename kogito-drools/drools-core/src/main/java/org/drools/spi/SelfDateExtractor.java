/*
 * Copyright 2005 JBoss Inc
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

package org.drools.spi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.ValueType;
import org.drools.base.extractors.BaseDateClassFieldReader;
import org.drools.base.extractors.BaseNumberClassFieldReader;
import org.drools.base.extractors.BaseObjectClassFieldReader;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.MathUtils;
import org.drools.facttemplates.Fact;

public class SelfDateExtractor extends BaseDateClassFieldReader
    implements
    InternalReadAccessor,
    AcceptsClassObjectType,
    Externalizable {

    private static final long serialVersionUID = 510l;
    private ObjectType        objectType;

    public SelfDateExtractor() {
    }

    public SelfDateExtractor(final ObjectType objectType) {
        super(-1, ((ClassObjectType) objectType).getClassType(), objectType.getValueType() );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        objectType = (ObjectType) in.readObject();
        setIndex( -1 );
        setFieldType( ((ClassObjectType) objectType).getClassType() );
        setValueType( objectType.getValueType() );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( objectType );
    }
    
    public void setClassObjectType(ClassObjectType objectType) {
        this.objectType = objectType;
        setIndex( -1 );
        setFieldType( ((ClassObjectType) objectType).getClassType());
        setValueType( objectType.getValueType() );        
    }

    public Object getValue(InternalWorkingMemory workingMemory,
                           final Object object) {
        return object;
    }

    public ObjectType getObjectType() {
        return this.objectType;
    }


    public int hashCode() {
        return this.objectType.hashCode();
    }

    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( !(obj instanceof SelfDateExtractor) ) {
            return false;
        }
        final SelfDateExtractor other = (SelfDateExtractor) obj;
        return this.objectType.equals( other.objectType );
    }

    public boolean isGlobal() {
        return false;
    }

    public boolean isSelfReference() {
        return true;
    }

}
