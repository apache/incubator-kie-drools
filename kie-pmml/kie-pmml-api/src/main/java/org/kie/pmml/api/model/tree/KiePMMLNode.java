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
package org.kie.pmml.api.model.tree;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;

import org.kie.pmml.api.model.tree.predicates.KiePMMLPredicate;

public class KiePMMLNode implements Serializable {

    private static final long serialVersionUID = 8447087369287427969L;

    private String id;
    private String score;
    private List<KiePMMLPredicate> kiePMMLPredicates;
    private List<KiePMMLNode> kiePMMLNodes;

    public static Builder builder() {
        return new Builder();
    }

    public Optional<String> evaluate(Map<String, Object> values) {
        BinaryOperator<Optional<Boolean>> binaryOperator = getBooleanOperator();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            if (kiePMMLPredicates != null) {
                Optional<Boolean> evaluation = Optional.empty();
                for (KiePMMLPredicate kiePMMLPredicate : kiePMMLPredicates) {
                    evaluation = binaryOperator.apply(Optional.empty(), kiePMMLPredicate.evaluate(Collections.singletonMap(entry.getKey(), entry.getValue())));
                }
                // If there is a matching predicates, ev
                if (evaluation.isPresent() || evaluation.get()) {
                    if (kiePMMLNodes != null) {
                        for (KiePMMLNode kiePMMLNode : kiePMMLNodes) {
                            Optional<String> nodeEvaluation = kiePMMLNode.evaluate(values);
                            if (nodeEvaluation.isPresent()) {
                                return nodeEvaluation;
                            }
                        }
                    }
                }
            }
            if (kiePMMLNodes != null) {
                for (KiePMMLNode kiePMMLNode : kiePMMLNodes) {
                    Optional<String> evaluation = kiePMMLNode.evaluate(values);
                    if (evaluation.isPresent()) {
                        return evaluation;
                    }
                }
            }
        }
       return Optional.empty();
    }

    public String getId() {
        return id;
    }

    public String getScore() {
        return score;
    }

    public List<KiePMMLPredicate> getKiePMMLPredicates() {
        return kiePMMLPredicates;
    }

    public List<KiePMMLNode> getKiePMMLNodes() {
        return kiePMMLNodes;
    }

    private BinaryOperator<Optional<Boolean>> getBooleanOperator() {
        return (aBoolean, aBoolean2) -> {
            if (!aBoolean.isPresent()) {
                return aBoolean2;
            } else
                return aBoolean2.map(value -> Optional.of(aBoolean.get() && aBoolean2.get())).orElse(aBoolean);
        };
    }

    private KiePMMLNode() {
    }

    public static class Builder {

        private KiePMMLNode toBuild;

        private Builder() {
            this.toBuild = new KiePMMLNode();
        }

        public KiePMMLNode build() {
            return toBuild;
        }

        public Builder withId(String id) {
            toBuild.id = id;
            return this;
        }

        public Builder withScore(String score) {
            toBuild.score = score;
            return this;
        }

        public Builder withKiePMMLPredicates(List<KiePMMLPredicate> kiePMMLPredicates) {
            toBuild.kiePMMLPredicates = kiePMMLPredicates;
            return this;
        }

        public Builder withKiePMMLNodes(List<KiePMMLNode> kiePMMLNodes) {
            toBuild.kiePMMLNodes = kiePMMLNodes;
            return this;
        }
    }
}
