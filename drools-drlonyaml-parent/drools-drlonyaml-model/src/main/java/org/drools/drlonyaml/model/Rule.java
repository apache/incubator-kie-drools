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
package org.drools.drlonyaml.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.RuleDescr;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"name", "when", "then"})
public class Rule {

    @JsonProperty(required = true)
    private String name;

    @JsonProperty(required = true)
    private List<Base> when = new ArrayList<>();

    @JsonProperty(required = true)
    private AbstractThen then;

    public static Rule from(RuleDescr r) {
        Objects.requireNonNull(r);
        Rule result = new Rule();
        result.name = r.getName();
        for (BaseDescr dd: r.getLhs().getDescrs()) {
            result.when.add(Utils.from(dd));
        }
        result.then = StringThen.from(r.getConsequence().toString());
        return result;
    }

    public String getName() {
        return name;
    }

    public List<Base> getWhen() {
        return when;
    }

    public Object getThen() {
        return then;
    }
}
