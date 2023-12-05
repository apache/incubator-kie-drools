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
package org.kie.pmml.commons.model.predicates;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLTruePredicate extends KiePMMLPredicate {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTruePredicate.class);

    private static final String TRUE = "True";
    private static final long serialVersionUID = 4217223133097672810L;

    public KiePMMLTruePredicate(String name, List<KiePMMLExtension> extensions) {
        super(name, extensions);
    }

    /**
     * Builder to auto-generate the <b>id</b>
     * @return
     */
    public static Builder builder(List<KiePMMLExtension> extensions) {
        return new Builder(extensions);
    }

    @Override
    public String getName() {
        return TRUE;
    }

    @Override
    public boolean evaluate(Map<String, Object> values) {
        logger.trace("{} evaluate {}", this, values);
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
            super("TruePredicate-", () -> new KiePMMLTruePredicate("TruePredicate", extensions));
        }
    }
}