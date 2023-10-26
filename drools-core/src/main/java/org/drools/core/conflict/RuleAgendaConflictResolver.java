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
package org.drools.core.conflict;

import java.io.Serializable;

import org.drools.base.rule.consequence.ConflictResolver;
import org.drools.core.phreak.RuleAgendaItem;
import org.kie.api.definition.rule.Rule;

public class RuleAgendaConflictResolver implements ConflictResolver<RuleAgendaItem>, Serializable {

    public static final RuleAgendaConflictResolver INSTANCE = new RuleAgendaConflictResolver();

    public final int compare(RuleAgendaItem existing, RuleAgendaItem adding) {
        return doCompare( existing, adding );
    }

    public final static int doCompare(final RuleAgendaItem existing, final RuleAgendaItem adding) {
        if (existing == adding) {
            return 0;
        }

        final int s1 = existing.getSalience();
        final int s2 = adding.getSalience();

        if (s1 != s2) {
            return s1 > s2 ? 1 : -1;
        }
        Rule r1 = existing.getRule();
        Rule r2 = adding.getRule();

        if (r1.getLoadOrder() == r2.getLoadOrder()) {
            return adding.getTerminalNode().getId() - existing.getTerminalNode().getId();
        }

        return r2.getLoadOrder() - r1.getLoadOrder(); // lowest order goes first
    }
}
