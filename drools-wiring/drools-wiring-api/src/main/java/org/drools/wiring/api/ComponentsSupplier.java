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
package org.drools.wiring.api;

import java.io.IOException;
import java.util.Map;

import org.drools.wiring.api.classloader.ProjectClassLoader;
import org.drools.wiring.api.util.ByteArrayClassLoader;
import org.kie.api.internal.utils.KieService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.KnowledgeBuilder;

public interface ComponentsSupplier extends KieService {
    ProjectClassLoader createProjectClassLoader(ClassLoader parent, ResourceProvider resourceProvider );

    ByteArrayClassLoader createByteArrayClassLoader(ClassLoader parent );

    default ClassLoader createPackageClassLoader(Map<String, byte[]> store, ClassLoader rootClassLoader) {
        return rootClassLoader;
    }

    Object createConsequenceExceptionHandler(String className, ClassLoader classLoader);

    default void addPackageFromXSD(KnowledgeBuilder kBuilder, Resource resource, ResourceConfiguration configuration) throws IOException { }
}
