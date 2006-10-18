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

package org.drools.base.extractors;

import org.drools.RuntimeDroolsException;
import org.drools.base.BaseClassFieldExtractor;

/**
 * A Base class for primitive boolean class field
 * extractors. This class centralizes type conversions.
 *  
 * @author etirelli
 */
public abstract class BaseBooleanClassFieldExtractor extends BaseClassFieldExtractor {

    private static final long serialVersionUID = 9104214567753008212L;
    
    public BaseBooleanClassFieldExtractor(Class clazz,
                                          String fieldName) {
        super( clazz,
               fieldName );
    }

    public Object getValue(Object object) {
        return getBooleanValue( object ) ? Boolean.TRUE : Boolean.FALSE;
    }

    public abstract boolean getBooleanValue(Object object);

    public byte getByteValue(Object object) {
        throw new RuntimeDroolsException("Conversion to byte not supported from boolean");
    }

    public char getCharValue(Object object) {
        throw new RuntimeDroolsException("Conversion to char not supported from boolean");
    }

    public double getDoubleValue(Object object) {
        throw new RuntimeDroolsException("Conversion to double not supported from boolean");
    }

    public float getFloatValue(Object object) {
        throw new RuntimeDroolsException("Conversion to float not supported from boolean");
    }

    public int getIntValue(Object object) {
        throw new RuntimeDroolsException("Conversion to int not supported from boolean");
    }

    public long getLongValue(Object object) {
        throw new RuntimeDroolsException("Conversion to long not supported from boolean");
    }

    public short getShortValue(Object object) {
        throw new RuntimeDroolsException("Conversion to short not supported from boolean");
    }

}
