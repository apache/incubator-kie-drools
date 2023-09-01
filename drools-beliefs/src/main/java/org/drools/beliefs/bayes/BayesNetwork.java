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
package org.drools.beliefs.bayes;

import org.drools.beliefs.graph.impl.GraphImpl;
import org.drools.beliefs.graph.impl.ListGraphStore;

public class BayesNetwork extends GraphImpl<BayesVariable> {

    private String name;
    private String packageName;

    public BayesNetwork(String name) {
        super(new ListGraphStore<>());
        this.name = name;
    }

    public BayesNetwork(String name, String packageName) {
        super(new ListGraphStore<>());
        this.name = name;
        this.packageName = packageName;
    }

    public BayesNetwork() {
        super(new ListGraphStore<>());
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }
}
