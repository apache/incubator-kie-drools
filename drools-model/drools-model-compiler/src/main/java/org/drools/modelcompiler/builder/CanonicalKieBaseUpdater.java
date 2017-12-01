/*
 * Copyright 2005 JBoss Inc
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

package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.kie.builder.impl.KieBaseUpdateContext;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.modelcompiler.CanonicalKiePackages;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResourceChangeSet;

public class CanonicalKieBaseUpdater implements Runnable {

    private final KieBaseUpdateContext ctx;

    public CanonicalKieBaseUpdater( KieBaseUpdateContext ctx ) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        CanonicalKieModule newKM = ( CanonicalKieModule ) ctx.newKM;
        newKM.setModuleClassLoader( (( CanonicalKieModule ) ctx.currentKM).getModuleClassLoader() );
        CanonicalKiePackages newPkgs = newKM.getKiePackages( ctx.newKieBaseModel );

        List<RuleImpl> rulesToBeRemoved = new ArrayList<>();
        List<RuleImpl> rulesToBeAdded = new ArrayList<>();

        for (ResourceChangeSet changeSet : ctx.cs.getChanges().values()) {
            InternalKnowledgePackage oldKpkg = ctx.kBase.getPackage( changeSet.getResourceName() );
            InternalKnowledgePackage kpkg = ( InternalKnowledgePackage ) newPkgs.getKiePackage( changeSet.getResourceName() );

            for (ResourceChange change : changeSet.getChanges()) {
                String changedRuleName = change.getName();
                if (change.getChangeType() == ChangeType.UPDATED || change.getChangeType() == ChangeType.REMOVED) {
                    rulesToBeRemoved.add( oldKpkg.getRule( changedRuleName ) );
                }
                if (change.getChangeType() == ChangeType.UPDATED || change.getChangeType() == ChangeType.ADDED) {
                    rulesToBeAdded.add( kpkg.getRule( changedRuleName ) );
                }
            }
        }


        ctx.kBase.removeRules( rulesToBeRemoved );
        ctx.kBase.addRules( rulesToBeAdded );
    }
}
