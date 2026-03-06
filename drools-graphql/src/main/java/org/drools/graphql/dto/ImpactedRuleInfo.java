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

import org.eclipse.microprofile.graphql.Description;

@Description("A rule identified as impacted by or impacting a target rule, with reactivity type")
public class ImpactedRuleInfo {

    private String packageName;
    private String ruleName;
    private String reactivityType;

    public ImpactedRuleInfo() {
    }

    public ImpactedRuleInfo(String packageName, String ruleName, String reactivityType) {
        this.packageName = packageName;
        this.ruleName = ruleName;
        this.reactivityType = reactivityType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    @Description("POSITIVE, NEGATIVE, or UNKNOWN")
    public String getReactivityType() {
        return reactivityType;
    }

    public void setReactivityType(String reactivityType) {
        this.reactivityType = reactivityType;
    }
}
