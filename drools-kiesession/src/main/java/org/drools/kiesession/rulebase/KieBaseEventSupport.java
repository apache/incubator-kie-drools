/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.kiesession.rulebase;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.event.AbstractEventSupport;
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
import org.kie.api.KieBase;
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

public class KieBaseEventSupport extends AbstractEventSupport<KieBaseEventListener> {
    private final KieBase kBase;

    public KieBaseEventSupport(KieBase kBase) {
        this.kBase = kBase;
    }

    public void fireBeforePackageAdded(final InternalKnowledgePackage newPkg) {
        if ( hasListeners() ) {
            BeforeKiePackageAddedEvent event = new BeforeKiePackageAddedEventImpl(this.kBase, newPkg);
            notifyAllListeners( event, ( l, e ) -> l.beforeKiePackageAdded( e ) );
        }
    }

    public void fireAfterPackageAdded(final InternalKnowledgePackage newPkg) {
        if ( hasListeners() ) {
            AfterKiePackageAddedEvent event = new AfterKiePackageAddedEventImpl(this.kBase, newPkg);
            notifyAllListeners( event, ( l, e ) -> l.afterKiePackageAdded( e ) );
        }
    }

    public void fireBeforePackageRemoved(InternalKnowledgePackage pkg) {
        if ( hasListeners() ) {
            BeforeKiePackageRemovedEvent event = new BeforeKiePackageRemovedEventImpl(this.kBase, pkg);
            notifyAllListeners( event, ( l, e ) -> l.beforeKiePackageRemoved( e ) );
        }
    }

    public void fireAfterPackageRemoved(InternalKnowledgePackage pkg) {
        if ( hasListeners() ) {
            AfterKiePackageRemovedEvent event = new AfterKiePackageRemovedEventImpl(this.kBase, pkg);
            notifyAllListeners( event, ( l, e ) -> l.afterKiePackageRemoved( e ) );
        }
    }

    public void fireBeforeRuleBaseLocked() {
        if ( hasListeners() ) {
            BeforeKieBaseLockedEvent event = new BeforeKnowledgeBaseLockedEventImpl(this.kBase);
            notifyAllListeners( event, ( l, e ) -> l.beforeKieBaseLocked( e ) );
        }
    }

    public void fireAfterRuleBaseLocked() {
        if ( hasListeners() ) {
            AfterKieBaseLockedEvent event = new AfterKnowledgeBaseLockedEventImpl(this.kBase);
            notifyAllListeners( event, ( l, e ) -> l.afterKieBaseLocked( e ) );
        }
    }

    public void fireBeforeRuleBaseUnlocked() {
        if ( hasListeners() ) {
            BeforeKieBaseUnlockedEvent event = new BeforeKnowledgeBaseUnlockedEventImpl(this.kBase);
            notifyAllListeners( event, ( l, e ) -> l.beforeKieBaseUnlocked( e ) );
        }
    }

    public void fireAfterRuleBaseUnlocked() {
        if ( hasListeners() ) {
            AfterKieBaseUnlockedEvent event = new AfterKnowledgeBaseUnlockedEventImpl(this.kBase);
            notifyAllListeners( event, ( l, e ) -> l.afterKieBaseUnlocked( e ) );
        }
    }

    public void fireBeforeRuleAdded(RuleImpl rule) {
        if ( hasListeners() ) {
            BeforeRuleAddedEvent event = new BeforeRuleAddedEventImpl(this.kBase, rule);
            notifyAllListeners( event, ( l, e ) -> l.beforeRuleAdded( e ) );
        }
    }

    public void fireAfterRuleAdded(final RuleImpl rule) {
        if ( hasListeners() ) {
            AfterRuleAddedEvent event = new AfterRuleAddedEventImpl(this.kBase, rule);
            notifyAllListeners( event, ( l, e ) -> l.afterRuleAdded( e ) );
        }
    }

    public void fireBeforeRuleRemoved(final RuleImpl rule) {
        if ( hasListeners() ) {
            final BeforeRuleRemovedEvent event = new BeforeRuleRemovedEventImpl(this.kBase, rule);
            notifyAllListeners( event, ( l, e ) -> l.beforeRuleRemoved( e ) );
        }
    }

    public void fireAfterRuleRemoved(final RuleImpl rule) {
        if ( hasListeners() ) {
            AfterRuleRemovedEvent event = new AfterRuleRemovedEventImpl(this.kBase, rule);
            notifyAllListeners( event, ( l, e ) -> l.afterRuleRemoved( e ) );
        }
    }

    public void fireBeforeFunctionRemoved(final InternalKnowledgePackage pkg, final String function) {
        if ( hasListeners() ) {
            BeforeFunctionRemovedEvent event = new BeforeFunctionRemovedEventImpl(this.kBase, function);
            notifyAllListeners( event, ( l, e ) -> l.beforeFunctionRemoved( e ) );
        }
    }

    public void fireAfterFunctionRemoved(final InternalKnowledgePackage pkg, final String function) {
        if ( hasListeners() ) {
            AfterFunctionRemovedEvent event = new AfterFunctionRemovedEventImpl(this.kBase, function);
            notifyAllListeners( event, ( l, e ) -> l.afterFunctionRemoved( e ) );
        }
    }
    
    public void fireBeforeProcessAdded(final Process process) {
        if ( hasListeners() ) {
            BeforeProcessAddedEvent event = new BeforeProcessAddedEventImpl(this.kBase, process);
            notifyAllListeners( event, ( l, e ) -> l.beforeProcessAdded( e ) );
        }
    }

    public void fireAfterProcessAdded(final Process process) {
        if ( hasListeners() ) {
            AfterProcessAddedEvent event = new AfterProcessAddedEventImpl(this.kBase, process);
            notifyAllListeners( event, ( l, e ) -> l.afterProcessAdded( e ) );
        }
    }

    public void fireBeforeProcessRemoved(final Process process) {
        if ( hasListeners() ) {
            BeforeProcessRemovedEvent event = new BeforeProcessRemovedEventImpl(this.kBase, process);
            notifyAllListeners( event, ( l, e ) -> l.beforeProcessRemoved( e ) );
        }
    }

    public void fireAfterProcessRemoved(final Process process) {
        if ( hasListeners() ) {
            AfterProcessRemovedEvent event = new AfterProcessRemovedEventImpl(this.kBase, process);
            notifyAllListeners( event, ( l, e ) -> l.afterProcessRemoved( e ) );
        }
    }
}
