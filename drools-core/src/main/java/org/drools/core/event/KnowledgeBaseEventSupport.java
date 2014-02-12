/*
 * Copyright 2007 JBoss Inc
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
import org.drools.core.impl.InternalKnowledgeBase;
import org.kie.api.definition.process.Process;

import java.util.Iterator;

public class KnowledgeBaseEventSupport extends AbstractEventSupport<KnowledgeBaseEventListener> {
    private transient InternalKnowledgeBase kBase;

    public KnowledgeBaseEventSupport() {

    }

    public KnowledgeBaseEventSupport(final InternalKnowledgeBase kBase) {
        this.kBase = kBase;
    }

    public void setKnowledgeBase(InternalKnowledgeBase kBase) {
        this.kBase = kBase;
    }

    public void fireBeforePackageAdded(final InternalKnowledgePackage newPkg) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforePackageAddedEvent event = new BeforePackageAddedEvent(this.kBase, newPkg);

            do {
                iter.next().beforePackageAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterPackageAdded(final InternalKnowledgePackage newPkg) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterPackageAddedEvent event = new AfterPackageAddedEvent(this.kBase, newPkg);

            do {
                iter.next().afterPackageAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforePackageRemoved(InternalKnowledgePackage pkg) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforePackageRemovedEvent event = new BeforePackageRemovedEvent(this.kBase, pkg);

            do {
                iter.next().beforePackageRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterPackageRemoved(InternalKnowledgePackage pkg) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterPackageRemovedEvent event = new AfterPackageRemovedEvent(this.kBase, pkg);

            do {
                iter.next().afterPackageRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleBaseLocked() {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeRuleBaseLockedEvent event = new BeforeRuleBaseLockedEvent(this.kBase);

            do {
                iter.next().beforeRuleBaseLocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleBaseLocked() {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterRuleBaseLockedEvent event = new AfterRuleBaseLockedEvent(this.kBase);

            do {
                iter.next().afterRuleBaseLocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleBaseUnlocked() {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeRuleBaseUnlockedEvent event = new BeforeRuleBaseUnlockedEvent(this.kBase);

            do {
                iter.next().beforeRuleBaseUnlocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleBaseUnlocked() {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterRuleBaseUnlockedEvent event = new AfterRuleBaseUnlockedEvent(this.kBase);

            do {
                iter.next().afterRuleBaseUnlocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleAdded(final InternalKnowledgePackage newPkg, final RuleImpl rule) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeRuleAddedEvent event = new BeforeRuleAddedEvent(this.kBase, newPkg, rule);

            do {
                iter.next().beforeRuleAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleAdded(final InternalKnowledgePackage newPkg, final RuleImpl rule) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterRuleAddedEvent event = new AfterRuleAddedEvent(this.kBase, newPkg, rule);

            do {
                iter.next().afterRuleAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleRemoved(final InternalKnowledgePackage pkg, final RuleImpl rule) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeRuleRemovedEvent event = new BeforeRuleRemovedEvent(this.kBase, pkg, rule);

            do {
                iter.next().beforeRuleRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleRemoved(final InternalKnowledgePackage pkg, final RuleImpl rule) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterRuleRemovedEvent event = new AfterRuleRemovedEvent(this.kBase, pkg, rule);

            do {
                iter.next().afterRuleRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeFunctionRemoved(final InternalKnowledgePackage pkg, final String function) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeFunctionRemovedEvent event = new BeforeFunctionRemovedEvent(this.kBase, pkg, function);

            do {
                iter.next().beforeFunctionRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterFunctionRemoved(final InternalKnowledgePackage pkg, final String function) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterFunctionRemovedEvent event = new AfterFunctionRemovedEvent(this.kBase, pkg, function);
                    
            do {
                iter.next().afterFunctionRemoved(event);
            } while (iter.hasNext());
        }
    }
    
    public void fireBeforeProcessAdded(final Process process) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeProcessAddedEvent event = new BeforeProcessAddedEvent(process);

            do {
                iter.next().beforeProcessAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterProcessAdded(final Process process) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterProcessAddedEvent event = new AfterProcessAddedEvent(process);

            do {
                iter.next().afterProcessAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeProcessRemoved(final Process process) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeProcessRemovedEvent event = new BeforeProcessRemovedEvent(process);

            do {
                iter.next().beforeProcessRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterProcessRemoved(final Process process) {
        final Iterator<KnowledgeBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterProcessRemovedEvent event = new AfterProcessRemovedEvent(process);

            do {
                iter.next().afterProcessRemoved(event);
            } while (iter.hasNext());
        }
    }

}
