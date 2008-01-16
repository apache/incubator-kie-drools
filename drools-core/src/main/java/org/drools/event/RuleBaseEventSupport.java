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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.List;

import org.drools.RuleBase;
import org.drools.rule.Rule;
import org.drools.rule.Package;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author etirelli
 */
public class RuleBaseEventSupport
    implements
    Serializable {
    /**
     * 
     */
    private static final long                 serialVersionUID = 400L;
    private final List<RuleBaseEventListener> listeners        = new CopyOnWriteArrayList<RuleBaseEventListener>();
    private transient RuleBase                ruleBase;

    public RuleBaseEventSupport(final RuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    public void addEventListener(final RuleBaseEventListener listener) {
        if ( !this.listeners.contains( listener ) ) {
            this.listeners.add( listener );
        }
    }

    public void setRuleBase(RuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    public void removeEventListener(Class cls) {
        for ( int i = 0; i < this.listeners.size(); ) {
            RuleBaseEventListener listener = this.listeners.get( i );
            if ( cls.isAssignableFrom( listener.getClass() ) ) {
                this.listeners.remove( i );
            } else {
                i++;
            }
        }
    }

    public void removeEventListener(final RuleBaseEventListener listener) {
        this.listeners.remove( listener );
    }

    public List<RuleBaseEventListener> getEventListeners() {
        return Collections.unmodifiableList( this.listeners );
    }

    public int size() {
        return this.listeners.size();
    }

    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }

    public void fireBeforePackageAdded(final Package newPkg) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforePackageAddedEvent event = new BeforePackageAddedEvent( this.ruleBase,
                                                                           newPkg );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).beforePackageAdded( event );
        }
    }

    public void fireAfterPackageAdded(final Package newPkg) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterPackageAddedEvent event = new AfterPackageAddedEvent( this.ruleBase,
                                                                         newPkg );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).afterPackageAdded( event );
        }
    }

    public void fireBeforePackageRemoved(final Package pkg) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforePackageRemovedEvent event = new BeforePackageRemovedEvent( this.ruleBase,
                                                                               pkg );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).beforePackageRemoved( event );
        }
    }

    public void fireAfterPackageRemoved(final Package pkg) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterPackageRemovedEvent event = new AfterPackageRemovedEvent( this.ruleBase,
                                                                             pkg );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).afterPackageRemoved( event );
        }
    }

    //--
    public void fireBeforeRuleBaseLocked() {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforeRuleBaseLockedEvent event = new BeforeRuleBaseLockedEvent( this.ruleBase );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).beforeRuleBaseLocked( event );
        }
    }

    public void fireAfterRuleBaseLocked() {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterRuleBaseLockedEvent event = new AfterRuleBaseLockedEvent( this.ruleBase );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).afterRuleBaseLocked( event );
        }
    }

    public void fireBeforeRuleBaseUnlocked() {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforeRuleBaseUnlockedEvent event = new BeforeRuleBaseUnlockedEvent( this.ruleBase );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).beforeRuleBaseUnlocked( event );
        }
    }

    public void fireAfterRuleBaseUnlocked() {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterRuleBaseUnlockedEvent event = new AfterRuleBaseUnlockedEvent( this.ruleBase );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).afterRuleBaseUnlocked( event );
        }
    }

    public void fireBeforeRuleAdded(final Package newPkg,
                                    final Rule rule) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforeRuleAddedEvent event = new BeforeRuleAddedEvent( this.ruleBase,
                                                                     newPkg,
                                                                     rule );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).beforeRuleAdded( event );
        }
    }

    public void fireAfterRuleAdded(final Package newPkg,
                                   final Rule rule) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterRuleAddedEvent event = new AfterRuleAddedEvent( this.ruleBase,
                                                                   newPkg,
                                                                   rule );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).afterRuleAdded( event );
        }
    }

    public void fireBeforeRuleRemoved(final Package pkg,
                                      final Rule rule) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforeRuleRemovedEvent event = new BeforeRuleRemovedEvent( this.ruleBase,
                                                                         pkg,
                                                                         rule );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).beforeRuleRemoved( event );
        }
    }

    public void fireAfterRuleRemoved(final Package pkg,
                                     final Rule rule) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterRuleRemovedEvent event = new AfterRuleRemovedEvent( this.ruleBase,
                                                                       pkg,
                                                                       rule );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).afterRuleRemoved( event );
        }
    }

    public void fireBeforeFunctionRemoved(final Package pkg,
                                          final String function) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforeFunctionRemovedEvent event = new BeforeFunctionRemovedEvent( this.ruleBase,
                                                                                 pkg,
                                                                                 function );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).beforeFunctionRemoved( event );
        }
    }

    public void fireAfterFunctionRemoved(final Package pkg,
                                         final String function) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterFunctionRemovedEvent event = new AfterFunctionRemovedEvent( this.ruleBase,
                                                                               pkg,
                                                                               function );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).afterFunctionRemoved( event );
        }
    }

}