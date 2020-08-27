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
package org.kie.pmml.commons.model.expressions;

import java.util.List;
import java.util.Objects;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;

public class KiePMMLFieldRef extends AbstractKiePMMLComponent implements KiePMMLExpression {

    private final String mapMissingTo;

    public KiePMMLFieldRef(String name, List<KiePMMLExtension> extensions, String mapMissingTo) {
        super(name, extensions);
        this.mapMissingTo = mapMissingTo;
    }

    public String getMapMissingTo() {
        return mapMissingTo;
    }

    @Override
    public String toString() {
        return "KiePMMLFieldRef{" +
                "mapMissingTo='" + mapMissingTo + '\'' +
                ", extensions=" + extensions +
                ", name='" + name + '\'' +
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
        if (!super.equals(o)) {
            return false;
        }
        KiePMMLFieldRef that = (KiePMMLFieldRef) o;
        return Objects.equals(mapMissingTo, that.mapMissingTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mapMissingTo);
    }
}
