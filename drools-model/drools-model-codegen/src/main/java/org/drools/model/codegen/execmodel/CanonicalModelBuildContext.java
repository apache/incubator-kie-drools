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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.kie.builder.impl.ResultsImpl;

public class CanonicalModelBuildContext extends BuildContext {

    private final Map<String, String> resourceOwners = new HashMap<>();
    private final Map<String, Set<String>> notOwnedResources = new HashMap<>();

    private final Collection<GeneratedClassWithPackage> allGeneratedPojos = new HashSet<>();
    private final Map<String, Class<?>> allCompiledClasses = new HashMap<>();

    public CanonicalModelBuildContext() { }

    public CanonicalModelBuildContext(ResultsImpl messages) {
        super(messages);
    }

    @Override
    public boolean registerResourceToBuild(String kBaseName, String resource) {
        boolean newResource = resourceOwners.putIfAbsent(resource, kBaseName) == null;
        if (!newResource) {
            notOwnedResources.computeIfAbsent(kBaseName, n -> new HashSet<>()).add(resource);
        }
        return newResource;
    }

    public void registerGeneratedPojos(Collection<GeneratedClassWithPackage> generatedPojos, Map<String, Class<?>> compiledClasses) {
        allGeneratedPojos.addAll(generatedPojos);
        allCompiledClasses.putAll(compiledClasses);
    }

    public Collection<GeneratedClassWithPackage> getAllGeneratedPojos() {
        return allGeneratedPojos;
    }

    public Map<String, Class<?>> getAllCompiledClasses() {
        return allCompiledClasses;
    }

    public Collection<String> getNotOwnedModelFiles(Map<String, ModelBuilderImpl> modelBuilders, String kBaseName) {
        Collection<String> notOwned = notOwnedResources.get(kBaseName);
        if (notOwned == null) {
            return Collections.emptyList();
        }

        Collection<String> notOwnedModelFiles = new HashSet<>();
        for (String resource : notOwned) {
            PackageSources packageSources = modelBuilders.get(resourceOwners.get(resource)).getPackageSource(resource2Package(resource));
            if (packageSources != null) {
                notOwnedModelFiles.addAll(packageSources.getModelNames());
            }
        }
        return notOwnedModelFiles;
    }

    private String resource2Package(String resource) {
        int pathEndPos = resource.lastIndexOf('/');
        return pathEndPos <= 0 ? "" :resource.substring(0, pathEndPos).replace('/', '.');
    }
}
