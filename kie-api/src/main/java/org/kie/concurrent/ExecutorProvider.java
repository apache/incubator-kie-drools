package org.kie.concurrent;

import org.kie.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;

public interface ExecutorProvider extends Service {

    Executor getExecutor();

    <T> CompletionService<T> getCompletionService();
}
