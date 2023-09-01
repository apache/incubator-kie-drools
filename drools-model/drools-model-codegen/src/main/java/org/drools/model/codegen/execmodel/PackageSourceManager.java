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
package org.drools.model.codegen.execmodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A delegate/container of package sources,
 * used in {@link ModelBuilderImpl}
 *
 * @param <T> the type of the Package Source object
 */
public class PackageSourceManager<T> {

    private final Map<String, T> packageSources = new HashMap<>();

    public void put(String name, T generated) {
        packageSources.put( name, generated );
    }

    public Collection<T> getPackageSources() {
        return packageSources.values();
    }

    public T getPackageSource(String packageName) {
        return packageSources.get(packageName);
    }

    public Collection<T> values() {
        return packageSources.values();
    }

    public T get(String packageName) {
        return packageSources.get(packageName);
    }
}
