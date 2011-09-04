/*
 * Copyright 2011 JBoss Inc
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

package org.drools.base.field;

import org.drools.base.TypeResolver;
import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.spi.FieldValue;

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
        type = null;
    }

    public ClassFieldImpl(String value) {
        className = value;
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( type );
        out.writeObject( className );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        type = (Class) in.readObject();
        className = (String) in.readObject();
    }

    public Object getValue() {
        return type;
    }

    public Object resolve( InternalWorkingMemory workingMemory ) {
        try {
            type = ((ReteooRuleBase) workingMemory.getRuleBase()).getRootClassLoader().loadClass( className, true );
        } catch (Exception e) {

        }
        return type;
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
