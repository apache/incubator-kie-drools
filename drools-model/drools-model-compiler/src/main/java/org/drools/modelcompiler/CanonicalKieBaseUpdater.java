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
package org.drools.modelcompiler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.Function;
import org.drools.base.rule.TypeDeclaration;
import org.drools.compiler.builder.InternalKnowledgeBuilder;
import org.drools.compiler.builder.impl.CompositeKnowledgeBuilderImpl;
import org.drools.compiler.kie.builder.impl.KieBaseUpdaterImpl;
import org.drools.compiler.kie.builder.impl.KieBaseUpdaterImplContext;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.KieBaseUpdate;
import org.drools.core.reteoo.CoreComponentFactory;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResourceChangeSet;

public class CanonicalKieBaseUpdater extends KieBaseUpdaterImpl {

    public CanonicalKieBaseUpdater( KieBaseUpdaterImplContext ctx ) {
        super(ctx);
    }

    @Override
    public void run() {
        CanonicalKieModule oldKM = ( CanonicalKieModule ) ctx.currentKM;
        CanonicalKieModule newKM = ( CanonicalKieModule ) ctx.newKM;

        Map<String, AtomicInteger> globalsCounter = new HashMap<>();

        // To keep compatible the classes generated from declared types the new kmodule has to be loaded with the classloader of the old one
        newKM.setIncrementalUpdate( true );
        CanonicalKiePackages newPkgs = newKM.getKiePackages( ctx.newKieBaseModel );
        InternalKnowledgeBuilder pkgbuilder = ctx.kbuilder;
        CompositeKnowledgeBuilder ckbuilder = pkgbuilder.batch();
        newKM.setIncrementalUpdate( false );

        removeResources(pkgbuilder);

        List<RuleImpl> rulesToBeRemoved;
        List<RuleImpl> rulesToBeAdded;

        if (ctx.modifyingUsedClass) {
            // remove all ObjectTypeNodes for the modified classes
            for (Class<?> cls : ctx.modifiedClasses ) {
                clearInstancesOfModifiedClass(cls);
            }

            for (InternalKnowledgePackage kpkg : ctx.kBase.getPackagesMap().values()) {
                List<TypeDeclaration> types = new ArrayList<>( kpkg.getTypeDeclarations().values() );
                for (TypeDeclaration type : types) {
                    kpkg.removeTypeDeclaration( type.getTypeName() );
                    kpkg.addTypeDeclaration( (( InternalKnowledgePackage ) newPkgs.getKiePackage( kpkg.getName() )).getTypeDeclaration( type.getTypeName() ) );
                }
            }

            rulesToBeRemoved = getAllRulesInKieBase( oldKM, ctx.currentKieBaseModel );
            rulesToBeAdded = getAllRulesInKieBase( newKM, ctx.newKieBaseModel );

        } else {

            ctx.kBase.processAllTypesDeclaration( newPkgs.getKiePackages() );

            rulesToBeRemoved = new ArrayList<>();
            rulesToBeAdded = new ArrayList<>();

            for (ResourceChangeSet changeSet : ctx.cs.getChanges().values()) {
                if (!isPackageInKieBase( ctx.newKieBaseModel, changeSet.getResourceName() )) {
                    continue;
                }

                InternalKnowledgePackage kpkg = ( InternalKnowledgePackage ) newPkgs.getKiePackage( changeSet.getResourceName() );
                InternalKnowledgePackage oldKpkg = ctx.kBase.getPackage( changeSet.getResourceName() );
                if (oldKpkg == null) {
                    try {
                        oldKpkg = (InternalKnowledgePackage) ctx.kBase.addPackage( CoreComponentFactory.get().createKnowledgePackage( changeSet.getResourceName() ) ).get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException( e );
                    }
                }

                if (kpkg != null) {
                    for (Rule newRule : kpkg.getRules()) {
                        RuleImpl rule = oldKpkg.getRule( newRule.getName() );
                        if ( rule != null ) {
                            rule.setLoadOrder( (( RuleImpl ) newRule).getLoadOrder() );
                        }
                    }
                }

                this.updateResource(pkgbuilder, ckbuilder, changeSet);

                for (ResourceChange change : changeSet.getChanges()) {
                    String changedItemName = change.getName();
                    if (change.getChangeType() == ChangeType.UPDATED || change.getChangeType() == ChangeType.REMOVED) {
                        switch (change.getType()) {
                            case GLOBAL:
                                oldKpkg.removeGlobal( changedItemName );
                                AtomicInteger globalCounter = globalsCounter.get(changedItemName);
                                if (globalCounter == null || globalCounter.decrementAndGet() <= 0) {
                                    ctx.kBase.removeGlobal( changedItemName );
                                }
                                break;
                            case RULE:
                                RuleImpl removedRule = oldKpkg.getRule( changedItemName );
                                if(removedRule != null) {
                                    rulesToBeRemoved.add(removedRule);
                                    oldKpkg.removeRule(removedRule);
                                }
                                break;
                            case DECLARATION:
                                oldKpkg.removeTypeDeclaration( changedItemName );
                                break;
                            case FUNCTION:
                                oldKpkg.removeFunction(changedItemName);
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported change type: " + change.getType() + "!");
                        }
                    }
                    if (kpkg != null && (change.getChangeType() == ChangeType.UPDATED || change.getChangeType() == ChangeType.ADDED)) {
                        switch (change.getType()) {
                            case GLOBAL:
                                globalsCounter.computeIfAbsent( changedItemName, name -> ctx.kBase.getGlobals().get(name) == null ? new AtomicInteger( 1 ) : new AtomicInteger( 0 ) ).incrementAndGet();
                                Type globalClass = kpkg.getGlobals().get(changedItemName);
                                oldKpkg.addGlobal( changedItemName, globalClass );
                                ctx.kBase.addGlobal( changedItemName, globalClass );
                                break;
                            case RULE:
                                RuleImpl addedRule = kpkg.getRule( changedItemName );
                                rulesToBeAdded.add( addedRule );
                                oldKpkg.addRule( addedRule );
                                break;
                            case DECLARATION:
                                TypeDeclaration addedType = kpkg.getTypeDeclaration( changedItemName );
                                oldKpkg.addTypeDeclaration( addedType );
                                break;
                            case FUNCTION:
                                Function addedFunction = kpkg.getFunctions().get(changedItemName);
                                oldKpkg.addFunction(addedFunction);
                                break;
                            default:
                                throw new IllegalArgumentException("Unsupported change type: " + change.getType() + "!");
                        }
                    }
                }
            }
        }

        if (ctx.modifyingUsedClass) {
            invalidateAccessorForOldClass();
            updateAllResources(pkgbuilder, ckbuilder);
        }

        ((CompositeKnowledgeBuilderImpl)ckbuilder).build(false);

        KieBaseUpdate kieBaseUpdate = new KieBaseUpdate(rulesToBeRemoved, rulesToBeAdded);
        ctx.kBase.beforeIncrementalUpdate( kieBaseUpdate );
        ctx.kBase.removeRules( rulesToBeRemoved );
        ctx.kBase.addRules( rulesToBeAdded );
        ctx.kBase.afterIncrementalUpdate( kieBaseUpdate );

        for ( InternalWorkingMemory wm : ctx.kBase.getWorkingMemories() ) {
            wm.notifyWaitOnRest();
        }
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
}
