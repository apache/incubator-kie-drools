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
package org.kie.yard.api.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
@JsonPropertyOrder({"inputs", "hitPolicy", "outputComponents", "rules"})
public class DecisionTable implements DecisionLogic {

    @JsonProperty("inputs")
    private List<String> inputs;
    
    @JsonProperty("hitPolicy")
    private String hitPolicy = "ANY";
    
    @Deprecated
    @JsonProperty("outputComponents")
    private List<String> outputComponents;
    
    @JsonProperty("rules")
    @JsonDeserialize(using = RuleListDeserializer.class)
    private List<Rule> rules;

    public void setInputs(List<String> inputs) {
        this.inputs = inputs;
    }

    public void setOutputComponents(List<String> outputComponents) {
        this.outputComponents = outputComponents;
    }

    public List<String> getInputs() {
        return inputs;
    }

    @Deprecated
    public List<String> getOutputComponents() {
        return outputComponents;
    }

    public String getHitPolicy() {
        return hitPolicy;
    }

    public void setHitPolicy(String hitPolicy) {
        this.hitPolicy = hitPolicy;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}
