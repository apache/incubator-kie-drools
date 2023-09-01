package org.kie.api.runtime.rule;

public interface ConsequenceExceptionHandler {
    void handleException(Match match,
                         RuleRuntime workingMemory,
                         Exception exception);
}
