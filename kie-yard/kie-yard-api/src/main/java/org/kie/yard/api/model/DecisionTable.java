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

import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;
import org.kie.j2cl.tools.json.mapper.annotation.JSONMapper;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YAMLMapper;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlTypeDeserializer;
import org.kie.j2cl.tools.yaml.mapper.api.annotation.YamlTypeSerializer;

@YAMLMapper
@JSONMapper
public class DecisionTable implements DecisionLogic {

    private List<String> inputs;
    private String hitPolicy = "ANY";
    @Deprecated
    private List<String> outputComponents;
    @YamlTypeSerializer(Rule_YamlSerializerImpl.class)
    @YamlTypeDeserializer(Rule_YamlDeserializerImpl.class)
    @JsonbTypeSerializer(RuleJSONDefSerializer.class)
    @JsonbTypeDeserializer(RuleJSONDefSerializer.class)
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
