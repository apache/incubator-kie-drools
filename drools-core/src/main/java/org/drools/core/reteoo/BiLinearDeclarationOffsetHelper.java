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

import java.util.HashMap;
import java.util.Map;

import org.drools.base.rule.Declaration;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;

/**
 * Helper for adjusting declaration tuple indices when a rule uses BiLinearJoinNode.
 *
 * When a rule shares a network segment via BiLinear optimization, declarations
 * from the second network have tupleIndex values relative to that network (0, 1, ...)
 * rather than the combined tuple (firstNetworkSize, firstNetworkSize+1, ...).
 *
 * This helper creates offset-adjusted declarations for correct tuple access.
 */
public class BiLinearDeclarationOffsetHelper {

    private final int firstNetworkSize;
    private final Map<Pattern, Integer> patternExpectedPositions;

    public BiLinearDeclarationOffsetHelper(GroupElement subrule, int firstNetworkSize) {
        this.firstNetworkSize = firstNetworkSize;
        this.patternExpectedPositions = new HashMap<>();
        collectPatternsInOrder(subrule, 0);
    }

    public Map<String, Declaration> createOffsetDeclarations(Map<String, Declaration> originalDecls) {
        Map<String, Declaration> result = new HashMap<>();
        for (Map.Entry<String, Declaration> entry : originalDecls.entrySet()) {
            Declaration adjusted = adjustIfNeeded(entry.getValue());
            result.put(entry.getKey(), adjusted);
        }
        return result;
    }

    private Declaration adjustIfNeeded(Declaration original) {
        Pattern pattern = original.getPattern();
        if (pattern == null) {
            return original;
        }

        Integer expectedPos = patternExpectedPositions.get(pattern);
        if (expectedPos == null) {
            return original;
        }

        // Pattern needs offset if:
        // 1. Expected position >= firstNetworkSize (logically in second network)
        // 2. Current tupleIndex < firstNetworkSize (shared pattern, not yet offset)
        boolean needsOffset = expectedPos >= firstNetworkSize
                           && pattern.getTupleIndex() < firstNetworkSize;
        if (!needsOffset) {
            return original;
        }

        int offset = expectedPos - pattern.getTupleIndex();
        return createOffsetDeclaration(original, offset);
    }

    private Declaration createOffsetDeclaration(Declaration original, int offset) {
        Pattern originalPattern = original.getPattern();

        Pattern offsetPattern = new Pattern(
            originalPattern.getPatternId(),
            originalPattern.getTupleIndex() + offset,
            originalPattern.getObjectIndex() + offset,
            originalPattern.getObjectType(),
            original.getIdentifier()
        );

        Declaration offsetDecl = new Declaration(
            original.getIdentifier(),
            original.getExtractor(),
            offsetPattern
        );
        offsetDecl.setDeclarationClass(original.getDeclarationClass());

        return offsetDecl;
    }

    private int collectPatternsInOrder(RuleConditionElement element, int currentPosition) {
        if (element instanceof Pattern) {
            patternExpectedPositions.put((Pattern) element, currentPosition);
            return currentPosition + 1;
        } else if (element instanceof GroupElement) {
            GroupElement ge = (GroupElement) element;
            for (RuleConditionElement child : ge.getChildren()) {
                currentPosition = collectPatternsInOrder(child, currentPosition);
            }
        }
        return currentPosition;
    }
}
