package org.kie.api.runtime;

import org.kie.api.command.Command;

/**
 * A pool of session created from a KieContainer
 */
public interface KieContainerSessionsPool extends KieSessionsPool {

    /**
     * Obtain a {@link KieSession} with the given name from this pool.
     * Calling {@link KieSession#dispose()} on this session when you are done will push it back into the pool.
     *
     * @return created {@link KieSession}
     * @throws RuntimeException if the KieContainer doesn't have any KieSession with the given name
     */
    KieSession newKieSession(String kSessionName);

    /**
     * Obtain a {@link KieSession} with the given name and configuration from this pool.
     * Calling {@link KieSession#dispose()} on this session when you are done will push it back into the pool.
     *
     * @return created {@link KieSession}
     * @throws RuntimeException if the KieContainer doesn't have any KieSession with the given name
     */
    KieSession newKieSession(String kSessionName, KieSessionConfiguration conf);

    /**
     * Obtain a {@link StatelessKieSession} with the given name from this pool.
     * Note that, what is pooled here is not {@link StatelessKieSession} but the {@link KieSession} that it internally
     * wraps, so calling multiple times {@link KieSession#execute(Command)} ()} (or one of its overload) will
     * make this {@link StatelessKieSession} to get a {@link KieSession} from the pool instead of creating a new one.
     *
     * @return created {@link StatelessKieSession}
     * @throws RuntimeException if this KieContainer doesn't have any StatelessKieSession with the given name
     */
    StatelessKieSession newStatelessKieSession(String kSessionName);

    /**
     * Obtain a {@link StatelessKieSession} with the given name and configuration from this pool.
     * Note that, what is pooled here is not {@link StatelessKieSession} but the {@link KieSession} that it internally
     * wraps, so calling multiple times {@link KieSession#execute(Command)} ()} (or one of its overload) will
     * make this {@link StatelessKieSession} to get a {@link KieSession} from the pool instead of creating a new one.
     *
     * @return created {@link StatelessKieSession}
     * @throws RuntimeException if this KieContainer doesn't have any StatelessKieSession with the given name
     */
    StatelessKieSession newStatelessKieSession(String kSessionName, KieSessionConfiguration conf);
}
