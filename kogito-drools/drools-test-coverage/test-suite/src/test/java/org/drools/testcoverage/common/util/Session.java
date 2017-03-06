/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.common.util;

import org.assertj.core.api.Assertions;
import org.kie.api.command.Command;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.CommandExecutor;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.event.KnowledgeRuntimeEventManager;

import java.util.Collection;

/**
 * Convenient class to help generalizing drools StatelessKnowledgeSession and
 * StatefulKnowledgeSession. This is least common implementation. The sessions
 * are stored within and are created during construction of this class. Session
 * implements CommandExecutor and KnowledgeRuntimeEventManager interfaces by
 * delegating the methods directly to stateless or stateful knowledge session instance.
 */
public class Session implements CommandExecutor, KnowledgeRuntimeEventManager {
    private KieRuntimeEventManager session;
    private final boolean stateful;
    private final boolean persisted;

    protected Session(KieRuntimeEventManager session, boolean stateful, boolean persisted) {
        this.session = session;
        this.stateful = stateful;
        this.persisted = persisted;
    }

    public boolean isPersisted() {
        return persisted;
    }

    public boolean isStateful() {
        return this.stateful;
    }

    /**
     * Casts this session to StatelessKieSession
     *
     * @throws IllegalArgumentException
     *             - when this session is not stateless
     * @return StatelessKieSession from within this session
     */
    public StatelessKieSession getStateless() {
        if (this.isStateful()) {
            throw new IllegalStateException("This session is not stateless");
        }
        return (StatelessKieSession) session;
    }

    /**
     * Casts this session to KieSession
     *
     * @throws IllegalArgumentException
     *             - when this session is not stateful
     * @return KieSession from within this session
     */
    public KieSession getStateful() {
        if (!this.isStateful()) {
            throw new IllegalStateException("This session is not stateful");
        }
        return (KieSession) session;
    }

    /**
     * If this session is stateful it is disposed
     */
    public void dispose() {
        if (this.isStateful()) {
            KieSession s = this.getStateful();
            try {
                s.dispose();
            } catch (IllegalStateException ex) {
                Assertions.assertThat(ex.getMessage()).isEqualTo("Illegal method call. This session was previously disposed.");
            } finally {
                session = null;
            }
        } else {
            session = null;
        }
    }

    private CommandExecutor getCommandExecutor() {
        return (CommandExecutor) session;
    }

    @Override
    public <T> T execute(Command<T> command) {
        return getCommandExecutor().execute(command);
    }

    @Override
    public void addEventListener(AgendaEventListener arg0) {
        this.session.addEventListener(arg0);
    }

    @Override
    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return this.session.getAgendaEventListeners();
    }

    @Override
    public void removeEventListener(AgendaEventListener arg0) {
        this.session.removeEventListener(arg0);
    }

    @Override
    public void addEventListener(ProcessEventListener arg0) {
        this.session.addEventListener(arg0);
    }

    @Override
    public Collection<ProcessEventListener> getProcessEventListeners() {
        return this.session.getProcessEventListeners();
    }

    @Override
    public void removeEventListener(ProcessEventListener arg0) {
        this.session.removeEventListener(arg0);
    }

    public void setGlobal(String identifier, Object value) {
        if (stateful) {
            getStateful().setGlobal(identifier, value);
        } else {
            getStateless().setGlobal(identifier, value);
        }
    }

    @Override
    public KieRuntimeLogger getLogger() {
        if (stateful) {
            return getStateful().getLogger();
        } else {
            return getStateless().getLogger();
        }
    }

    @Override
    public void addEventListener(RuleRuntimeEventListener listener) {
        if (stateful) {
            getStateful().addEventListener(listener);
        } else {
            getStateless().addEventListener(listener);
        }
    }

    @Override
    public void removeEventListener(RuleRuntimeEventListener listener) {
        if (stateful) {
            getStateful().removeEventListener(listener);
        } else {
            getStateless().removeEventListener(listener);
        }
    }

    @Override
    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
        if (stateful) {
            return getStateful().getRuleRuntimeEventListeners();
        } else {
            return getStateless().getRuleRuntimeEventListeners();
        }
    }
}
