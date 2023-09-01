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
package org.drools.impact.analysis.graph;

import org.drools.impact.analysis.model.Rule;

public class AnalyzedRule {

    private final Rule rule;
    private ReactivityType reactivityType;

    public AnalyzedRule( Rule rule, boolean positive ) {
        this(rule, ReactivityType.decode(positive) );
    }

    public AnalyzedRule( Rule rule, ReactivityType reactivityType ) {
        this.rule = rule;
        this.reactivityType = reactivityType;
    }

    public Rule getRule() {
        return rule;
    }

    public ReactivityType getReactivityType() {
        return reactivityType;
    }

    public void combineReactivityType( ReactivityType reactivityType ) {
        this.reactivityType = this.reactivityType.combine( reactivityType );
    }
}
