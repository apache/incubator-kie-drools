/**
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
package org.kie.drl.api.identifiers;

import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class RuleUnitInstanceId extends LocalUriId implements LocalId {

    public static final String PREFIX = "instances";

    private final RuleUnitId ruleUnitId;
    private final String ruleUnitInstanceId;

    public RuleUnitInstanceId(RuleUnitId processId, String ruleUnitInstanceId) {
        super(processId.asLocalUri().append(PREFIX).append(ruleUnitInstanceId));
        LocalId localDecisionId = processId.toLocalId();
        if (!localDecisionId.asLocalUri().startsWith(RuleUnitId.PREFIX)) {
            throw new IllegalArgumentException("Not a valid process path"); // fixme use typed exception
        }

        this.ruleUnitId = processId;
        this.ruleUnitInstanceId = ruleUnitInstanceId;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    public RuleUnitId ruleUnitId() {
        return ruleUnitId;
    }

    public InstanceQueryIds queries() {
        return new InstanceQueryIds(this);
    }

    public DataSourceIds dataSources() {
        return new DataSourceIds(this);
    }

    public String ruleUnitInstanceId() {
        return ruleUnitInstanceId;
    }

}