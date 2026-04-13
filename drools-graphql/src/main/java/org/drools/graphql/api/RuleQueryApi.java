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
package org.drools.graphql.api;

import java.util.List;

import jakarta.inject.Inject;

import org.drools.graphql.dto.PackageInfo;
import org.drools.graphql.dto.RuleInfo;
import org.drools.graphql.service.RuleMetadataService;
import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

/**
 * GraphQL API for querying rule definitions and package metadata.
 *
 * <p>Example queries:
 * <pre>
 * {
 *   packages { name rules { name packageName metadata { key value } } }
 *   rule(packageName: "com.example", ruleName: "Validate Order") { name loadOrder metadata { key value } }
 *   searchRules(namePattern: "fraud") { name packageName }
 *   totalRuleCount
 * }
 * </pre>
 */
@GraphQLApi
public class RuleQueryApi {

    @Inject
    RuleMetadataService metadataService;

    public RuleQueryApi() {
    }

    public RuleQueryApi(RuleMetadataService metadataService) {
        this.metadataService = metadataService;
    }

    @Query("packages")
    @Description("List all packages in the knowledge base with their rules, queries, and fact types")
    public List<PackageInfo> getPackages() {
        return metadataService.getAllPackages();
    }

    @Query("packageByName")
    @Description("Get a specific package by name")
    public PackageInfo getPackage(@Name("packageName") @NonNull String packageName) {
        return metadataService.getPackage(packageName);
    }

    @Query("rule")
    @Description("Get a specific rule by package name and rule name")
    public RuleInfo getRule(@Name("packageName") @NonNull String packageName,
                           @Name("ruleName") @NonNull String ruleName) {
        return metadataService.getRule(packageName, ruleName);
    }

    @Query("allRules")
    @Description("List all rules across all packages")
    public List<RuleInfo> getAllRules() {
        return metadataService.getAllRules();
    }

    @Query("rulesByPackage")
    @Description("List all rules in a specific package")
    public List<RuleInfo> getRulesByPackage(@Name("packageName") @NonNull String packageName) {
        return metadataService.getRulesByPackage(packageName);
    }

    @Query("searchRules")
    @Description("Search rules by name pattern (case-insensitive contains match)")
    public List<RuleInfo> searchRules(@Name("namePattern") @NonNull String namePattern) {
        return metadataService.searchRules(namePattern);
    }

    @Query("totalRuleCount")
    @Description("Total number of rules across all packages")
    public long getTotalRuleCount() {
        return metadataService.getTotalRuleCount();
    }
}
