/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.impl;

import org.drools.core.SessionConfiguration;
import org.kie.internal.concurrent.ExecutorProviderFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

public class SessionsCache {

    private final Map<SessionConfiguration, Queue<StatefulKnowledgeSessionImpl>> cleanSessions = new ConcurrentHashMap<SessionConfiguration, Queue<StatefulKnowledgeSessionImpl>>();
    private final boolean isAsync;

    SessionsCache(boolean isAsync) {
        this.isAsync = isAsync;
    }

    public void store(StatefulKnowledgeSessionImpl session) {
        if (isAsync) {
            SessionResetterHolder.SESSION_RESETTER.enqueue(this, session);
        } else {
            session.reset();
            storeResettedSession(session);
        }
    }

    private void storeResettedSession(StatefulKnowledgeSessionImpl session) {
        Queue<StatefulKnowledgeSessionImpl> cache = cleanSessions.get(session.getSessionConfiguration());
        if (cache == null) {
            cache = new ConcurrentLinkedQueue<StatefulKnowledgeSessionImpl>();
            cleanSessions.put(session.getSessionConfiguration(), cache);
        }
        cache.offer(session);
    }

    public StatefulKnowledgeSessionImpl getCachedSession(SessionConfiguration config) {
        Queue<StatefulKnowledgeSessionImpl> cache = cleanSessions.get(config);
        return cache != null ? cache.poll() : null;
    }

    private static class SessionResetterHolder {
        private static final SessionResetter SESSION_RESETTER = new SessionResetter();
    }

    private static class SessionResetter {
        private final Executor executor = ExecutorProviderFactory.getExecutorProvider().newSingleThreadExecutor();

        private final BlockingQueue<SessionSlot> dirtySessions = new ArrayBlockingQueue<SessionSlot>(20);

        private SessionResetter() {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            dirtySessions.take().doReset();
                        } catch (InterruptedException e) { }
                    }
                }
            });
        }

        private void enqueue(SessionsCache sessionsCache, StatefulKnowledgeSessionImpl session) {
            dirtySessions.offer(new SessionSlot(sessionsCache, session));
        }
    }

    private static class SessionSlot {
        private final SessionsCache sessionsCache;
        private final StatefulKnowledgeSessionImpl session;

        private SessionSlot(SessionsCache sessionsCache, StatefulKnowledgeSessionImpl session) {
            this.sessionsCache = sessionsCache;
            this.session = session;
        }

        private void doReset() {
            session.reset();
            sessionsCache.storeResettedSession(session);
        }
    }
}
