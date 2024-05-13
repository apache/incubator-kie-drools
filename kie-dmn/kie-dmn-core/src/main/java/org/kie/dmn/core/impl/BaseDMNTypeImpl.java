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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.util.NumberEvalHelper;

/**
 * @see DMNType
 */
public abstract class BaseDMNTypeImpl
        implements DMNType {

    private String          namespace;
    private String          name;
    private String          id;
    private boolean         collection;
    private List<UnaryTest> allowedValues;
    private List<UnaryTest> typeConstraint;
    private DMNType         baseType;
    private Type            feelType;
    private DMNType         belongingType;

    public BaseDMNTypeImpl(String namespace, String name, String id, boolean collection, DMNType baseType, Type feelType) {
        this.namespace = namespace;
        this.name = name;
        this.id = id;
        this.collection = collection;
        this.feelType = feelType;
        this.baseType = baseType;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isCollection() {
        return collection;
    }

    @Override
    public Map<String, DMNType> getFields() {
        return Collections.emptyMap();
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public List<DMNUnaryTest> getAllowedValues() {
        return allowedValues != null ? Collections.unmodifiableList(allowedValues) : Collections.emptyList();
    }

    @Override
    public List<DMNUnaryTest> getTypeConstraint() {
        return typeConstraint != null ? Collections.unmodifiableList(typeConstraint) : Collections.emptyList();
    }
    
    public List<UnaryTest> getAllowedValuesFEEL() {
        return allowedValues;
    }

    public void setAllowedValues(List<UnaryTest> allowedValues) {
        this.allowedValues = allowedValues;
    }

    public List<UnaryTest> getTypeConstraintFEEL() {
        return typeConstraint;
    }

    public void setTypeConstraint(List<UnaryTest> typeConstraints) {
        this.typeConstraint = typeConstraints;
    }


    @Override
    public DMNType getBaseType() {
        return baseType;
    }

    public void setBaseType(DMNType baseType) {
        this.baseType = baseType;
    }

    public abstract BaseDMNTypeImpl clone();

    public void setFeelType(Type feelType) {
        this.feelType = feelType;
    }

    public Type getFeelType() {
        return feelType;
    }

    @Override
    public String toString() {
        return "DMNType{ "+getNamespace()+" : "+getName()+" }";
    }

    @Override
    public boolean isInstanceOf(Object value) {
        if ( value == null ) {
            return false; // See FEEL specifications Table 49.
        }
        Object toCheck = getObjectToCheck(value);
        // try first to recurse in case of Collection..
        if ( isCollection() && toCheck instanceof Collection elements) {
            for ( Object e : elements ) {
                // Do not dig inside collection for typeConstraint check
                if ( !internalAllowedValueIsInstanceOf(e) || !valueMatchesInUnaryTests(allowedValues, e) ) {
                    return false;
                }
            }
            return true;
        } 
        // .. normal case
        boolean instanceOfAllowedValue = internalAllowedValueIsInstanceOf(toCheck);
        // Also check typeConstraint for not-collection values
        boolean instanceOfTypeConstraint = internalTypeConstraintIsInstanceOf(toCheck);
        // Also check typeConstraint for not-collection values
        return (instanceOfAllowedValue && valueMatchesInUnaryTests(allowedValues, toCheck))
                && (instanceOfTypeConstraint && valueMatchesInUnaryTests(typeConstraint, toCheck));
    }

    private Object getObjectToCheck(Object value) {
        // spec defines that "a=[a]", i.e., singleton collections should be treated as the single element
        // and vice-versa
        // For isCollection type, a single element can be converted to singleton collection
        if (isCollection() && !(value instanceof Collection)) {
            return Collections.singletonList(value);
        }
        if (!isCollection() && (value instanceof Collection collection) && collection.size() == 1) {
            return collection.iterator().next();
        }
        return value;
    }

    private boolean valueMatchesInUnaryTests(List<UnaryTest> unaryTests,  Object o) {
        if ( unaryTests == null || unaryTests.isEmpty() ) {
            return true;
        } else {
            return DMNFEELHelper.valueMatchesInUnaryTests(unaryTests, NumberEvalHelper.coerceNumber(o), null);
        }
    }

    protected abstract boolean internalIsInstanceOf(Object o);

    protected boolean internalAllowedValueIsInstanceOf(Object o) {
        return internalIsInstanceOf(o);
    }

    protected boolean internalTypeConstraintIsInstanceOf(Object o) {
        return internalIsInstanceOf(o);
    }

    
    @Override
    public boolean isAssignableValue(Object value) {
        if (value == null && allowedValues == null && typeConstraint == null) {
            return true; // a null-value can be assigned to any type.
        }
        Object toCheck = getObjectToCheck(value);
        // try first to recurse in case of Collection..
        if ( isCollection() && toCheck instanceof Collection elements) {
            for ( Object e : elements ) {
                // Do not dig inside collection for typeContraint check
                if ( !internalAllowedValueIsAssignableValue(e) || !valueMatchesInUnaryTests(allowedValues, e) ) {
                    return false;
                }
            }
            // If it is a collection, we have to check the typeConstraint on the whole object
            return internalTypeConstraintIsAssignableValue(toCheck) && valueMatchesInUnaryTests(typeConstraint, toCheck);
        } 
        // .. normal case
        boolean assignableAllowedValue = internalAllowedValueIsAssignableValue(toCheck);
        // Also check typeConstraint for not-collection values
        boolean assignableTypeConstraint = internalTypeConstraintIsAssignableValue(toCheck);

        return (assignableAllowedValue && valueMatchesInUnaryTests(allowedValues, toCheck)) &&
                (assignableTypeConstraint && valueMatchesInUnaryTests(typeConstraint, toCheck));
    }

    /**
     * This method relies mostly on <code>baseType</code>
     * Different implementations may provide/extend the logic
     * @param o
     * @return
     */
    protected abstract boolean internalAllowedValueIsAssignableValue(Object o);

    /**
     * This method relies mostly on <code>feelType</code>
     * Different implementations may provide/extend the logic, mostly depending on <code>isCollection</code> and, eventually, if a <code>MapBackedType</code> is provided
     * @param o
     * @return
     */
    protected abstract boolean internalTypeConstraintIsAssignableValue(Object o);


    public void setBelongingType(DMNType belongingType) {
        this.belongingType = belongingType;
    }
    
    public DMNType getBelongingType() {
        return this.belongingType;
    }
}
