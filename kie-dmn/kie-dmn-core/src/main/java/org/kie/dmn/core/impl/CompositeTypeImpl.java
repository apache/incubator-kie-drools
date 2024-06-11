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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.GenListType;
import org.kie.dmn.feel.util.EvalHelper;
import org.kie.dmn.feel.util.EvalHelper.PropertyValueResult;
import org.kie.dmn.feel.util.NumberEvalHelper;

/**
 * @see DMNType
 */
public class CompositeTypeImpl
        extends BaseDMNTypeImpl {

    private final Map<String, DMNType> fields;

    public CompositeTypeImpl() {
        this( null, null, null, false, new LinkedHashMap<>(  ), null, null );
    }

    public CompositeTypeImpl(String namespace, String name, String id) {
        this( namespace, name, id, false, new LinkedHashMap<>(  ), null, null );
    }

    public CompositeTypeImpl(String namespace, String name, String id, boolean isCollection) {
        this( namespace, name, id, isCollection, new LinkedHashMap<>(  ), null, null );
    }

    public CompositeTypeImpl(String namespace, String name, String id, boolean isCollection, Map<String, DMNType> fields, DMNType baseType, Type feelType ) {
        super( namespace, name, id, isCollection, baseType, feelType );
        this.fields = fields;
        if( feelType == null ) {
            feelType = new MapBackedType( name );
            setFeelType( feelType );
            if( fields != null ) {
                for( Map.Entry<String, DMNType> field : fields.entrySet() ) {
                    ((MapBackedType) feelType).addField( field.getKey(), ((BaseDMNTypeImpl)field.getValue()).getFeelType() );
                }
            }
            if (isCollection) {
                setFeelType(new GenListType(getFeelType()));
            }
        }
    }

    public Map<String, DMNType> getFields() {
        return Collections.unmodifiableMap( fields );
    }

    public void addField( String name, DMNType type ) {
        this.fields.put( name, type );
        MapBackedType mbType = !isCollection() ? (MapBackedType) getFeelType() : (MapBackedType) ((GenListType) getFeelType()).getGen();
        mbType.addField(name, ((BaseDMNTypeImpl) type).getFeelType());
    }

    public String toString(Object value) {
        return null;
    }

    @Override
    public boolean isComposite() {
        return true;
    }

    @Override
    public CompositeTypeImpl clone() {
        return new CompositeTypeImpl( getNamespace(), getName(), getId(), isCollection(), new LinkedHashMap<>( fields), getBaseType(), getFeelType() );
    }

    @Override
    protected boolean internalIsInstanceOf(Object o) {
        if (getBaseType() != null) {
            return getBaseType().isInstanceOf(o);
        } else if (o instanceof Map<?, ?>) {
            Map<?, ?> instance = (Map<?, ?>) o;
            for ( Entry<String, DMNType> f : fields.entrySet() ) {
                if ( !instance.containsKey(f.getKey()) ) {
                    return false; // It must have key named 'f.getKey()' like a Duck.
                } else {
                    if ( !f.getValue().isInstanceOf(instance.get(f.getKey())) ) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            for ( Entry<String, DMNType> f : fields.entrySet() ) {
                Method getter = EvalHelper.getGenericAccessor( o.getClass(), f.getKey() );
                if ( getter != null ) {
                    Object invoked;
                    try {
                        invoked = getter.invoke( o );
                    } catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException e ) {
                        return false;
                    }
                    Object fieldValue = NumberEvalHelper.coerceNumber(invoked );
                    if ( !f.getValue().isInstanceOf( fieldValue ) ) {
                        return false;
                    }
                } else {
                    return false; // It must <genericAccessor> like a Duck.
                }
            }
            return true;
        }
    }

    @Override
    protected boolean internalAllowedValueIsAssignableValue(Object o) {
        if (getBaseType() != null) {
            return getBaseType().isAssignableValue(o);
        } else {
            return internalCheckObject(o);
        }
    }

    @Override
    protected boolean internalTypeConstraintIsAssignableValue(Object o) {
        return checkByCollection(o) || checkByElement(o);
    }

    private boolean internalCheckObject(Object o) {
        return o == null || checkByMap(o) || checkByFields(o);
    }

    private boolean checkByCollection(Object o) {
        // the check of contained elements is done inside BaseDMNType.isAssignableValue
        // Here we have only to confirm a) current feel type is a list and b) given object is a collection
        return isCollection() && getFeelType() instanceof GenListType && o instanceof Collection;
    }

    private boolean checkByElement(Object o) {
        return getFeelType() instanceof MapBackedType ? checkByFields(o) : getFeelType().isAssignableValue(o);
    }

    private boolean checkByMap(Object o) {
        if (o instanceof Map<?, ?> instance) {
            for (Entry<String, DMNType> f : fields.entrySet()) {
                if (!instance.containsKey(f.getKey())) {
                    return false; // It must have key named 'f.getKey()' like a Duck.
                } else {
                    if (!f.getValue().isAssignableValue(instance.get(f.getKey()))) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean checkByFields(Object o) {
        for (Entry<String, DMNType> f : fields.entrySet()) {
            PropertyValueResult fValue = EvalHelper.getDefinedValue(o, f.getKey());
            if (fValue.isDefined()) {
                Object valueResult = fValue.getValueResult().getOrElseThrow(IllegalStateException::new);
                DMNType expectedType = f.getValue();
                if (!expectedType.isAssignableValue(valueResult)) {
                    return false;
                }
            } else {
                return false; // It must <genericAccessor> like a Duck.
            }
        }
        return true;
    }
}
