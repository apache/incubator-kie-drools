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
package org.drools.model.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drools.model.AnnotationValue;
import org.drools.model.TypeMetaData;

import static java.util.Comparator.comparing;

public class TypeMetaDataImpl implements TypeMetaData, ModelComponent {

    private final Class<?> type;
    private final String pkg;
    private final String name;
    private final Map<String, AnnotationValue[]> annotations = new HashMap<>();

    public TypeMetaDataImpl( Class<?> type ) {
        this.type = type;
        this.pkg = type.getPackage().getName();
        this.name = type.getSimpleName();
    }

    @Override
    public String getPackage() {
        return pkg;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public Map<String, AnnotationValue[]> getAnnotations() {
        return annotations;
    }

    public TypeMetaDataImpl addAnnotation( String name, AnnotationValue... values) {
        annotations.put(name, values);
        return this;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        TypeMetaDataImpl other = ( TypeMetaDataImpl ) o;

        if ( !pkg.equals( other.pkg ) ) return false;
        if ( !name.equals( other.name ) ) return false;

        Field[] thisFields = type.getDeclaredFields();
        Field[] otherFields = other.type.getDeclaredFields();

        if ( thisFields.length != otherFields.length ) return false;

        Arrays.sort( thisFields, comparing( Field::getName ) );
        Arrays.sort( otherFields, comparing( Field::getName ) );

        for ( int i = 0; i < thisFields.length; i++ ) {
            if ( ! (thisFields[i].getName().equals( otherFields[i].getName() ) && thisFields[i].getType().equals( otherFields[i].getType() )) ) {
                return false;
            }
        }

        return true;
    }
}
