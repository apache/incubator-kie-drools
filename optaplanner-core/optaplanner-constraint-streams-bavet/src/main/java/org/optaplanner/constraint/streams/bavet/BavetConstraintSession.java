/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.constraint.streams.bavet.common.AbstractNode;
import org.optaplanner.constraint.streams.bavet.uni.ForEachUniNode;
import org.optaplanner.constraint.streams.common.inliner.AbstractScoreInliner;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;

public final class BavetConstraintSession<Score_ extends Score<Score_>> {

    private final AbstractScoreInliner<Score_> scoreInliner;
    private final Map<Class<?>, ForEachUniNode<Object>> declaredClassToNodeMap;
    private final AbstractNode[] nodes; // Indexed by nodeIndex

    private final Map<Class<?>, List<ForEachUniNode<Object>>> effectiveClassToNodeListMap;

    public BavetConstraintSession(AbstractScoreInliner<Score_> scoreInliner,
            Map<Class<?>, ForEachUniNode<Object>> declaredClassToNodeMap,
            AbstractNode[] nodes) {
        this.scoreInliner = scoreInliner;
        this.declaredClassToNodeMap = declaredClassToNodeMap;
        this.nodes = nodes;
        effectiveClassToNodeListMap = new HashMap<>(declaredClassToNodeMap.size());
    }

    public List<ForEachUniNode<Object>> findNodeList(Class<?> factClass) {
        return effectiveClassToNodeListMap.computeIfAbsent(factClass, key -> {
            List<ForEachUniNode<Object>> nodeList = new ArrayList<>();
            declaredClassToNodeMap.forEach((declaredClass, declaredNode) -> {
                if (declaredClass.isAssignableFrom(factClass)) {
                    nodeList.add(declaredNode);
                }
            });
            return nodeList;
        });
    }

    public void insert(Object fact) {
        Class<?> factClass = fact.getClass();
        List<ForEachUniNode<Object>> nodeList = findNodeList(factClass);
        for (ForEachUniNode<Object> node : nodeList) {
            node.insert(fact);
        }
    }

    public void update(Object fact) {
        Class<?> factClass = fact.getClass();
        List<ForEachUniNode<Object>> nodeList = findNodeList(factClass);
        for (ForEachUniNode<Object> node : nodeList) {
            node.update(fact);
        }
    }

    public void retract(Object fact) {
        Class<?> factClass = fact.getClass();
        List<ForEachUniNode<Object>> nodeList = findNodeList(factClass);
        for (ForEachUniNode<Object> node : nodeList) {
            node.retract(fact);
        }
    }

    public Score_ calculateScore(int initScore) {
        for (AbstractNode node : nodes) {
            node.calculateScore();
        }
        return scoreInliner.extractScore(initScore);
    }

    public Map<String, ConstraintMatchTotal<Score_>> getConstraintMatchTotalMap() {
        return scoreInliner.getConstraintMatchTotalMap();
    }

    public Map<Object, Indictment<Score_>> getIndictmentMap() {
        return scoreInliner.getIndictmentMap();
    }

}
