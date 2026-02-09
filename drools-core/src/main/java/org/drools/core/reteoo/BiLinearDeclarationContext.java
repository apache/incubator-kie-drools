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
package org.drools.core.reteoo;

import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;

import java.util.HashMap;
import java.util.Map;

/**
 * BiLinearDeclarationContext manages variable declarations across two input networks
 * for BiLinearJoinNode. It provides unified declaration lookup that can resolve
 * variables from either input network and handles potential naming conflicts.
 */
public class BiLinearDeclarationContext {
    
    private final Map<String, Declaration> firstNetworkDeclarations;
    
    private final Map<String, Declaration> secondNetworkDeclarations;
    
    private final Map<String, Declaration> combinedDeclarations;
    
    private final Map<String, Integer> declarationNetworkMapping;
    
    private final int secondNetworkOffset;

    public BiLinearDeclarationContext(Map<String, Declaration> firstNetworkDeclarations,
                                     Map<String, Declaration> secondNetworkDeclarations,
                                     int secondNetworkOffset) {
        this.firstNetworkDeclarations = new HashMap<>(firstNetworkDeclarations);
        this.secondNetworkDeclarations = new HashMap<>(secondNetworkDeclarations);
        this.secondNetworkOffset = secondNetworkOffset;
        this.combinedDeclarations = new HashMap<>();
        this.declarationNetworkMapping = new HashMap<>();
        
        buildCombinedDeclarations();
    }

    public BiLinearDeclarationContext(LeftTupleSource firstNetworkSource,
                                     LeftTupleSource secondNetworkSource,
                                     int secondNetworkOffset) {
        this.secondNetworkOffset = secondNetworkOffset;
        this.firstNetworkDeclarations = extractDeclarations(firstNetworkSource);
        this.secondNetworkDeclarations = extractDeclarations(secondNetworkSource);
        this.combinedDeclarations = new HashMap<>();
        this.declarationNetworkMapping = new HashMap<>();
        
        buildCombinedDeclarations();
    }
    
    private void buildCombinedDeclarations() {
        // Add first network declarations (no offset needed)
        for (Map.Entry<String, Declaration> entry : firstNetworkDeclarations.entrySet()) {
            String name = entry.getKey();
            Declaration declaration = entry.getValue();
            
            combinedDeclarations.put(name, declaration);
            declarationNetworkMapping.put(name, 1);
        }
        
        // Add second network declarations with offset and conflict resolution
        for (Map.Entry<String, Declaration> entry : secondNetworkDeclarations.entrySet()) {
            String name = entry.getKey();
            Declaration originalDeclaration = entry.getValue();
            
            // Check for naming conflicts
            if (firstNetworkDeclarations.containsKey(name)) {
                // Conflict detected - need to handle this
                handleDeclarationConflict(name, originalDeclaration);
            } else {
                // No conflict - create offset declaration for second network
                Declaration offsetDeclaration = createOffsetDeclaration(originalDeclaration);
                combinedDeclarations.put(name, offsetDeclaration);
                declarationNetworkMapping.put(name, 2);
            }
        }
    }
    
    /**
     * Handles naming conflicts between networks.
     * When the same variable name exists in both networks, we use the second network's
     * declaration with offset, keeping the original variable name from the rule.
     * This allows rules to reference variables by their original names.
     */
    private void handleDeclarationConflict(String name, Declaration secondNetworkDeclaration) {
        Declaration offsetDeclaration = createOffsetDeclaration(secondNetworkDeclaration);

        // Replace the first network's declaration with the second network's (with offset)
        combinedDeclarations.put(name, offsetDeclaration);
        declarationNetworkMapping.put(name, 2);
    }

    private Declaration createOffsetDeclaration(Declaration original) {
        Declaration offsetDeclaration = new Declaration(
            original.getIdentifier(),
            original.getExtractor(),
            createOffsetPattern(original.getPattern())
        );
        
        offsetDeclaration.setDeclarationClass(original.getDeclarationClass());
        
        return offsetDeclaration;
    }

    private Pattern createOffsetPattern(Pattern original) {
        if (original == null) {
            return null;
        }
        
        Pattern offsetPattern = new Pattern(
            original.getPatternId(),
            original.getTupleIndex() + secondNetworkOffset,
            original.getObjectIndex() + secondNetworkOffset,
            original.getObjectType(),
            original.getDeclaration() != null ? original.getDeclaration().getIdentifier() : null
        );
        
        return offsetPattern;
    }
    
    private Map<String, Declaration> extractDeclarations(LeftTupleSource source) {
        return new HashMap<>();
    }

    public BiLinearDeclarationContext copy() {
        return new BiLinearDeclarationContext(
            firstNetworkDeclarations,
            secondNetworkDeclarations,
            secondNetworkOffset
        );
    }
    
    @Override
    public String toString() {
        return "BiLinearDeclarationContext{" +
                "firstNetwork=" + firstNetworkDeclarations.size() + " declarations, " +
                "secondNetwork=" + secondNetworkDeclarations.size() + " declarations, " +
                "combined=" + combinedDeclarations.size() + " declarations, " +
                "offset=" + secondNetworkOffset +
                '}';
    }
}