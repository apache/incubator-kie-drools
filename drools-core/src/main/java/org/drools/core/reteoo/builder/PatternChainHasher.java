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
package org.drools.core.reteoo.builder;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.GroupElement;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates combined hashes for pattern chains in rules, creating hashes for
 * progressively shorter "tails" of the pattern sequence.
 *
 * For example, if a rule has patterns [A, B, C]:
 * - Creates hash for [A, B, C] (full chain)
 * - Creates hash for [B, C] (tail starting at position 1)
 * - Creates hash for [C] (tail starting at position 2)
 *
 * This enables efficient matching of partial pattern sequences across rules.
 */
public class PatternChainHasher {

    public static ChainHashResult generateChainHashes(RuleImpl rule, String kieBaseId) {
        if (rule == null) {
            return new ChainHashResult(Collections.emptyList());
        }

        List<Pattern> patterns = extractPatterns(rule);
        return generateChainHashes(kieBaseId, patterns);
    }

    public static ChainHashResult generateChainHashes(String identifier, List<Pattern> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return new ChainHashResult(Collections.emptyList());
        }

        List<TailHash> tailHashList = new ArrayList<>();

        // Generate hash for each tail starting from position i
        for (int startIndex = 0; startIndex < patterns.size(); startIndex++) {
            List<Pattern> tailPatterns = patterns.subList(startIndex, patterns.size());
            String combinedHash = generateCombinedHash(tailPatterns);

            tailHashList.add(new TailHash(tailPatterns.size(), patterns.size(), combinedHash, new ArrayList<>(tailPatterns)));

            // Use "::" separator for proper scoping (KieBase::hash format)
            String scopedHash = identifier.contains("::") ? identifier + combinedHash : identifier + "::" + combinedHash;

            Pattern currentPattern = tailPatterns.get(0);
            currentPattern.setTailHash(scopedHash);

            // Generate currentHash: individual pattern hash + tail hash
            String individualPatternHash = PatternHashComparator.generateNormalizedHash(currentPattern);
            String currentHash = individualPatternHash + "::" + scopedHash;
            currentPattern.setCurrentHash(currentHash);
        }

        return new ChainHashResult(tailHashList);
    }

    public static String generateCombinedHash(List<Pattern> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return "EMPTY_CHAIN";
        }

        StringBuilder chainHash = new StringBuilder();
        chainHash.append("CHAIN[").append(patterns.size()).append("]:");

        List<String> patternHashes = new ArrayList<>();
        for (int i = 0; i < patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            String patternHash = PatternHashComparator.generateNormalizedHash(pattern);
            // Include position information for ordering sensitivity
            patternHashes.add("E" + i + ":" + patternHash);
        }

        chainHash.append(String.join("|", patternHashes));

        return chainHash.toString();
    }

    public static List<Pattern> extractPatterns(RuleImpl rule) {
        List<Pattern> patterns = new ArrayList<>();

        if (rule != null && rule.getLhs() != null) {
            collectPatterns(rule.getLhs(), patterns);
        }

        return patterns;
    }

    private static void collectPatterns(RuleConditionElement rce, List<Pattern> patterns) {
        if (rce instanceof Pattern pattern) {
            patterns.add(pattern);
        } else if (rce instanceof GroupElement ge) {
            // Only recurse into AND groups - EXISTS, NOT, OR create subnetworks
            if (ge.isAnd()) {
                for (RuleConditionElement child : ge.getChildren()) {
                    collectPatterns(child, patterns);
                }
            }
        }
    }

    public static class ChainHashResult {
        private final List<TailHash> tailHashes;

        public ChainHashResult(List<TailHash> tailHashes) {
            this.tailHashes = Collections.unmodifiableList(tailHashes);
        }

        public List<TailHash> getTailHashes() {
            return tailHashes;
        }

        @Override
        public String toString() {
            return "ChainHashResult{tailCount=" + tailHashes.size() + '}';
        }
    }

    public static class TailHash {
        private final int length;
        private final int max;
        private final String hash;
        private final List<Pattern> patterns;

        public TailHash(int length, int max, String hash, List<Pattern> patterns) {
            this.length = length;
            this.max = max;
            this.hash = hash;
            this.patterns = Collections.unmodifiableList(patterns);
        }

        public int getLength() {
            return length;
        }

        public boolean isFullChain() {
            return max == length;
        }

        public String getHash() {
            return hash;
        }

        public List<Pattern> getPatterns() {
            return patterns;
        }

        @Override
        public String toString() {
            return "TailHash{" +
                    "length=" + length +
                    ", hash='" + hash.substring(0, Math.min(50, hash.length())) + "...'" +
                    '}';
        }
    }
}
