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
package org.drools.audit.event;

public enum AuditEventType {

    RULE_FIRED("RULE_FIRED", "rule"),
    RULE_MATCH_CREATED("RULE_MATCH_CREATED", "rule"),
    RULE_MATCH_CANCELLED("RULE_MATCH_CANCELLED", "rule"),

    FACT_INSERTED("FACT_INSERTED", "fact"),
    FACT_UPDATED("FACT_UPDATED", "fact"),
    FACT_DELETED("FACT_DELETED", "fact"),

    AGENDA_GROUP_PUSHED("AGENDA_GROUP_PUSHED", "agenda"),
    AGENDA_GROUP_POPPED("AGENDA_GROUP_POPPED", "agenda"),
    RULEFLOW_GROUP_ACTIVATED("RULEFLOW_GROUP_ACTIVATED", "agenda"),
    RULEFLOW_GROUP_DEACTIVATED("RULEFLOW_GROUP_DEACTIVATED", "agenda"),

    SESSION_CREATED("SESSION_CREATED", "session"),
    SESSION_DISPOSED("SESSION_DISPOSED", "session"),
    SESSION_FIRE_ALL_RULES("SESSION_FIRE_ALL_RULES", "session");

    private final String code;
    private final String category;

    AuditEventType(String code, String category) {
        this.code = code;
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public String getCategory() {
        return category;
    }
}
