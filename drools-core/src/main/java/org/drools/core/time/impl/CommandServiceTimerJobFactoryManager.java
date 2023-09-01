package org.drools.core.time.impl;

import org.kie.api.runtime.ExecutableRunner;

public interface CommandServiceTimerJobFactoryManager extends TimerJobFactoryManager {
    void setRunner(ExecutableRunner runner );
    ExecutableRunner getRunner();
}
