package org.kie.api.runtime;

import org.kie.api.command.Command;

/**
 * A pool of session created from a KieContainer
 */
public interface KieSessionsPool {

    /**
     * Obtain a {@link KieSession} from this pool using the default session configuration.
     * Calling {@link KieSession#dispose()} on this session when you are done will push it back into the pool.
     *
     * @return created {@link KieSession}
     */
    KieSession newKieSession();

    /**
     * Obtain a {@link KieSession} from this pool using using the given session configuration.
     * Calling {@link KieSession#dispose()} on this session when you are done will push it back into the pool.
     *
     * @return created {@link KieSession}
     */
    KieSession newKieSession(KieSessionConfiguration conf);

    /**
     * Obtain a {@link StatelessKieSession} from this pool using the default session configuration.
     * You do not need to call @{link #dispose()} on this.
     * Note that, what is pooled here is not {@link StatelessKieSession} but the {@link KieSession} that it internally
     * wraps, so calling multiple times {@link KieSession#execute(Command)} ()} (or one of its overload) will
     * make this {@link StatelessKieSession} to get a {@link KieSession} from the pool instead of creating a new one.
     *
     * @return created {@link StatelessKieSession}
     */
    StatelessKieSession newStatelessKieSession();

    /**
     * Obtain a {@link StatelessKieSession} from this pool using using the given session configuration.
     * You do not need to call @{link #dispose()} on this.
     * Note that, what is pooled here is not {@link StatelessKieSession} but the {@link KieSession} that it internally
     * wraps, so calling multiple times {@link KieSession#execute(Command)} ()} (or one of its overload) will
     * make this {@link StatelessKieSession} to get a {@link KieSession} from the pool instead of creating a new one.
     *
     * @return created {@link StatelessKieSession}
     */
    StatelessKieSession newStatelessKieSession( KieSessionConfiguration conf );

    /**
     * Shutdown this pool and clean up all the resources
     */
    void shutdown();
}
