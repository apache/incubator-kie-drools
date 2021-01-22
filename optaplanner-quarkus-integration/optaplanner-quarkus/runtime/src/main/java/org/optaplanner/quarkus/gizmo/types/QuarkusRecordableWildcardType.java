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

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;

import org.jboss.jandex.IndexView;

public class QuarkusRecordableWildcardType implements WildcardType {

    Type[] upperBounds;
    Type[] lowerBounds;

    public QuarkusRecordableWildcardType() {
    }

    public QuarkusRecordableWildcardType(org.jboss.jandex.WildcardType jandexType, IndexView indexView) {
        if (jandexType.superBound() != null) {
            this.lowerBounds =
                    new Type[] { QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(jandexType.superBound(), indexView) };
        } else {
            this.lowerBounds = new Type[] {};
        }
        this.upperBounds =
                new Type[] { QuarkusRecordableTypes.getQuarkusRecorderFriendlyType(jandexType.extendsBound(), indexView) };
    }

    public QuarkusRecordableWildcardType(Type[] upperBounds, Type[] lowerBounds) {
        this.upperBounds = upperBounds;
        this.lowerBounds = lowerBounds;
    }

    @Override
    public String getTypeName() {
        String suffix = "";
        if (lowerBounds.length != 0) {
            suffix = " implements " + lowerBounds[0].getTypeName();
        }
        return "? extends " + upperBounds[0].getTypeName() + suffix;
    }

    @Override
    public String toString() {
        return getTypeName();
    }

    @Override
    public Type[] getUpperBounds() {
        return upperBounds;
    }

    @Override
    public Type[] getLowerBounds() {
        return lowerBounds;
    }

    public void setUpperBounds(Type[] upperBounds) {
        this.upperBounds = upperBounds;
    }

    public void setLowerBounds(Type[] lowerBounds) {
        this.lowerBounds = lowerBounds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuarkusRecordableWildcardType that = (QuarkusRecordableWildcardType) o;
        return Arrays.equals(upperBounds, that.upperBounds) && Arrays.equals(lowerBounds, that.lowerBounds);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(upperBounds);
        result = 31 * result + Arrays.hashCode(lowerBounds);
        return result;
    }
}
