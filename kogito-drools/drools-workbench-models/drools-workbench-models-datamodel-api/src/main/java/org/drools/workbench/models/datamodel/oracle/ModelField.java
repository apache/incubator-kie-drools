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

package org.drools.workbench.models.datamodel.oracle;

public class ModelField {

    public static enum FIELD_CLASS_TYPE {
        REGULAR_CLASS,
        TYPE_DECLARATION_CLASS
    }

    public static enum FIELD_ORIGIN {
        SELF,
        DECLARED,
        DELEGATED,
        INHERITED
    }

    private String name;
    private String className;
    private FieldAccessorsAndMutators accessorsAndMutators;

    /**
     * parametrized type of clazz
     */
    private String type;

    private FIELD_CLASS_TYPE classType;

    private FIELD_ORIGIN fieldOrigin;

    public ModelField() {
    }

    /**
     * Creates a new ModelField instance
     * @param name field's name
     * @param clazz the class of the field. For fields defined as a type declaration
     * @param fieldClassType tells if this is a field for a regular POJO class or for a object type declaration
     * this clazz should be null.
     * @param fieldOrigin gives info about this field's origin
     * @param accessorsAndMutators Whether the field has an Accessor, Mutator or both
     * @param type the type of the clazz.
     */
    public ModelField( final String name,
                       final String clazz,
                       final FIELD_CLASS_TYPE fieldClassType,
                       final FIELD_ORIGIN fieldOrigin,
                       final FieldAccessorsAndMutators accessorsAndMutators,
                       final String type ) {
        this.name = name;
        this.classType = fieldClassType;
        this.fieldOrigin = fieldOrigin;
        this.className = clazz;
        this.accessorsAndMutators = accessorsAndMutators;
        this.type = type;
    }

    public String getClassName() {
        return this.className;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public FIELD_CLASS_TYPE getClassType() {
        return classType;
    }

    public FIELD_ORIGIN getOrigin() {
        return fieldOrigin;
    }

    public FieldAccessorsAndMutators getAccessorsAndMutators() {
        return accessorsAndMutators;
    }

    @Override
    public String toString() {
        return "ModelField [classType=" + classType
                + ", name=" + name
                + ", type=" + type
                + ", className=" + className
                + ", origin=" + fieldOrigin
                + ", accessorsAndMutators=" + accessorsAndMutators
                + "]";
    }

}
