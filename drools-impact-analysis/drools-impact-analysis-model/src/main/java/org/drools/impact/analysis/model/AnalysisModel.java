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
package org.drools.impact.analysis.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class AnalysisModel {

    private final List<Package> packages = new ArrayList<>();

    public List<Package> getPackages() {
        return packages;
    }

    public void addPackage(Package pkg) {
        packages.add(pkg);
    }

    @Override
    public String toString() {
        return "AnalysisModel{" +
                "packages=" + packages.stream().map( Object::toString ).collect( joining("\n", ",\n", "") ) +
                '}';
    }
}
