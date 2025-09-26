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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class RuleListDeserializer extends JsonDeserializer<List<Rule>> {
    
    private int rowNumber = 1;
    
    @Override
    public List<Rule> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        JsonNode arrayNode = parser.getCodec().readTree(parser);
        List<Rule> rules = new ArrayList<>();
        
        if (arrayNode.isArray()) {
            for (JsonNode ruleNode : arrayNode) {
                Rule rule = deserializeRule(ruleNode);
                rules.add(rule);
            }
        }
        
        return rules;
    }
    
    private Rule deserializeRule(JsonNode node) {
        if (node.isArray()) {
            // This is an InlineRule (array format)
            List<Object> items = new ArrayList<>();
            for (JsonNode item : node) {
                if (item.isTextual()) {
                    items.add(item.textValue());
                } else if (item.isNumber()) {
                    items.add(new BigDecimal(item.asText()));
                } else if (item.isBoolean()) {
                    items.add(item.booleanValue());
                } else {
                    items.add(item.toString());
                }
            }
            return new InlineRule(rowNumber++, items);
        } else if (node.isObject()) {
            // This is a WhenThenRule (object format with when/then)
            if (node.has("when") && node.has("then")) {
                WhenThenRule rule = new WhenThenRule(rowNumber++);
                
                // Process "when" array
                JsonNode whenNode = node.get("when");
                List<Object> whenItems = new ArrayList<>();
                if (whenNode.isArray()) {
                    for (JsonNode item : whenNode) {
                        if (item.isTextual()) {
                            whenItems.add(item.textValue());
                        } else if (item.isNumber()) {
                            whenItems.add(new BigDecimal(item.asText()));
                        } else if (item.isBoolean()) {
                            whenItems.add(item.booleanValue());
                        } else {
                            whenItems.add(item.toString());
                        }
                    }
                }
                rule.setWhen(whenItems);
                
                // Process "then" value
                JsonNode thenNode = node.get("then");
                if (thenNode.isTextual()) {
                    rule.setThen(thenNode.textValue());
                } else if (thenNode.isNumber()) {
                    rule.setThen(new BigDecimal(thenNode.asText()));
                } else if (thenNode.isBoolean()) {
                    rule.setThen(thenNode.booleanValue());
                } else {
                    rule.setThen(thenNode.toString());
                }
                
                return rule;
            }
        }
        
        throw new IllegalArgumentException("Unknown rule format: " + node.toString());
    }
}