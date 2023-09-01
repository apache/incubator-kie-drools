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

import java.util.StringTokenizer;

import org.kie.efesto.common.api.identifiers.LocalId;

public class RuleUnitIdParser {
    public static LocalId parse(String id) {
        return parse(id, LocalId.class);
    }

    public static <T extends LocalId> T parse(String id, Class<T> expected) {
        StringTokenizer tok = new StringTokenizer(id, "/");
        String prefix = tok.nextToken();

        if (!prefix.equals(RuleUnitId.PREFIX))
            throw newError(id, RuleUnitId.PREFIX);
        String ruleUnit = tok.nextToken();
        RuleUnitId ruleUnitId = new RuleUnitId(ruleUnit);
        if (expected == RuleUnitId.class)
            return (T) ruleUnitId;
        if (!tok.hasMoreTokens() && expected == LocalId.class)
            return (T) ruleUnitId;

        String instanceOrQuery = tok.nextToken();
        if (instanceOrQuery.equals(QueryId.PREFIX)) {
            String query = tok.nextToken();
            QueryId queryId = ruleUnitId.queries().get(query);
            if (expected == QueryId.class)
                return (T) queryId;
            if (!tok.hasMoreTokens() && expected == LocalId.class)
                return (T) queryId;
        }
        if (instanceOrQuery.equals(RuleUnitInstanceId.PREFIX)) {
            String instanceId = tok.nextToken();
            RuleUnitInstanceId ruleUnitInstanceId = ruleUnitId.instances().get(instanceId);
            if (expected == RuleUnitInstanceId.class)
                return (T) ruleUnitInstanceId;
            if (!tok.hasMoreTokens() && expected == LocalId.class)
                return (T) ruleUnitInstanceId;
            if (!tok.nextToken().equals(QueryId.PREFIX))
                throw newError(id, QueryId.PREFIX);
            String query = tok.nextToken();
            InstanceQueryId queryId = ruleUnitInstanceId.queries().get(query);
            if (expected == InstanceQueryId.class)
                return (T) queryId;
            if (!tok.hasMoreTokens() && expected == LocalId.class)
                return (T) queryId;
        }
        throw newError(id, "instances or queries");
    }

    public static <T extends LocalId> T select(LocalId id, Class<T> expected) {
        return parse(id.asLocalUri().path(), expected);
    }

    private static IllegalArgumentException newError(String id, String expected) {
        return new IllegalArgumentException("Invalid id " + id + "; expected " + expected);
    }
}
