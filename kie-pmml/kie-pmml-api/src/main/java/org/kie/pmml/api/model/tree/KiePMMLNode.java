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
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.kie.pmml.api.model.tree.predicates.KiePMMLPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLNode implements Serializable {

    private static final long serialVersionUID = 8447087369287427969L;
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLNode.class);


    private final String id;
    private String parentId;
    private String score;
    private String result;
    private KiePMMLPredicate kiePMMLPredicate;
    private List<KiePMMLNode> kiePMMLNodes;

    private KiePMMLNode(String id) {
        this.id = id;
    }

    /**
     * Builder to provide a defined <b>id</b>
     * @param  id
     * @return
     */
    public static Builder builder(String id) {
        return new Builder(id);
    }

    /**
     * Builder to auto-generate the <b>id</b>
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }


    public String getParentId() {
        return parentId;
    }

    public boolean evaluate(Map<String, Object> values) {
        result = null;
        logger.info(String.format("%s: evaluate %s", id, this.score));
        if (kiePMMLPredicate != null && kiePMMLPredicate.evaluate(values)) {
            logger.info(String.format("%s: matching predicate, evaluating... ", id));
            logger.info(String.format("%s: preliminary set %s", id, score));
            result = score;
            if (kiePMMLNodes != null) {
                for (KiePMMLNode kiePMMLNode : kiePMMLNodes) {
                    if (kiePMMLNode.evaluate(values)) {
                        logger.info(String.format("%s: matching node, update set %s", id, kiePMMLNode.result));
                        result = kiePMMLNode.result;
                        break;
                    }
                }
            }
            return true;
        }
        logger.info(String.format("%s: no matching predicate, set %s", id,  result));
        return false;
    }

    public String getId() {
        return id;
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

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (score != null ? !score.equals(that.score) : that.score != null) {
            return false;
        }
        if (kiePMMLPredicate != null ? !kiePMMLPredicate.equals(that.kiePMMLPredicate) : that.kiePMMLPredicate != null) {
            return false;
        }
        return kiePMMLNodes != null ? kiePMMLNodes.equals(that.kiePMMLNodes) : that.kiePMMLNodes == null;
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

    public static class Builder {

        private static final AtomicInteger counter = new AtomicInteger(1);
        private KiePMMLNode toBuild;

        private Builder(String id) {
            this.toBuild = new KiePMMLNode(id);
        }

        private Builder() {
            String id = "Node-" + counter.getAndAdd(1);
            this.toBuild = new KiePMMLNode(id);
        }

        public KiePMMLNode build() {
            return toBuild;
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
            kiePMMLNodes.forEach(node -> node.parentId = toBuild.id);
            toBuild.kiePMMLNodes = kiePMMLNodes;
            return this;
        }
    }
}
