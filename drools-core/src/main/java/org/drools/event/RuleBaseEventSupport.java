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
import java.util.List;

import org.drools.rule.Package;
import org.drools.rule.Rule;

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
    private static final long serialVersionUID = 400L;
    private final List        listeners        = Collections.synchronizedList( new ArrayList() );

    public RuleBaseEventSupport() {
    }

    public void addEventListener(final RuleBaseEventListener listener) {
        if ( !this.listeners.contains( listener ) ) {
            this.listeners.add( listener );
        }
    }

    public void removeEventListener(final RuleBaseEventListener listener) {
        this.listeners.remove( listener );
    }

    public List getEventListeners() {
        return Collections.unmodifiableList( this.listeners );
    }

    public int size() {
        return this.listeners.size();
    }

    public boolean isEmpty() {
        return this.listeners.isEmpty();
    }

    public void fireBeforePackageAdded(Package newPkg) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforePackageAddedEvent event = new BeforePackageAddedEvent( newPkg );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).beforePackageAdded( event );
        }
    }

    public void fireAfterPackageAdded(Package newPkg) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterPackageAddedEvent event = new AfterPackageAddedEvent( newPkg );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).afterPackageAdded( event );
        }
    }

    public void fireBeforePackageRemoved(Package pkg) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforePackageRemovedEvent event = new BeforePackageRemovedEvent( pkg );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).beforePackageRemoved( event );
        }
    }

    public void fireAfterPackageRemoved(Package pkg) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterPackageRemovedEvent event = new AfterPackageRemovedEvent( pkg );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).afterPackageRemoved( event );
        }
    }

    public void fireBeforeRuleAdded(Package newPkg,
                                    Rule rule) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforeRuleAddedEvent event = new BeforeRuleAddedEvent( newPkg,
                                                                     rule );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).beforeRuleAdded( event );
        }
    }

    public void fireAfterRuleAdded(Package newPkg,
                                   Rule rule) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterRuleAddedEvent event = new AfterRuleAddedEvent( newPkg,
                                                                   rule );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).afterRuleAdded( event );
        }
    }

    public void fireBeforeRuleRemoved(Package pkg,
                                      Rule rule) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final BeforeRuleRemovedEvent event = new BeforeRuleRemovedEvent( pkg,
                                                                         rule );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).beforeRuleRemoved( event );
        }
    }

    public void fireAfterRuleRemoved(Package pkg,
                                     Rule rule) {
        if ( this.listeners.isEmpty() ) {
            return;
        }

        final AfterRuleRemovedEvent event = new AfterRuleRemovedEvent( pkg,
                                                                       rule );

        for ( int i = 0, size = this.listeners.size(); i < size; i++ ) {
            ((RuleBaseEventListener) this.listeners.get( i )).afterRuleRemoved( event );
        }
    }

}