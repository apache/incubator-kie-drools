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
