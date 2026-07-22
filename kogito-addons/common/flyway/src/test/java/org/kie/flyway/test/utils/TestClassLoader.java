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

package org.kie.flyway.test.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.kie.flyway.initializer.impl.DefaultKieModuleFlywayConfigLoader;

public class TestClassLoader extends ClassLoader {
    private final List<URL> modules = new ArrayList<>();

    public TestClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void addKieFlywayModule(String resourceUrl) {
        this.modules.add(getResource(resourceUrl));
    }

    @Override
    public Stream<URL> resources(String name) {
        if (!modules.isEmpty() && DefaultKieModuleFlywayConfigLoader.KIE_FLYWAY_DESCRIPTOR_FILE_LOCATION.equals(name)) {
            return modules.stream();
        }
        return super.resources(name);
    }
}
