/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.replay.debug;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * An immutable snapshot of the working memory state at a given point in the execution log.
 * Tracks which facts are currently present (by identity) and which rules have fired so far.
 */
public class StateSnapshot {

    private final int position;
    private final Map<String, String> activeFacts;
    private final List<String> rulesFiredSoFar;
    private final int totalEventsProcessed;

    public StateSnapshot(int position, Map<String, String> activeFacts,
                         List<String> rulesFiredSoFar, int totalEventsProcessed) {
        this.position = position;
        this.activeFacts = Map.copyOf(activeFacts);
        this.rulesFiredSoFar = List.copyOf(rulesFiredSoFar);
        this.totalEventsProcessed = totalEventsProcessed;
    }
    public int getPosition() {
        return position;
    }
    /**
     * @return map of fact identity -&gt; fact description for all facts currently in working memory
     */
    public Map<String, String> getActiveFacts() {
        return activeFacts;
    }
    public Set<String> getActiveFactIdentities() {
        return activeFacts.keySet();
    }
    public List<String> getRulesFiredSoFar() {
        return rulesFiredSoFar;
    }
    public int getTotalEventsProcessed() {
        return totalEventsProcessed;
    }
    @Override
    public String toString() {
        return String.format("StateSnapshot{position=%d, activeFacts=%d, rulesFired=%d}",
                position, activeFacts.size(), rulesFiredSoFar.size());
    }
}