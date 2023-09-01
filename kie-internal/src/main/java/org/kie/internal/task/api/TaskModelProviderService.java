package org.kie.internal.task.api;

import org.kie.api.internal.utils.KieService;

public interface TaskModelProviderService extends KieService {

    TaskModelFactory getTaskModelFactory();

}
