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
package org.drools.swagger.model;

import java.util.List;
import java.util.Map;

public class ExecutionRequest {

    private List<FactInput> facts;
    private Map<String, Object> globals;
    private String agendaFilter;
    private Integer maxRules;

    public ExecutionRequest() {
    }

    public List<FactInput> getFacts() {
        return facts;
    }

    public void setFacts(List<FactInput> facts) {
        this.facts = facts;
    }

    public Map<String, Object> getGlobals() {
        return globals;
    }

    public void setGlobals(Map<String, Object> globals) {
        this.globals = globals;
    }

    public String getAgendaFilter() {
        return agendaFilter;
    }

    public void setAgendaFilter(String agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public Integer getMaxRules() {
        return maxRules;
    }

    public void setMaxRules(Integer maxRules) {
        this.maxRules = maxRules;
    }

    public static class FactInput {
        private String type;
        private Map<String, Object> data;

        public FactInput() {
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Map<String, Object> getData() {
            return data;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }
    }
}
