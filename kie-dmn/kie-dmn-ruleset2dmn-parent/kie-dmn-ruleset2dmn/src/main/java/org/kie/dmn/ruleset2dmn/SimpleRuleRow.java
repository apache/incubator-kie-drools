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
package org.kie.dmn.ruleset2dmn;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.CompoundPredicate.BooleanOperator;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.dmg.pmml.rule_set.SimpleRule;

public class SimpleRuleRow {
    final SimpleRule r;
    final Map<String, List<SimplePredicate>> map = new LinkedHashMap<>();

    public SimpleRuleRow(SimpleRule r) {
        this.r = r;
        Predicate rootPredicate = r.getPredicate();
        if (rootPredicate instanceof SimplePredicate) {
            SimplePredicate sp = (SimplePredicate) rootPredicate;
            map.computeIfAbsent(sp.getField(), k -> new ArrayList<SimplePredicate>()).add(sp);
        } else {
            if (!(rootPredicate instanceof CompoundPredicate)) {
                throw new UnsupportedOperationException("Was expecting a CompoundPredicate, found: "+rootPredicate.getClass());
            }
            CompoundPredicate cPredicate = (CompoundPredicate) rootPredicate;
            if (!(cPredicate.getBooleanOperator() == BooleanOperator.AND)){
                throw new UnsupportedOperationException("Only AND operator usage is supported in CompoundPredicate to convert to a Decision Table.");
            }
            for (Predicate c : cPredicate.getPredicates()) {
                SimplePredicate sp = (SimplePredicate) c;
                map.computeIfAbsent(sp.getField(), k -> new ArrayList<SimplePredicate>()).add(sp);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(r.getId()).append(" -> ").append(r.getScore()).append("\n");
        for (Entry<String, List<SimplePredicate>> kv : map.entrySet()) {
            sb.append(kv.getKey()).append(" ");
            for (SimplePredicate v : kv.getValue()) {
                sb.append(v.getOperator()).append(v.getValue()).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}