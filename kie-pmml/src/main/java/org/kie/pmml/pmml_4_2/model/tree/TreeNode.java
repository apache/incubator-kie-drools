/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.pmml_4_2.model.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * declare @{ pmmlPackageName }.TreeNode
@propertyReactive
    context     : String            @key
    id          : String            @key
    parent      : String
    children    : java.util.List
    recordCount : int
    counts      : java.util.Map
    confidence  : java.util.Map
    defaultChld : String
end
 */
@SuppressWarnings("rawtypes")
public class TreeNode {
    private String context;
    private String id;
    private String parent;
    private List children;
    private int recordCount;
    private Map counts;
    private Map confidence;
    private String defaultChld;


    public TreeNode() {
    	// Necessary for JAXB
    }

    public TreeNode(String correlationId, String context, String id, String parent, List children, int recordCount,
            Map counts, Map confidence, String defaultChld) {
        this.context = context;
        this.id = id;
        this.parent = parent;
        this.children = children;
        this.recordCount = recordCount;
        this.counts = counts;
        this.confidence = confidence;
        this.defaultChld = defaultChld;
    }
    public String getContext() {
        return context;
    }
    public void setContext(String context) {
        this.context = context;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getParent() {
        return parent;
    }
    public void setParent(String parent) {
        this.parent = parent;
    }
    public List getChildren() {
        return children;
    }
    public void setChildren(List children) {
        this.children = children;
    }
    public int getRecordCount() {
        return recordCount;
    }
    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }
    public Map getCounts() {
        return counts;
    }
    public void setCounts(Map counts) {
        this.counts = counts;
    }
    public Map getConfidence() {
        return confidence;
    }
    public void setConfidence(Map confidence) {
        this.confidence = confidence;
    }
    public String getDefaultChld() {
        return defaultChld;
    }
    public void setDefaultChld(String defaultChld) {
        this.defaultChld = defaultChld;
    }
    public ScoreDistributionInfo getWeightedConfidenceWinner(List<TreeNode> childNodes) {
        ScoreDistributions distributedScores = new ScoreDistributions(childNodes);
        return distributedScores.getWeightedDistribution();
    }

    @SuppressWarnings("unchecked")
    public static class ScoreDistributions {
        private Map<String, Map<String,ScoreDistributionInfo>> distributions = new HashMap<>();
        private Map<String, Double> recordsPerNode;
        private Set<String> keyValues;
        private Set<String> nodeIds;
        private Double totalRecords;

        private ScoreDistributions() {}

        private void initKeyValues(List<TreeNode> nodes) {
            keyValues = new HashSet<>();
            nodes.forEach(n -> {
                keyValues.addAll(n.confidence.keySet());
                keyValues.addAll(n.counts.keySet());
            });
        }

        private void initFromNodes(List<TreeNode> nodes) {
            recordsPerNode = new HashMap<>();
            nodeIds = new HashSet<>();
            totalRecords = 0.0;
            nodes.forEach(n -> {
                double cnt = 0.0;
                for (Double val: (Collection<Double>)n.counts.values()) {
                    cnt += val;
                }
                totalRecords += cnt;
                recordsPerNode.put(n.id, cnt);
                nodeIds.add(n.id);
            });
        }

        public ScoreDistributions(List<TreeNode> nodes) {
            initKeyValues(nodes);
            initFromNodes(nodes);
            for (TreeNode node: nodes) {
                String nodeKey = node.id;
                Map<String,ScoreDistributionInfo> scoresMap = new HashMap<>();
                for (String key: keyValues) {
                    Double conf = (Double)node.confidence.get(key);
                    Double cnt = (Double)node.counts.get(key);
                    ScoreDistributionInfo info = new ScoreDistributionInfo(key,cnt,conf);
                    scoresMap.put(key, info);
                }
                distributions.put(nodeKey, scoresMap);
            }
        }

        public ScoreDistributionInfo getWeightedDistribution() {
            Double maxConf = 0.0;
            ScoreDistributionInfo winner = new ScoreDistributionInfo();
            Map<String,Double> weightedConfidences = new HashMap<>();
            Map<String,Double> combinedCounts = new HashMap<>();
            for (String key: keyValues) {
                weightedConfidences.put(key, 0.0);
            }
            for (String nodeKey: nodeIds) {
                Double recordCount = recordsPerNode.get(nodeKey);
                if (recordCount != null && recordCount > 0.0) {
                    Double weightFactor = recordCount/totalRecords;
                    Map<String, ScoreDistributionInfo> distMap = distributions.get(nodeKey);
                    for (String key: keyValues) {
                        if (combinedCounts.containsKey(key)) {
                            combinedCounts.put(key, combinedCounts.get(key) + recordCount);
                        } else {
                            combinedCounts.put(key, recordCount);
                        }
                        ScoreDistributionInfo sdi = distMap.get(key);
                        Double nodeConf = sdi.getConfidence();
                        Double weightedNodeConf = ((nodeConf != null) ? nodeConf : 0.0) * weightFactor;
                        Double weightedTotalConf = weightedConfidences.get(key) + weightedNodeConf;
                        weightedConfidences.put(key, weightedTotalConf);
                        if (weightedTotalConf > maxConf) {
                            maxConf = weightedTotalConf;
                            winner.setConfidence(weightedTotalConf);
                            winner.setValue(key);
                        }
                    }
                }
            }
            winner.setCount(combinedCounts.get(winner.getValue()));
            return winner;
        }

        @Override
        public String toString() {
            return "ScoreDistributions [distributions=" + distributions + ", recordsPerNode=" + recordsPerNode
                    + ", totalRecords=" + totalRecords + "]";
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((context == null) ? 0 : context.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TreeNode other = (TreeNode) obj;
        if (context == null) {
            if (other.context != null) {
                return false;
            }
        } else if (!context.equals(other.context)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TreeNode( ");
        builder.append("context=").append(this.context).append(", ");
        builder.append("id=").append(this.id).append(", ");
        builder.append("parent=").append(this.parent).append(", ");
        builder.append("children=[");
        Iterator lstIter = this.children.iterator();
        while (lstIter.hasNext()) {
            Object object = lstIter.next();
            builder.append(object.toString());
            if (lstIter.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append("], ");
        builder.append("recordCount=").append(this.recordCount).append(", ");
        builder.append("counts=[");
        Iterator keyIter = this.counts.keySet().iterator();
        while (keyIter.hasNext()) {
            Object key = keyIter.next();
            Object value = this.counts.get(key);
            builder.append("(").append(key.toString()).append("->").append(value != null ? value.toString() : "null").append(")");
            if (keyIter.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append("], ");
        builder.append("confidence=[");
        keyIter = this.confidence.keySet().iterator();
        while (keyIter.hasNext()) {
            Object key = keyIter.next();
            Object value = this.confidence.get(key);
            builder.append("(").append(key.toString()).append("->").append(value != null ? value.toString() : "null").append(")");
            if (keyIter.hasNext()) {
                builder.append(", ");
            }
        }
        builder.append("], ");
        builder.append("defaultChild=").append(this.defaultChld).append(" )");
        return builder.toString();
    }

}
