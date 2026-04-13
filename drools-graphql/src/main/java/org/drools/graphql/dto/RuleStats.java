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
package org.drools.graphql.dto;

import org.eclipse.microprofile.graphql.Description;

@Description("Execution statistics for a single rule")
public class RuleStats {

    private String ruleName;
    private long matchesFired;
    private long matchesCreated;
    private long matchesCancelled;
    private long firingTimeMs;
    private double averageFiringTimeMs;

    public RuleStats() {
    }

    public RuleStats(String ruleName, long matchesFired, long matchesCreated,
                     long matchesCancelled, long firingTimeMs) {
        this.ruleName = ruleName;
        this.matchesFired = matchesFired;
        this.matchesCreated = matchesCreated;
        this.matchesCancelled = matchesCancelled;
        this.firingTimeMs = firingTimeMs;
        this.averageFiringTimeMs = matchesFired > 0 ? (double) firingTimeMs / matchesFired : 0.0;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public long getMatchesFired() {
        return matchesFired;
    }

    public void setMatchesFired(long matchesFired) {
        this.matchesFired = matchesFired;
    }

    public long getMatchesCreated() {
        return matchesCreated;
    }

    public void setMatchesCreated(long matchesCreated) {
        this.matchesCreated = matchesCreated;
    }

    public long getMatchesCancelled() {
        return matchesCancelled;
    }

    public void setMatchesCancelled(long matchesCancelled) {
        this.matchesCancelled = matchesCancelled;
    }

    public long getFiringTimeMs() {
        return firingTimeMs;
    }

    public void setFiringTimeMs(long firingTimeMs) {
        this.firingTimeMs = firingTimeMs;
    }

    public double getAverageFiringTimeMs() {
        return averageFiringTimeMs;
    }

    public void setAverageFiringTimeMs(double averageFiringTimeMs) {
        this.averageFiringTimeMs = averageFiringTimeMs;
    }
}
