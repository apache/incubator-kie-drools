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
package org.drools.ancompiler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.base.rule.IndexableConstraint;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.core.util.index.AlphaRangeIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompiledNetworkSources {

    private final Logger logger = LoggerFactory.getLogger(CompiledNetworkSources.class);

    private final String source;
    private final IndexableConstraint indexableConstraint;
    private final String name;
    private final String sourceName;
    private final ObjectTypeNode objectTypeNode;
    private final Map<String, AlphaRangeIndex> rangeIndexDeclarationMap;
    private Collection<CompilationUnit> initClasses;

    public CompiledNetworkSources(String source,
                                  IndexableConstraint indexableConstraint,
                                  String name,
                                  String sourceName,
                                  ObjectTypeNode objectTypeNode,
                                  Map<String, AlphaRangeIndex> rangeIndexDeclarationMap,
                                  Collection<CompilationUnit> initClasses) {
        this.source = source;
        this.indexableConstraint = indexableConstraint;
        this.name = name;
        this.sourceName = sourceName;
        this.objectTypeNode = objectTypeNode;
        this.rangeIndexDeclarationMap = rangeIndexDeclarationMap;
        this.initClasses = initClasses;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public String getSourceName() {
        return sourceName;
    }

    public Collection<CompilationUnit> getInitClasses() {
        return initClasses;
    }

    public CompiledNetwork createInstanceAndSet(Class<?> compiledNetworkClass) {
        CompiledNetwork compiledNetwork = newCompiledNetworkInstance(compiledNetworkClass);
        compiledNetwork.setStartingObjectTypeNode(objectTypeNode);
        logger.debug("Setting {} as starting node of: {}",
                     objectTypeNode,
                     compiledNetworkClass.getName());

        return compiledNetwork;
    }

    public CompiledNetwork newCompiledNetworkInstance(Class<?> aClass) {
        try {
            return (CompiledNetwork) aClass.getDeclaredConstructor(ReadAccessor.class, Map.class)
                    .newInstance(getFieldExtractor(), rangeIndexDeclarationMap);
        } catch (Exception e) {
            throw new CouldNotCreateAlphaNetworkCompilerException(e);
        }
    }

    private ReadAccessor getFieldExtractor() {
        return indexableConstraint == null ? null : indexableConstraint.getFieldExtractor();
    }

    public Map<String, String> getAllGeneratedSources() {
        Map<String, String> allGeneratedSources = new HashMap<>();

        allGeneratedSources.put(getName(), getSource());

        for(CompilationUnit ch : getInitClasses()) {
            PackageDeclaration packageDeclaration = (PackageDeclaration) ch.getChildNodes().get(0);
            ClassOrInterfaceDeclaration classOrInterfaceDeclaration = (ClassOrInterfaceDeclaration) ch.getChildNodes().get(1);
            String classNameWithPackage = String.format("%s.%s", packageDeclaration.getNameAsString(),
                                                        classOrInterfaceDeclaration.getNameAsString());
            allGeneratedSources.put(classNameWithPackage, ch.toString());
        }

        return allGeneratedSources;
    }
}
