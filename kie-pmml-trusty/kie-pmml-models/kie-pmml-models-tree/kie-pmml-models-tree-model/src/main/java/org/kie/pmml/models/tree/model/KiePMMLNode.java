/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.pmml.models.tree.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.kie.pmml.commons.model.predicates.KiePMMLPredicate;

public class KiePMMLNode extends AbstractKiePMMLComponent {

    protected List<KiePMMLNode> nodes = new LinkedList<>();
    protected KiePMMLPredicate predicate;
    protected Object score;


    protected KiePMMLNode(String name, List<KiePMMLExtension> extensions, KiePMMLPredicate predicate, Object score) {
        super(name, extensions);
        this.predicate = predicate;
        this.score = score;
    }

    public Object evaluate(final Map<String, Object> requestData) {
        System.out.println("KiePMMLNode  " + getName() + " evaluate");
        if (!evaluatePredicate(requestData)) {
            System.out.println("KiePMMLNode  " + "return null");
            return null;
        }
        if (nodes.isEmpty()) {
            System.out.println("KiePMMLNode  " + "Empty node, return " + score);
            return score;
        }
        Optional<Object> nestedScore = getNestedScore(requestData);
        System.out.println("KiePMMLNode  " + "nestedScore present ? " + nestedScore.isPresent());
        if (nestedScore.isPresent()) {
            System.out.println("KiePMMLNode  " + "nestedScore  " + nestedScore.get());
        }
        Object toReturn = nestedScore.orElse(score);
        System.out.println("KiePMMLNode  " +  getName() + " return  " + toReturn);
        return toReturn;
    }

    public List<KiePMMLNode> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public KiePMMLPredicate getPredicate() {
        return predicate;
    }

    public Object getScore() {
        return score;
    }

    protected Optional<Object> getNestedScore(final Map<String, Object> requestData) {
        Optional<Object> toReturn = Optional.empty();
        for (KiePMMLNode nestedNode : nodes) {
            final Object evaluation = nestedNode.evaluate(requestData);
            System.out.println("Evaluation of " + nestedNode.getName() + " --> " + evaluation);
            toReturn = Optional.ofNullable(evaluation);
            if (toReturn.isPresent()) {
                System.out.println("Break ");
                break;
            }
        }
        return toReturn;
    }

    protected boolean evaluatePredicate(final Map<String, Object> requestData) {
        return predicate.evaluate(requestData);
    }
}
