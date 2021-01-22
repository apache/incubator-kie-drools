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

package org.optaplanner.quarkus.gizmo.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class AbstractQuarkusRecordableAnnotation implements Annotation {

    public Map<String, Object> map;

    public AbstractQuarkusRecordableAnnotation() {
    }

    public AbstractQuarkusRecordableAnnotation(Map<String, Object> map) {
        this.map = map;
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(String parameterName, Class<T> type) {
        Object value = map.get(parameterName);
        if (!type.isArray() || type.isInstance(value)) {
            return (T) value;
        } else {
            // must be the case of having an empty array
            return (T) Array.newInstance(type.getComponentType(), 0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractQuarkusRecordableAnnotation that = (AbstractQuarkusRecordableAnnotation) o;

        // Cannot use map.equals since different arrays with same values are considered different
        if (map.size() != that.map.size()) {
            return false;
        }
        for (String key : map.keySet()) {
            Object myValue = map.get(key);
            Object theirValue = that.map.get(key);
            if (myValue.getClass().isArray()) {
                if (theirValue == null || !theirValue.getClass().isArray()
                        || !Arrays.deepEquals((Object[]) myValue, (Object[]) theirValue)) {
                    return false;
                }
            } else {
                if (!Objects.equals(myValue, theirValue)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    public String toString() {
        return annotationType().getSimpleName() +
                map.entrySet().stream().sorted(Map.Entry.comparingByKey())
                        .map(entry -> entry.getKey() + "={" + entry.getValue() + "}")
                        .collect(Collectors.joining(",", "(", ")"));
    }
}
