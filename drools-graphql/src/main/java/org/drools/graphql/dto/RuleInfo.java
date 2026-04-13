/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.graphql.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.microprofile.graphql.Description;
import org.kie.api.definition.rule.Rule;

@Description("A rule definition within a Drools knowledge base")
public class RuleInfo {

    private String name;
    private String packageName;
    private int loadOrder;
    private List<MetaEntry> metadata;

    public RuleInfo() {
    }

    public RuleInfo(String name, String packageName, int loadOrder, List<MetaEntry> metadata) {
        this.name = name;
        this.packageName = packageName;
        this.loadOrder = loadOrder;
        this.metadata = metadata;
    }

    public static RuleInfo from(Rule rule) {
        List<MetaEntry> meta = new ArrayList<>();
        Map<String, Object> metaData = rule.getMetaData();
        if (metaData != null) {
            meta = metaData.entrySet().stream()
                    .map(e -> new MetaEntry(e.getKey(), String.valueOf(e.getValue())))
                    .collect(Collectors.toList());
        }
        return new RuleInfo(rule.getName(), rule.getPackageName(), rule.getLoadOrder(), meta);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getLoadOrder() {
        return loadOrder;
    }

    public void setLoadOrder(int loadOrder) {
        this.loadOrder = loadOrder;
    }

    public List<MetaEntry> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<MetaEntry> metadata) {
        this.metadata = metadata;
    }
}
