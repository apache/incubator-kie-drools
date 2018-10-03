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
import org.kie.api.KieBase;
import org.kie.api.KiePool;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;

public class StatefulSessionPool implements KiePool<KieSession> {

    private final ScalablePool<StatefulKnowledgeSessionImpl> pool;

    public StatefulSessionPool( KieBase kieBase, int initialSize) {
        this(kieBase, null, initialSize);
    }

    public StatefulSessionPool( KieBase kieBase, int initialSize, Supplier<StatefulKnowledgeSessionImpl> supplier) {
        this(kieBase, null, initialSize, supplier);
    }

    public StatefulSessionPool( KieBase kieBase, KieSessionConfiguration conf, int initialSize) {
        this(kieBase, null, initialSize, () -> ((StatefulKnowledgeSessionImpl) kieBase.newKieSession(conf, null)));
    }

    public StatefulSessionPool( KieBase kieBase, KieSessionConfiguration conf, int initialSize, Supplier<StatefulKnowledgeSessionImpl> supplier ) {
        this.pool = new ScalablePool<>(initialSize, supplier, s -> s.reset(), s -> s.fromPool(null).dispose());
    }

    @Override
    public KieSession get() {
        return pool.get().fromPool( this );
    }

    public void release(StatefulKnowledgeSessionImpl session) {
        pool.release( session );
    }

    @Override
    public void shutdown() {
        pool.shutdown();
    }
}
