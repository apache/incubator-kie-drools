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
package org.kie.pmml.api.model.tree.predicates;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kie.pmml.api.model.KiePMMLExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/TreeModel.html#xsdElement_True>True</a>
 */
public class KiePMMLTruePredicate extends KiePMMLPredicate {

    private static final long serialVersionUID = -1996390505352151403L;
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTruePredicate.class);

    private final String name = "True";

    /**
     * Builder to auto-generate the <b>id</b>
     * @return
     */
    public static Builder builder(List<KiePMMLExtension> extensions) {
        return new Builder(extensions);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean evaluate(Map<String, Object> values) {
        logger.info("{} evaluate {}", this, values);
        return true;
    }

    @Override
    public String toString() {
        return "KiePMMLTruePredicate{" +
                "name='" + name + '\'' +
                ", extensions=" + extensions +
                ", id='" + id + '\'' +
                ", parentId='" + parentId + '\'' +
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
        KiePMMLTruePredicate that = (KiePMMLTruePredicate) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }

    public static class Builder extends KiePMMLPredicate.Builder<KiePMMLTruePredicate> {

        private Builder(List<KiePMMLExtension> extensions) {
            super(extensions, "TruePredicate-", KiePMMLTruePredicate::new);

        }
    }
}
