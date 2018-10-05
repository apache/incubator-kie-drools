/*
 * Copyright 2018 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.function.Supplier;

import org.drools.core.util.ScalablePool;

public class StatefulSessionPool {

    private final KnowledgeBaseImpl kbase;
    private final ScalablePool<StatefulKnowledgeSessionImpl> pool;

    public StatefulSessionPool( KnowledgeBaseImpl kbase, int initialSize, Supplier<StatefulKnowledgeSessionImpl> supplier ) {
        this.kbase = kbase;
        this.pool = new ScalablePool<>(initialSize, supplier, s -> s.reset(), s -> s.fromPool(null).dispose());
    }

    public KnowledgeBaseImpl getKieBase() {
        return kbase;
    }

    public StatefulKnowledgeSessionImpl get() {
        return pool.get().fromPool( this );
    }

    public void release(StatefulKnowledgeSessionImpl session) {
        pool.release( session );
    }

    public void shutdown() {
        pool.clear();
    }
}
