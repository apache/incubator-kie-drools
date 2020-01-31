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
package org.kie.pmml.api.model.mining.segmentation;

import java.util.List;
import java.util.Objects;

import org.kie.pmml.api.model.KiePMMLExtension;
import org.kie.pmml.api.model.abstracts.KiePMMLIDedExtensioned;
import org.kie.pmml.api.model.mining.enums.MULTIPLE_MODEL_METHOD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see <a href=http://dmg.org/pmml/v4-3/MultipleModels.html#xsdElement_Segmentation>Segmentation</a>
 */
public class KiePMMLSegmentation extends KiePMMLIDedExtensioned {

    private static final long serialVersionUID = 8447087369287427969L;
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSegmentation.class);

    private final MULTIPLE_MODEL_METHOD multipleModelMethod;
    private List<KiePMMLSegment> segments;

    /**
     * Builder to auto-generate the <b>id</b>
     * @return
     */
    public static Builder builder(List<KiePMMLExtension> extensions, MULTIPLE_MODEL_METHOD multipleModelMethod) {
        return new Builder(extensions, multipleModelMethod);
    }

    public MULTIPLE_MODEL_METHOD getMultipleModelMethod() {
        return multipleModelMethod;
    }

    @Override
    public String toString() {
        return "KiePMMLSegmentation{" +
                "multipleModelMethod=" + multipleModelMethod +
                ", segments=" + segments +
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
        KiePMMLSegmentation that = (KiePMMLSegmentation) o;
        return multipleModelMethod == that.multipleModelMethod &&
                Objects.equals(segments, that.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), multipleModelMethod, segments);
    }

    private KiePMMLSegmentation(MULTIPLE_MODEL_METHOD multipleModelMethod) {
        this.multipleModelMethod = multipleModelMethod;
    }

    public static class Builder extends KiePMMLIDedExtensioned.Builder<KiePMMLSegmentation> {

        private Builder(List<KiePMMLExtension> extensions, MULTIPLE_MODEL_METHOD multipleModelMethod) {
            super(extensions, "Segmentation-", () -> new KiePMMLSegmentation(multipleModelMethod));
        }

        public Builder withSegments(List<KiePMMLSegment> segments) {
            toBuild.segments = segments;
            return this;
        }
    }
}
