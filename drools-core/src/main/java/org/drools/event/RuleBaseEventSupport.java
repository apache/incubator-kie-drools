package org.drools.event;

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

import org.drools.RuleBase;
import org.drools.rule.Package;
import org.drools.rule.Rule;

import java.util.Iterator;

/**
 * Please note that any event notification methods, e.g. <method>fireBeforePackageAdded</method>, etc.,
 * always create the event and iterator regardless if there are listeners. This is because if the
 * check is to see if there are listeners via the <method>isEmpty</method> method, theoretically
 * there should be synchonrization involved to ensure the <method>isEmpty</method> and
 * </method>getEventListenersIterator</method> both see the same list contents.
 *
 * @author etirelli
 * @author <a href="mailto:stampy88@yahoo.com">dave sinclair</a>
 */
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
        final BeforePackageAddedEvent event = new BeforePackageAddedEvent(this.ruleBase, newPkg);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().beforePackageAdded(event);
        }
    }

    public void fireAfterPackageAdded(final Package newPkg) {
        final AfterPackageAddedEvent event = new AfterPackageAddedEvent(this.ruleBase, newPkg);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().afterPackageAdded(event);
        }
    }

    public void fireBeforePackageRemoved(final Package pkg) {
        final BeforePackageRemovedEvent event = new BeforePackageRemovedEvent(this.ruleBase, pkg);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().beforePackageRemoved(event);
        }
    }

    public void fireAfterPackageRemoved(final Package pkg) {
        final AfterPackageRemovedEvent event = new AfterPackageRemovedEvent(this.ruleBase, pkg);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().afterPackageRemoved(event);
        }
    }

    public void fireBeforeRuleBaseLocked() {
        final BeforeRuleBaseLockedEvent event = new BeforeRuleBaseLockedEvent(this.ruleBase);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().beforeRuleBaseLocked(event);
        }
    }

    public void fireAfterRuleBaseLocked() {
        final AfterRuleBaseLockedEvent event = new AfterRuleBaseLockedEvent(this.ruleBase);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().afterRuleBaseLocked(event);
        }
    }

    public void fireBeforeRuleBaseUnlocked() {
        final BeforeRuleBaseUnlockedEvent event = new BeforeRuleBaseUnlockedEvent(this.ruleBase);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().beforeRuleBaseUnlocked(event);
        }
    }

    public void fireAfterRuleBaseUnlocked() {
        final AfterRuleBaseUnlockedEvent event = new AfterRuleBaseUnlockedEvent(this.ruleBase);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().afterRuleBaseUnlocked(event);
        }
    }

    public void fireBeforeRuleAdded(final Package newPkg, final Rule rule) {
        final BeforeRuleAddedEvent event = new BeforeRuleAddedEvent(this.ruleBase, newPkg, rule);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().beforeRuleAdded(event);
        }
    }

    public void fireAfterRuleAdded(final Package newPkg, final Rule rule) {
        final AfterRuleAddedEvent event = new AfterRuleAddedEvent(this.ruleBase, newPkg, rule);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().afterRuleAdded(event);
        }
    }

    public void fireBeforeRuleRemoved(final Package pkg, final Rule rule) {
        final BeforeRuleRemovedEvent event = new BeforeRuleRemovedEvent(this.ruleBase, pkg, rule);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().beforeRuleRemoved(event);
        }
    }

    public void fireAfterRuleRemoved(final Package pkg, final Rule rule) {
        final AfterRuleRemovedEvent event = new AfterRuleRemovedEvent(this.ruleBase, pkg, rule);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().afterRuleRemoved(event);
        }
    }

    public void fireBeforeFunctionRemoved(final Package pkg, final String function) {
        final BeforeFunctionRemovedEvent event = new BeforeFunctionRemovedEvent(this.ruleBase, pkg, function);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().beforeFunctionRemoved(event);
        }
    }

    public void fireAfterFunctionRemoved(final Package pkg, final String function) {
        final AfterFunctionRemovedEvent event = new AfterFunctionRemovedEvent(this.ruleBase, pkg, function);
        final Iterator<RuleBaseEventListener> iter = getEventListenersIterator();

        while (iter.hasNext()) {
            iter.next().afterFunctionRemoved(event);
        }
    }
}