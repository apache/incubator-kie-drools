package org.kie.concurrent;

import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;

import org.kie.api.Service;

public interface KieExecutors extends Service {

    Executor getExecutor();

    <T> CompletionService<T> getCompletionService();
}
