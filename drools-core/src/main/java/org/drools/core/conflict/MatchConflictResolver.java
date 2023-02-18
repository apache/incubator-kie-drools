/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.conflict;

import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.rule.consequence.ConflictResolver;
import org.kie.api.runtime.rule.Match;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class MatchConflictResolver implements ConflictResolver<Match>, Externalizable {

    private static final long                   serialVersionUID = 510l;
    public static final MatchConflictResolver INSTANCE         = new MatchConflictResolver();

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public static ConflictResolver getInstance() {
        return MatchConflictResolver.INSTANCE;
    }

    public final int compare(final Match existing,
                             final Match adding) {
        return doCompare( existing, adding );
    }

    public final static int doCompare(final Match existing,
                                      final Match adding) {
        if (existing == adding) {
            return 0;
        }

        final int s1 = existing.getSalience();
        final int s2 = adding.getSalience();

        if (s1 != s2) {
            return s1 > s2 ? 1 : -1;
        }

        return adding.getRule().getLoadOrder() - existing.getRule().getLoadOrder(); // lowest order goes first
    }
}
