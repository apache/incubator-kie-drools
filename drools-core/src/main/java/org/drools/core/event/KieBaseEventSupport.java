/*
 * Copyright 2007 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.event;

import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.knowlegebase.impl.AfterFunctionRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterKiePackageAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterKiePackageRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterKnowledgeBaseLockedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterKnowledgeBaseUnlockedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterProcessAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterProcessRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterRuleAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.AfterRuleRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeFunctionRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeKiePackageAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeKiePackageRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeKnowledgeBaseLockedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeKnowledgeBaseUnlockedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeProcessAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeProcessRemovedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeRuleAddedEventImpl;
import org.drools.core.event.knowlegebase.impl.BeforeRuleRemovedEventImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.definition.process.Process;
import org.kie.api.event.kiebase.AfterFunctionRemovedEvent;
import org.kie.api.event.kiebase.AfterKieBaseLockedEvent;
import org.kie.api.event.kiebase.AfterKieBaseUnlockedEvent;
import org.kie.api.event.kiebase.AfterKiePackageAddedEvent;
import org.kie.api.event.kiebase.AfterKiePackageRemovedEvent;
import org.kie.api.event.kiebase.AfterProcessAddedEvent;
import org.kie.api.event.kiebase.AfterProcessRemovedEvent;
import org.kie.api.event.kiebase.AfterRuleAddedEvent;
import org.kie.api.event.kiebase.AfterRuleRemovedEvent;
import org.kie.api.event.kiebase.BeforeFunctionRemovedEvent;
import org.kie.api.event.kiebase.BeforeKieBaseLockedEvent;
import org.kie.api.event.kiebase.BeforeKieBaseUnlockedEvent;
import org.kie.api.event.kiebase.BeforeKiePackageAddedEvent;
import org.kie.api.event.kiebase.BeforeKiePackageRemovedEvent;
import org.kie.api.event.kiebase.BeforeProcessAddedEvent;
import org.kie.api.event.kiebase.BeforeProcessRemovedEvent;
import org.kie.api.event.kiebase.BeforeRuleAddedEvent;
import org.kie.api.event.kiebase.BeforeRuleRemovedEvent;
import org.kie.api.event.kiebase.KieBaseEventListener;

import java.util.Iterator;

public class KieBaseEventSupport extends AbstractEventSupport<KieBaseEventListener> {
    private transient InternalKnowledgeBase kBase;

    public KieBaseEventSupport() {

    }

    public KieBaseEventSupport(final InternalKnowledgeBase kBase) {
        this.kBase = kBase;
    }

    public void setKnowledgeBase(InternalKnowledgeBase kBase) {
        this.kBase = kBase;
    }

    public void fireBeforePackageAdded(final InternalKnowledgePackage newPkg) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            BeforeKiePackageAddedEvent event = new BeforeKiePackageAddedEventImpl(this.kBase, newPkg);

            do {
                iter.next().beforeKiePackageAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterPackageAdded(final InternalKnowledgePackage newPkg) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AfterKiePackageAddedEvent event = new AfterKiePackageAddedEventImpl(this.kBase, newPkg);

            do {
                iter.next().afterKiePackageAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforePackageRemoved(InternalKnowledgePackage pkg) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            BeforeKiePackageRemovedEvent event = new BeforeKiePackageRemovedEventImpl(this.kBase, pkg);

            do {
                iter.next().beforeKiePackageRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterPackageRemoved(InternalKnowledgePackage pkg) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AfterKiePackageRemovedEvent event = new AfterKiePackageRemovedEventImpl(this.kBase, pkg);

            do {
                iter.next().afterKiePackageRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleBaseLocked() {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            BeforeKieBaseLockedEvent event = new BeforeKnowledgeBaseLockedEventImpl(this.kBase);

            do {
                iter.next().beforeKieBaseLocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleBaseLocked() {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AfterKieBaseLockedEvent event = new AfterKnowledgeBaseLockedEventImpl(this.kBase);

            do {
                iter.next().afterKieBaseLocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleBaseUnlocked() {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            BeforeKieBaseUnlockedEvent event = new BeforeKnowledgeBaseUnlockedEventImpl(this.kBase);

            do {
                iter.next().beforeKieBaseUnlocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleBaseUnlocked() {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AfterKieBaseUnlockedEvent event = new AfterKnowledgeBaseUnlockedEventImpl(this.kBase);

            do {
                iter.next().afterKieBaseUnlocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleAdded(final InternalKnowledgePackage newPkg, final RuleImpl rule) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            BeforeRuleAddedEvent event = new BeforeRuleAddedEventImpl(this.kBase, rule);

            do {
                iter.next().beforeRuleAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleAdded(final InternalKnowledgePackage newPkg, final RuleImpl rule) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AfterRuleAddedEvent event = new AfterRuleAddedEventImpl(this.kBase, rule);

            do {
                iter.next().afterRuleAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleRemoved(final InternalKnowledgePackage pkg, final RuleImpl rule) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeRuleRemovedEvent event = new BeforeRuleRemovedEventImpl(this.kBase, rule);

            do {
                iter.next().beforeRuleRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleRemoved(final InternalKnowledgePackage pkg, final RuleImpl rule) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AfterRuleRemovedEvent event = new AfterRuleRemovedEventImpl(this.kBase, rule);

            do {
                iter.next().afterRuleRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeFunctionRemoved(final InternalKnowledgePackage pkg, final String function) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            BeforeFunctionRemovedEvent event = new BeforeFunctionRemovedEventImpl(this.kBase, function);

            do {
                iter.next().beforeFunctionRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterFunctionRemoved(final InternalKnowledgePackage pkg, final String function) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AfterFunctionRemovedEvent event = new AfterFunctionRemovedEventImpl(this.kBase, function);
                    
            do {
                iter.next().afterFunctionRemoved(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeProcessAdded(final Process process) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            BeforeProcessAddedEvent event = new BeforeProcessAddedEventImpl(this.kBase, process);

            do {
                iter.next().beforeProcessAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterProcessAdded(final Process process) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AfterProcessAddedEvent event = new AfterProcessAddedEventImpl(this.kBase, process);

            do {
                iter.next().afterProcessAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeProcessRemoved(final Process process) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            BeforeProcessRemovedEvent event = new BeforeProcessRemovedEventImpl(this.kBase, process);

            do {
                iter.next().beforeProcessRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterProcessRemoved(final Process process) {
        final Iterator<KieBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            AfterProcessRemovedEvent event = new AfterProcessRemovedEventImpl(this.kBase, process);

            do {
                iter.next().afterProcessRemoved(event);
            } while (iter.hasNext());
        }
    }

}
