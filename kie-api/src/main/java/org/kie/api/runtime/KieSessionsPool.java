/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
