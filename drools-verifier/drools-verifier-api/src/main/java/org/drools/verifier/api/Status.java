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
package org.drools.verifier.api;

public class Status {

    private String webWorkerUUID;

    private int startCheckIndex;
    private int endCheckIndex;
    private int totalCheckCount;

    public Status() {
    }

    public Status(final String webWorkerUUID,
                  final int startCheckIndex,
                  final int endCheckIndex,
                  final int totalCheckCount) {
        this.webWorkerUUID = webWorkerUUID;
        this.startCheckIndex = startCheckIndex;
        this.endCheckIndex = endCheckIndex;
        this.totalCheckCount = totalCheckCount;
    }

    public void setWebWorkerUUID(final String webWorkerUUID) {
        this.webWorkerUUID = webWorkerUUID;
    }

    public void setStartCheckIndex(final int startCheckIndex) {
        this.startCheckIndex = startCheckIndex;
    }

    public void setEndCheckIndex(final int endCheckIndex) {
        this.endCheckIndex = endCheckIndex;
    }

    public void setTotalCheckCount(final int totalCheckCount) {
        this.totalCheckCount = totalCheckCount;
    }

    public String getWebWorkerUUID() {
        return webWorkerUUID;
    }

    public int getStart() {
        return startCheckIndex;
    }

    public int getEnd() {
        return endCheckIndex;
    }

    public int getTotalCheckCount() {
        return totalCheckCount;
    }
}
