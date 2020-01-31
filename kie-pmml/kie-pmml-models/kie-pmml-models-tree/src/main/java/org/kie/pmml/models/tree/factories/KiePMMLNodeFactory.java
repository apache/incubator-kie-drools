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
package org.kie.pmml.models.tree.factories;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.tree.BranchNode;
import org.dmg.pmml.tree.ClassifierNode;
import org.dmg.pmml.tree.ComplexNode;
import org.dmg.pmml.tree.Node;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.tree.KiePMMLNode;

import static org.kie.pmml.api.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;
import static org.kie.pmml.models.core.factories.KiePMMLPredicateFactory.getPredicate;

public class KiePMMLNodeFactory {

    private static final Logger log = Logger.getLogger(KiePMMLNodeFactory.class.getName());



    public static List<KiePMMLNode> getNodes(List<Node> nodes, DataDictionary dataDictionary) throws KiePMMLException {
        log.info("getNodes " + nodes);
        return nodes.stream().map(throwingFunctionWrapper(node -> getNode(node, dataDictionary))).collect(Collectors.toList());
    }

    public static KiePMMLNode getNode(Node node, DataDictionary dataDictionary) throws KiePMMLException {
        log.info("getNode " + node);
        KiePMMLNode.Builder builder = KiePMMLNode.builder()
                .withScore(node.getScore().toString())
                .withKiePMMLPredicate(getPredicate(node.getPredicate(), dataDictionary));
        if (node instanceof BranchNode || node instanceof ClassifierNode || node instanceof ComplexNode) {
            builder = builder.withKiePMMLNodes(getNodes(node.getNodes(), dataDictionary));
        }
        return builder
                .build();
    }

    private KiePMMLNodeFactory() {
    }
}
