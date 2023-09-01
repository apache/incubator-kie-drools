package org.kie.api.event;

import org.kie.api.event.process.ProcessEventManager;
import org.kie.api.event.rule.RuleRuntimeEventManager;
import org.kie.api.logger.KieRuntimeLogger;

/**
 * A manager for runtime events.
 */
public interface KieRuntimeEventManager
    extends
    RuleRuntimeEventManager,
    ProcessEventManager {

    /**
     * Returns the KieRuntimeLogger registered on this KieRuntimeEventManager if any
     */
    KieRuntimeLogger getLogger();
}
