/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.kie.services.impl.model;

import java.io.Serializable;

public class NodeDesc implements org.jbpm.services.api.model.NodeDesc, Serializable {

    private static final long serialVersionUID = -5724814793988493958L;

    private long id;
    private String uniqueId;
    private String name;
    private String nodeType;

    public NodeDesc() {
    }

    public NodeDesc(long id, String uniqueId, String name, String nodeType) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.name = name;
        this.nodeType = nodeType;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNodeType() {
        return nodeType;
    }

    @Override
    public String getUniqueId() {
        return uniqueId;
    }

    @Override
    public String toString() {
        return "NodeDesc{" +
                "id=" + id +
                ", uniqueId='" + uniqueId + '\'' +
                ", name='" + name + '\'' +
                ", nodeType='" + nodeType + '\'' +
                '}';
    }
}
