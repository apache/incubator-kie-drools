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
package org.kie.pmml.models.tree.api.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.kie.pmml.api.model.abstracts.KiePMMLIDed;
import org.kie.pmml.models.tree.api.model.predicates.KiePMMLPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLNode extends KiePMMLIDed {

    private static final long serialVersionUID = 8447087369287427969L;
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLNode.class);

    private String score;
    private String result;
    private KiePMMLPredicate kiePMMLPredicate;
    private List<KiePMMLNode> kiePMMLNodes;

    /**
     * Builder to auto-generate the <b>id</b>
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    public boolean evaluate(Map<String, Object> values) {
        result = null;
        logger.info("{}: evaluate {}", id, this.score);
        if (kiePMMLPredicate != null && kiePMMLPredicate.evaluate(values)) {
            logger.info("{}: matching predicate, evaluating... ", id);
            logger.info("{}: preliminary set {}", id, score);
            result = score;
            if (kiePMMLNodes != null) {
                for (KiePMMLNode kiePMMLNode : kiePMMLNodes) {
                    if (kiePMMLNode.evaluate(values)) {
                        logger.info("{}: matching node, update set {}", id, kiePMMLNode.result);
                        result = kiePMMLNode.result;
                        break;
                    }
                }
            }
            return true;
        }
        logger.info("{}: no matching predicate, set {}", id, result);
        return false;
    }

    public String getScore() {
        return score;
    }

    public String getResult() {
        return result;
    }

    public KiePMMLPredicate getKiePMMLPredicate() {
        return kiePMMLPredicate;
    }

    public List<KiePMMLNode> getKiePMMLNodes() {
        return kiePMMLNodes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KiePMMLNode that = (KiePMMLNode) o;

        if (!Objects.equals(id, that.id)) {
            return false;
        }
        if (!Objects.equals(score, that.score)) {
            return false;
        }
        if (!Objects.equals(kiePMMLPredicate, that.kiePMMLPredicate)) {
            return false;
        }
        return Objects.equals(kiePMMLNodes, that.kiePMMLNodes);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (kiePMMLPredicate != null ? kiePMMLPredicate.hashCode() : 0);
        result = 31 * result + (kiePMMLNodes != null ? kiePMMLNodes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "KiePMMLNode{" +
                "id='" + id + '\'' +
                ", score='" + score + '\'' +
                ", kiePMMLPredicate=" + kiePMMLPredicate +
                ", kiePMMLNodes=" + kiePMMLNodes +
                '}';
    }

    public static class Builder extends KiePMMLIDed.Builder<KiePMMLNode> {

        private Builder() {
            super("Node-", KiePMMLNode::new);
        }

        public Builder withScore(String score) {
            toBuild.score = score;
            return this;
        }

        public Builder withKiePMMLPredicate(KiePMMLPredicate kiePMMLPredicate) {
            kiePMMLPredicate.setParentId(toBuild.id);
            toBuild.kiePMMLPredicate = kiePMMLPredicate;
            return this;
        }

        public Builder withKiePMMLNodes(List<KiePMMLNode> kiePMMLNodes) {
            // TODO {gcardosi} fix this
            kiePMMLNodes.forEach(node -> node.parentId = toBuild.id);
            toBuild.kiePMMLNodes = kiePMMLNodes;
            return this;
        }
    }
}
