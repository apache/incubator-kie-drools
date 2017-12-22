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
import org.drools.compiler.kie.builder.impl.KieBaseUpdater;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.modelcompiler.CanonicalKiePackages;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResourceChangeSet;

public class CanonicalKieBaseUpdater extends KieBaseUpdater {

    public CanonicalKieBaseUpdater( KieBaseUpdateContext ctx ) {
        super(ctx);
    }

    @Override
    public void run() {
        CanonicalKieModule oldKM = ( CanonicalKieModule ) ctx.currentKM;
        CanonicalKieModule newKM = ( CanonicalKieModule ) ctx.newKM;

        List<RuleImpl> rulesToBeRemoved;
        List<RuleImpl> rulesToBeAdded;

        if (ctx.modifyingUsedClass) {
            // remove all ObjectTypeNodes for the modified classes
            for (Class<?> cls : ctx.modifiedClasses ) {
                clearInstancesOfModifiedClass( cls );
            }

            rulesToBeRemoved = getAllRulesInKieBase( oldKM, ctx.currentKieBaseModel );
            rulesToBeAdded = getAllRulesInKieBase( newKM, ctx.newKieBaseModel );

        } else {

            newKM.setModuleClassLoader( (( CanonicalKieModule ) ctx.currentKM).getModuleClassLoader() );
            CanonicalKiePackages newPkgs = newKM.getKiePackages( ctx.newKieBaseModel );

            rulesToBeRemoved = new ArrayList<>();
            rulesToBeAdded = new ArrayList<>();

            for (ResourceChangeSet changeSet : ctx.cs.getChanges().values()) {
                if (!isPackageInKieBase( ctx.newKieBaseModel, changeSet.getResourceName() )) {
                    continue;
                }
                InternalKnowledgePackage oldKpkg = ctx.kBase.getPackage( changeSet.getResourceName() );
                InternalKnowledgePackage kpkg = ( InternalKnowledgePackage ) newPkgs.getKiePackage( changeSet.getResourceName() );

                for (ResourceChange change : changeSet.getChanges()) {
                    String changedItemName = change.getName();
                    if (change.getChangeType() == ChangeType.UPDATED || change.getChangeType() == ChangeType.REMOVED) {
                        if (change.getType() == ResourceChange.Type.GLOBAL) {
                            ctx.kBase.removeGlobal( changedItemName );
                        } else {
                            rulesToBeRemoved.add( oldKpkg.getRule( changedItemName ) );
                        }
                    }
                    if (change.getChangeType() == ChangeType.UPDATED || change.getChangeType() == ChangeType.ADDED) {
                        if (change.getType() == ResourceChange.Type.GLOBAL) {
                            try {
                                ctx.kBase.addGlobal( changedItemName, kpkg.getTypeResolver().resolveType( kpkg.getGlobals().get(changedItemName) ) );
                            } catch (ClassNotFoundException e) {
                                throw new RuntimeException( e );
                            }
                        } else {
                            rulesToBeAdded.add( kpkg.getRule( changedItemName ) );
                        }
                    }
                }
            }
        }

        ctx.kBase.removeRules( rulesToBeRemoved );
        ctx.kBase.addRules( rulesToBeAdded );
    }

    private List<RuleImpl> getAllRulesInKieBase( CanonicalKieModule kieModule, KieBaseModelImpl model ) {
        List<RuleImpl> rules = new ArrayList<>();
        for (KiePackage oldPkg : kieModule.getKiePackages( model ).getKiePackages()) {
            if (!isPackageInKieBase( ctx.currentKieBaseModel, oldPkg.getName() )) {
                continue;
            }
            for (Rule oldRule : oldPkg.getRules()) {
                rules.add( (( RuleImpl ) oldRule) );
            }
        }
        return rules;
    }

    private static boolean isPackageInKieBase( KieBaseModel kieBaseModel, String pkgName ) {
        return kieBaseModel.getPackages().isEmpty() || KieBuilderImpl.isPackageInKieBase( kieBaseModel, pkgName );
    }
}
