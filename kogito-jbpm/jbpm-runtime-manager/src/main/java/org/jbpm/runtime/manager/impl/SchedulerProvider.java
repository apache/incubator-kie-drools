package org.jbpm.runtime.manager.impl;

import org.jbpm.process.core.timer.GlobalSchedulerService;

public interface SchedulerProvider {

    GlobalSchedulerService getSchedulerService();
}
