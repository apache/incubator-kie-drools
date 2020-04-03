/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.models.drooled.tuples;

import java.util.Objects;

/**
 * Tupla representing the name of a field and its <code>KiePMMLOperatorValue</code>
 */
public class KiePMMLFieldOperatorValue {

    private final String name;
    private final KiePMMLOperatorValue kiePMMLOperatorValue;

    public KiePMMLFieldOperatorValue(final String name, String operator, Object value) {
        this.name = name;
        this.kiePMMLOperatorValue = new KiePMMLOperatorValue(operator, value);
    }

    public String getName() {
        return name;
    }

    public KiePMMLOperatorValue getKiePMMLOperatorValue() {
        return kiePMMLOperatorValue;
    }

    @Override
    public String toString() {
        return "KiePMMLFieldOperatorValue{" +
                "name='" + name + '\'' +
                ", kiePMMLOperatorValue=" + kiePMMLOperatorValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLFieldOperatorValue that = (KiePMMLFieldOperatorValue) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(kiePMMLOperatorValue, that.kiePMMLOperatorValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, kiePMMLOperatorValue);
    }
}
