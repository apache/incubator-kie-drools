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

import org.drools.base.rule.Pattern;
import org.drools.core.reteoo.BiLinearJoinNode;

import java.util.List;

public class BiLinearBuildHelper {

    public static boolean canUseBiLinearJoin(BuildContext context) {
        if (!BiLinearDetector.isBiLinearEnabled()) {
            return false;
        }

        BiLinearDetector.Pair matchingPair = findMatchingPair(context);
        if (matchingPair == null) {
            return false;
        }

        return context.getRule().getName().equals(matchingPair.consumerRuleName());
    }

    public static BiLinearDetector.Pair findMatchingPair(BuildContext context) {
        String tailHash = getTailHash(context);
        if (tailHash == null) {
            return null;
        }

        List<BiLinearDetector.Pair> pairList = context.getBiLinearContext().sharedChains().get(tailHash);
        if (pairList != null && !pairList.isEmpty()) {
            String currentRuleName = context.getRule() != null ? context.getRule().getName() : null;

            for (BiLinearDetector.Pair pair : pairList) {
                if (currentRuleName != null && currentRuleName.equals(pair.consumerRuleName())) {
                    return pair;
                }
            }
        }

        return null;
    }

    public static String getTailHash(BuildContext context) {
        if (context.getBiLinearContext() == null || context.getBiLinearContext().sharedChains().isEmpty()) {
            return null;
        }

        List<Pattern> patterns = context.getPatterns();
        if (patterns == null || patterns.isEmpty()) {
            return null;
        }

        for (int i = patterns.size() - 1; i >= 0; i--) {
            Pattern p = patterns.get(i);
            String pTailHash = p.getTailHash();
            if (pTailHash != null && context.getBiLinearContext().sharedChains().containsKey(pTailHash)) {
                return pTailHash;
            }
        }

        return null;
    }

    public static void registerBiLinearNode(BuildContext context, BiLinearJoinNode biLinearNode) {
        String tailHash = getTailHash(context);
        if (tailHash != null) {
            context.getBiLinearContext().setBiLinearNode(tailHash, biLinearNode);
        }
    }
}
