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
import java.util.stream.Collectors;

import org.eclipse.microprofile.graphql.Description;
import org.kie.api.definition.KiePackage;

@Description("A package (namespace) of rules, queries, and fact types in a Drools knowledge base")
public class PackageInfo {

    private String name;
    private List<RuleInfo> rules;
    private List<String> queryNames;
    private List<String> functionNames;
    private List<String> factTypeNames;
    private List<String> globalNames;

    public PackageInfo() {
    }

    public static PackageInfo from(KiePackage pkg) {
        PackageInfo info = new PackageInfo();
        info.name = pkg.getName();
        info.rules = pkg.getRules().stream()
                .map(RuleInfo::from)
                .collect(Collectors.toList());
        info.queryNames = pkg.getQueries().stream()
                .map(q -> q.getName())
                .collect(Collectors.toList());
        info.functionNames = new ArrayList<>(pkg.getFunctionNames());
        info.factTypeNames = pkg.getFactTypes().stream()
                .map(ft -> ft.getName())
                .collect(Collectors.toList());
        info.globalNames = pkg.getGlobalVariables().stream()
                .map(g -> g.getName())
                .collect(Collectors.toList());
        return info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RuleInfo> getRules() {
        return rules;
    }

    public void setRules(List<RuleInfo> rules) {
        this.rules = rules;
    }

    public List<String> getQueryNames() {
        return queryNames;
    }

    public void setQueryNames(List<String> queryNames) {
        this.queryNames = queryNames;
    }

    public List<String> getFunctionNames() {
        return functionNames;
    }

    public void setFunctionNames(List<String> functionNames) {
        this.functionNames = functionNames;
    }

    public List<String> getFactTypeNames() {
        return factTypeNames;
    }

    public void setFactTypeNames(List<String> factTypeNames) {
        this.factTypeNames = factTypeNames;
    }

    public List<String> getGlobalNames() {
        return globalNames;
    }

    public void setGlobalNames(List<String> globalNames) {
        this.globalNames = globalNames;
    }
}
