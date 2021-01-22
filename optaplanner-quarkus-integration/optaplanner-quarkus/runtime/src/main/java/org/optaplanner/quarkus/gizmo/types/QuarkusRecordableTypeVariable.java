/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.quarkus.gizmo.types;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Objects;

import org.jboss.jandex.IndexView;
import org.optaplanner.quarkus.gizmo.annotations.QuarkusRecordableAnnotatedElement;

public class QuarkusRecordableTypeVariable extends QuarkusRecordableAnnotatedElement implements TypeVariable {

    Type[] bounds;
    String name;

    public QuarkusRecordableTypeVariable() {
    }

    public QuarkusRecordableTypeVariable(org.jboss.jandex.TypeVariable typeVariable, IndexView indexView) {
        super(typeVariable, indexView);

        this.bounds = typeVariable.bounds().stream()
                .map(type -> QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(type, indexView))
                .toArray(Type[]::new);
    }

    public QuarkusRecordableTypeVariable(org.jboss.jandex.UnresolvedTypeVariable typeVariable, IndexView indexView) {
        super(typeVariable, indexView);
        this.bounds = new Type[0];
    }

    @Override
    public Type[] getBounds() {
        return bounds;
    }

    public void setBounds(Type[] bounds) {
        this.bounds = bounds;
    }

    @Override
    public GenericDeclaration getGenericDeclaration() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public AnnotatedType[] getAnnotatedBounds() {
        return new AnnotatedType[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuarkusRecordableTypeVariable that = (QuarkusRecordableTypeVariable) o;
        return Arrays.equals(bounds, that.bounds) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(bounds);
        return result;
    }
}
