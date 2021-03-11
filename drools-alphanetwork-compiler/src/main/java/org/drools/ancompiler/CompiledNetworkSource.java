/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ancompiler;

import java.util.Map;

import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.index.AlphaRangeIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompiledNetworkSource {

    private final Logger logger = LoggerFactory.getLogger(CompiledNetworkSource.class);

    private final String source;
    private final IndexableConstraint indexableConstraint;
    private final String name;
    private final String sourceName;
    private final ObjectTypeNode objectTypeNode;
    private final Map<String, AlphaRangeIndex> rangeIndexDeclarationMap;

    public CompiledNetworkSource(String source,
                                 IndexableConstraint indexableConstraint,
                                 String name,
                                 String sourceName,
                                 ObjectTypeNode objectTypeNode,
                                 Map<String, AlphaRangeIndex> rangeIndexDeclarationMap) {
        this.source = source;
        this.indexableConstraint = indexableConstraint;
        this.name = name;
        this.sourceName = sourceName;
        this.objectTypeNode = objectTypeNode;
        this.rangeIndexDeclarationMap = rangeIndexDeclarationMap;
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

    public CompiledNetwork createInstanceAndSet(Class<?> compiledNetworkClass) {
        CompiledNetwork compiledNetwork = newCompiledNetworkInstance(compiledNetworkClass);
        compiledNetwork.setNetwork(objectTypeNode);
        logger.debug("Updating {} with instance of class: {}",
                     objectTypeNode,
                     compiledNetworkClass.getName());

        return compiledNetwork;
    }

    public CompiledNetwork newCompiledNetworkInstance(Class<?> aClass) {
        try {
            return (CompiledNetwork) aClass.getDeclaredConstructor(org.drools.core.spi.InternalReadAccessor.class, Map.class)
                    .newInstance(getFieldExtractor(), rangeIndexDeclarationMap);
        } catch (Exception e) {
            throw new CouldNotCreateAlphaNetworkCompilerException(e);
        }
    }

    private InternalReadAccessor getFieldExtractor() {
        return indexableConstraint == null ? null : indexableConstraint.getFieldExtractor();
    }
}
