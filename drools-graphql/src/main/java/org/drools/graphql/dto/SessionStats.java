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

@Description("Aggregate execution statistics for a KIE session")
public class SessionStats {

    private String sessionName;
    private String kieBaseId;
    private long totalMatchesFired;
    private long totalMatchesCancelled;
    private long totalMatchesCreated;
    private long totalFiringTimeMs;
    private double averageFiringTimeMs;
    private long totalSessions;

    public SessionStats() {
    }

    public SessionStats(String sessionName, String kieBaseId,
                        long totalMatchesFired, long totalMatchesCancelled,
                        long totalMatchesCreated, long totalFiringTimeMs,
                        double averageFiringTimeMs, long totalSessions) {
        this.sessionName = sessionName;
        this.kieBaseId = kieBaseId;
        this.totalMatchesFired = totalMatchesFired;
        this.totalMatchesCancelled = totalMatchesCancelled;
        this.totalMatchesCreated = totalMatchesCreated;
        this.totalFiringTimeMs = totalFiringTimeMs;
        this.averageFiringTimeMs = averageFiringTimeMs;
        this.totalSessions = totalSessions;
    }

    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }

    public String getKieBaseId() { return kieBaseId; }
    public void setKieBaseId(String kieBaseId) { this.kieBaseId = kieBaseId; }

    public long getTotalMatchesFired() { return totalMatchesFired; }
    public void setTotalMatchesFired(long v) { this.totalMatchesFired = v; }

    public long getTotalMatchesCancelled() { return totalMatchesCancelled; }
    public void setTotalMatchesCancelled(long v) { this.totalMatchesCancelled = v; }

    public long getTotalMatchesCreated() { return totalMatchesCreated; }
    public void setTotalMatchesCreated(long v) { this.totalMatchesCreated = v; }

    public long getTotalFiringTimeMs() { return totalFiringTimeMs; }
    public void setTotalFiringTimeMs(long v) { this.totalFiringTimeMs = v; }

    public double getAverageFiringTimeMs() { return averageFiringTimeMs; }
    public void setAverageFiringTimeMs(double v) { this.averageFiringTimeMs = v; }

    public long getTotalSessions() { return totalSessions; }
    public void setTotalSessions(long v) { this.totalSessions = v; }
}
