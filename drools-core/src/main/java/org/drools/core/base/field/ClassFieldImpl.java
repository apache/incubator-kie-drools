/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.base.field;

import org.drools.core.common.DroolsObjectInput;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.FieldValue;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ClassFieldImpl implements FieldValue, Externalizable {

    private Class type;
    private String className;

    public ClassFieldImpl( Class value ) {
        className = value.getName();
        type = value;
    }

    public ClassFieldImpl(String value) {
        className = value;
        try {
            type = Class.forName(className);
        } catch (ClassNotFoundException e) { }
    }

    public ClassFieldImpl() {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( className );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        className = in.readUTF();
        try {
            type = in instanceof DroolsObjectInput ?
                   Class.forName( className, false, ( (DroolsObjectInput) in ).getClassLoader() ) :
                   Class.forName( className );
        } catch (ClassNotFoundException e) { }
    }

    public Object getValue() {
        return type;
    }

    public Object resolve( InternalWorkingMemory workingMemory ) {
        try {
            type = workingMemory.getKnowledgeBase().getRootClassLoader().loadClass( className );
        } catch (Exception e) {

        }
        return type;
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    @Override
    public boolean equals( Object obj ) {
        return obj instanceof ClassFieldImpl && className.equals( ( (ClassFieldImpl) obj ).className );
    }

    public char getCharValue() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public BigDecimal getBigDecimalValue() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public BigInteger getBigIntegerValue() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int getIntValue() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public byte getByteValue() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public short getShortValue() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long getLongValue() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public float getFloatValue() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public double getDoubleValue() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean getBooleanValue() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isNull() {
        return className == null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isBooleanField() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isIntegerNumberField() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isFloatNumberField() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isObjectField() {
        return true;
    }

    public boolean isCollectionField() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isStringField() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }



}
