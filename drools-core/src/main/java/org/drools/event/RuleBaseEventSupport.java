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

package org.drools.event;

import org.drools.RuleBase;
import org.drools.rule.Package;
import org.drools.rule.Rule;

import java.util.Iterator;

public class RuleBaseEventSupport extends AbstractEventSupport<RuleBaseEventListener> {
    private transient RuleBase ruleBase;

    public RuleBaseEventSupport() {

    }

    public RuleBaseEventSupport(final RuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    public void setRuleBase(RuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    public void fireBeforePackageAdded(final Package newPkg) {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforePackageAddedEvent event = new BeforePackageAddedEvent(this.ruleBase, newPkg);

            do {
                iter.next().beforePackageAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterPackageAdded(final Package newPkg) {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterPackageAddedEvent event = new AfterPackageAddedEvent(this.ruleBase, newPkg);

            do {
                iter.next().afterPackageAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforePackageRemoved(final Package pkg) {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforePackageRemovedEvent event = new BeforePackageRemovedEvent(this.ruleBase, pkg);

            do {
                iter.next().beforePackageRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterPackageRemoved(final Package pkg) {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterPackageRemovedEvent event = new AfterPackageRemovedEvent(this.ruleBase, pkg);

            do {
                iter.next().afterPackageRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleBaseLocked() {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeRuleBaseLockedEvent event = new BeforeRuleBaseLockedEvent(this.ruleBase);

            do {
                iter.next().beforeRuleBaseLocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleBaseLocked() {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterRuleBaseLockedEvent event = new AfterRuleBaseLockedEvent(this.ruleBase);

            do {
                iter.next().afterRuleBaseLocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleBaseUnlocked() {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeRuleBaseUnlockedEvent event = new BeforeRuleBaseUnlockedEvent(this.ruleBase);

            do {
                iter.next().beforeRuleBaseUnlocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleBaseUnlocked() {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterRuleBaseUnlockedEvent event = new AfterRuleBaseUnlockedEvent(this.ruleBase);

            do {
                iter.next().afterRuleBaseUnlocked(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleAdded(final Package newPkg, final Rule rule) {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeRuleAddedEvent event = new BeforeRuleAddedEvent(this.ruleBase, newPkg, rule);

            do {
                iter.next().beforeRuleAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleAdded(final Package newPkg, final Rule rule) {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterRuleAddedEvent event = new AfterRuleAddedEvent(this.ruleBase, newPkg, rule);

            do {
                iter.next().afterRuleAdded(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeRuleRemoved(final Package pkg, final Rule rule) {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeRuleRemovedEvent event = new BeforeRuleRemovedEvent(this.ruleBase, pkg, rule);

            do {
                iter.next().beforeRuleRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterRuleRemoved(final Package pkg, final Rule rule) {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterRuleRemovedEvent event = new AfterRuleRemovedEvent(this.ruleBase, pkg, rule);

            do {
                iter.next().afterRuleRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireBeforeFunctionRemoved(final Package pkg, final String function) {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final BeforeFunctionRemovedEvent event = new BeforeFunctionRemovedEvent(this.ruleBase, pkg, function);

            do {
                iter.next().beforeFunctionRemoved(event);
            } while (iter.hasNext());
        }
    }

    public void fireAfterFunctionRemoved(final Package pkg, final String function) {
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        if (iter.hasNext()) {
            final AfterFunctionRemovedEvent event = new AfterFunctionRemovedEvent(this.ruleBase, pkg, function);
                    
            do {
                iter.next().afterFunctionRemoved(event);
            } while (iter.hasNext());
        }
    }
}
