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
package org.drools.reliability.core.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.core.reteoo.RuleTerminalNodeLeftTuple;
import org.drools.reliability.core.ReliabilityRuntimeException;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

public class ReliabilityUtils {

    private ReliabilityUtils() {
        // no constructor
    }

    /**
     * Returns a String representation of the activation.
     */
    public static String getActivationKey(Match match) {
        return getActivationKey(match, null);
    }

    /**
     * Returns a String representation of the activation, replacing the new fact handle id with the old fact handle id.
     * Used to find an activation key in the persisted storage.
     */
    public static String getActivationKeyReplacingNewIdWithOldId(Match match, Map<Long, Long> factHandleIdMap) {
        return getActivationKey(match, factHandleIdMap);
    }

    private static String getActivationKey(Match match, Map<Long, Long> factHandleIdMap) {
        if (!(match instanceof RuleTerminalNodeLeftTuple)) {
            throw new ReliabilityRuntimeException("getActivationKey doesn't support " + match.getClass());
        }
        RuleTerminalNodeLeftTuple ruleTerminalNodeLeftTuple = (RuleTerminalNodeLeftTuple) match;
        String packageName = ruleTerminalNodeLeftTuple.getRule().getPackageName();
        String ruleName = ruleTerminalNodeLeftTuple.getRule().getName();
        List<FactHandle> factHandles = ruleTerminalNodeLeftTuple.getFactHandles();
        List<Long> factHandleIdList = factHandles.stream()
                                                 .map(FactHandle::getId)
                                                 .map(handleId -> {
                                                     if (factHandleIdMap != null) {
                                                         return factHandleIdMap.get(handleId); // replace new id with old id
                                                     } else {
                                                         return handleId; // don't replace
                                                     }
                                                 })
                                                 .collect(Collectors.toList());
        return "ActivationKey [packageName=" + packageName + ", ruleName=" + ruleName + ", factHandleIdList=" + factHandleIdList + "]";
    }
}
