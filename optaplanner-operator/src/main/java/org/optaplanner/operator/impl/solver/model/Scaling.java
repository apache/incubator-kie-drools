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

package org.optaplanner.operator.impl.solver.model;

public final class Scaling {

    private boolean dynamic = false;

    private int replicas = 1;

    public Scaling() {
        // Required by Jackson
    }

    public Scaling(boolean dynamic, int replicas) {
        this.dynamic = dynamic;
        this.replicas = replicas;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public int getReplicas() {
        return replicas;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public void setReplicas(int replicas) {
        this.replicas = replicas;
    }
}
