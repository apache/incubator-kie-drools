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
import java.util.Map;

import org.kie.pmml.api.model.KiePMMLExtension;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.abstracts.KiePMMLIDedExtensioned;
import org.kie.pmml.api.model.tree.predicates.KiePMMLPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLSegment extends KiePMMLIDedExtensioned {

    private static final long serialVersionUID = 8447087369287427969L;
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSegment.class);

    private double weight = 1;
    private final KiePMMLPredicate kiePMMLPredicate;
    private final KiePMMLModel model;

    /**
     * Builder to auto-generate the <b>id</b>
     * @return
     */
    public static Builder builder(List<KiePMMLExtension> extensions, KiePMMLPredicate kiePMMLPredicate, KiePMMLModel model) {
        return new Builder(extensions, kiePMMLPredicate, model);
    }

    public boolean evaluate(Map<String, Object> values) {
//        result = null;
//        logger.info(String.format("%s: evaluate %s", id, this.score));
//        if (kiePMMLPredicate != null && kiePMMLPredicate.evaluate(values)) {
//            logger.info(String.format("%s: matching predicate, evaluating... ", id));
//            logger.info(String.format("%s: preliminary set %s", id, score));
//            result = score;
//            if (kiePMMLNodes != null) {
//                for (KiePMMLSegment kiePMMLNode : kiePMMLNodes) {
//                    if (kiePMMLNode.evaluate(values)) {
//                        logger.info(String.format("%s: matching node, update set %s", id, kiePMMLNode.result));
//                        result = kiePMMLNode.result;
//                        break;
//                    }
//                }
//            }
//            return true;
//        }
//        logger.info(String.format("%s: no matching predicate, set %s", id, result));
        return false;
    }

    private KiePMMLSegment(KiePMMLPredicate kiePMMLPredicate, KiePMMLModel model) {
        this.kiePMMLPredicate = kiePMMLPredicate;
        this.model = model;
    }

    public static class Builder extends KiePMMLIDedExtensioned.Builder<KiePMMLSegment> {

        private Builder(List<KiePMMLExtension> extensions, KiePMMLPredicate kiePMMLPredicate, KiePMMLModel model) {
            super(extensions, "Segmentation-", () -> new KiePMMLSegment(kiePMMLPredicate, model));
            kiePMMLPredicate.setParentId(toBuild.id);
            model.setParentId(toBuild.id);
        }

        public Builder withWeight(double weight) {
            toBuild.weight = weight;
            return this;
        }
    }
}
