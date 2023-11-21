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
package org.drools.compiler.kie.builder.impl;

import java.util.List;

import org.drools.base.base.ClassObjectType;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.impl.KnowledgePackageImpl;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.rule.DialectRuntimeData;
import org.drools.compiler.builder.InternalKnowledgeBuilder;
import org.drools.compiler.compiler.PackageBuilderErrors;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.impl.KieBaseUpdate;
import org.drools.core.reteoo.EntryPointNode;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.builder.ChangeType;
import org.kie.internal.builder.CompositeKnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.ResourceChange;
import org.kie.internal.builder.ResourceChangeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KieBaseUpdaterImpl implements KieBaseUpdater {

    private static final Logger log = LoggerFactory.getLogger( KieBaseUpdaterImpl.class );

    protected final KieBaseUpdaterImplContext ctx;

    public KieBaseUpdaterImpl(KieBaseUpdaterImplContext ctx ) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        InternalKnowledgeBuilder kbuilder = ctx.kbuilder;

        CompositeKnowledgeBuilder ckbuilder = kbuilder.batch();

        boolean shouldRebuild = applyResourceChanges(kbuilder, ckbuilder);
        removeResources(kbuilder);

        // remove all ObjectTypeNodes for the modified classes
        if (ctx.modifyingUsedClass) {
            for (Class<?> cls : ctx.modifiedClasses ) {
                clearInstancesOfModifiedClass(cls);
            }
            for (InternalKnowledgePackage pkg : ctx.kBase.getPackagesMap().values()) {
                DialectRuntimeData mvel = pkg.getDialectRuntimeRegistry().getDialectData("mvel");
                if(mvel != null) {
                    mvel.resetParserConfiguration();
                }
            }
        }

        if ( shouldRebuild ) {
            // readd unchanged dsl files to the kbuilder
            for (String dslFile : ctx.unchangedResources) {
                if (isFileInKBase(ctx.newKM, ctx.newKieBaseModel, dslFile)) {
                    ctx.newKM.addResourceToCompiler(ckbuilder, ctx.newKieBaseModel, dslFile);
                }
            }

            KieBaseUpdate kieBaseUpdate = createKieBaseUpdate();
            ctx.kBase.beforeIncrementalUpdate( kieBaseUpdate );
            rebuildAll(kbuilder, ckbuilder);
            ctx.kBase.afterIncrementalUpdate( kieBaseUpdate );
        }

        ctx.kBase.setResolvedReleaseId(ctx.newReleaseId);

        for ( InternalWorkingMemory wm : ctx.kBase.getWorkingMemories() ) {
            wm.notifyWaitOnRest();
        }
    }

    private KieBaseUpdate createKieBaseUpdate() {
        KieBaseUpdate kieBaseUpdate = new KieBaseUpdate();

        for (ResourceChangeSet changeSet : ctx.cs.getChanges().values()) {
            if (!isPackageInKieBase( ctx.newKieBaseModel, changeSet.getPackageName() )) {
                continue;
            }

            InternalKnowledgePackage currentPkg = ctx.currentKM.getPackage( changeSet.getPackageName() );
            InternalKnowledgePackage newPkg = ctx.newKM.getPackage( changeSet.getPackageName() );

            for (ResourceChange change : changeSet.getChanges()) {
                if (change.getType() == ResourceChange.Type.RULE) {
                    switch (change.getChangeType()) {
                        case ADDED:
                            kieBaseUpdate.registerRuleToBeAdded(newPkg.getRule( change.getName() ));
                            break;
                        case REMOVED:
                            kieBaseUpdate.registerRuleToBeRemoved(currentPkg.getRule( change.getName() ));
                            break;
                        case UPDATED:
                            kieBaseUpdate.registerRuleToBeAdded(newPkg.getRule( change.getName() ));
                            kieBaseUpdate.registerRuleToBeRemoved(currentPkg.getRule( change.getName() ));
                            break;
                    }
                }
            }
        }

        return kieBaseUpdate;
    }

    protected void removeResources(InternalKnowledgeBuilder kBuilder) {
        // remove resources first
        for ( ResourceChangeSet rcs : ctx.cs.getChanges().values()) {
            if ( rcs.getChangeType() == ChangeType.REMOVED ) {
                String resourceName = rcs.getResourceName();
                if ( !resourceName.endsWith( ".properties" ) && isFileInKBase(ctx.currentKM, ctx.currentKieBaseModel, resourceName) ) {
                    kBuilder.removeObjectsGeneratedFromResource( ctx.currentKM.getResource( resourceName ) );
                }
            }
        }
    }

    private void rebuildAll(InternalKnowledgeBuilder kbuilder, CompositeKnowledgeBuilder ckbuilder) {
        ckbuilder.build();

        PackageBuilderErrors errors = (PackageBuilderErrors) kbuilder.getErrors();
        if ( !errors.isEmpty() ) {
            for ( KnowledgeBuilderError error : errors.getErrors() ) {
                ctx.results.addMessage(error).setKieBaseName( ctx.newKieBaseModel.getName() );
            }
            log.error("Unable to update KieBase: " + ctx.newKieBaseModel.getName() + " to release " + ctx.newReleaseId + "\n" + errors.toString());
        }

        if (ctx.modifyingUsedClass) {
            kbuilder.rewireAllClassObjectTypes();
        }
    }

    private boolean applyResourceChanges( InternalKnowledgeBuilder kbuilder, CompositeKnowledgeBuilder ckbuilder) {
        boolean shouldRebuild = ctx.modifyingUsedClass;
        if (ctx.modifyingUsedClass) {
            // invalidate accessors for old class
            invalidateAccessorForOldClass();
            // there are modified classes used by this kbase, so it has to be completely updated
            updateAllResources(kbuilder, ckbuilder);
        } else {
            // there are no modified classes used by this kbase, so update it incrementally
            shouldRebuild = updateResourcesIncrementally(kbuilder, ckbuilder) > 0;
        }
        return shouldRebuild;
    }

    protected void invalidateAccessorForOldClass() {
        for (Class<?> cls : ctx.modifiedClasses) {
            InternalKnowledgePackage kpackage = ( (InternalKnowledgePackage) ctx.kBase.getKiePackage(cls.getPackage().getName() ) );
            if (kpackage != null) {
                kpackage.removeClass( cls );
            }
        }
    }

    private int updateResourcesIncrementally(InternalKnowledgeBuilder kbuilder, CompositeKnowledgeBuilder ckbuilder) {
        int fileCount = ctx.modifiedClasses.size();
        for ( ResourceChangeSet rcs : ctx.cs.getChanges().values()) {
            fileCount += updateResource(kbuilder, ckbuilder, rcs);
        }
        return fileCount;
    }

    protected int updateResource(InternalKnowledgeBuilder kbuilder, CompositeKnowledgeBuilder ckbuilder, ResourceChangeSet rcs) {
        int fileCount = 0;
        if ( rcs.getChangeType() != ChangeType.REMOVED ) {
            String resourceName = rcs.getResourceName();
            if ( !resourceName.endsWith( ".properties" ) && isFileInKBase(ctx.newKM, ctx.newKieBaseModel, resourceName) ) {
                List<ResourceChange> changes = rcs.getChanges();
                if ( ! changes.isEmpty() ) {
                    // we need to deal with individual parts of the resource
                    fileCount += AbstractKieModule.updateResource(ckbuilder, ctx.newKM, resourceName, rcs) ? 1 : 0;
                } else {
                    // the whole resource has to handled
                    if( rcs.getChangeType() == ChangeType.UPDATED ) {
                        Resource resource = ctx.currentKM.getResource(resourceName);
                        kbuilder.removeObjectsGeneratedFromResource(resource);
                    }
                    fileCount += ctx.newKM.addResourceToCompiler(ckbuilder, ctx.newKieBaseModel, resourceName, rcs) ? 1 : 0;
                }
            }
        }

        for ( ResourceChangeSet.RuleLoadOrder loadOrder : rcs.getLoadOrder() ) {
            KnowledgePackageImpl pkg = (KnowledgePackageImpl)ctx.kBase.getKiePackage(loadOrder.getPkgName() );
            if( pkg != null ) {
                RuleImpl rule = pkg.getRule(loadOrder.getRuleName() );
                if ( rule != null ) {
                    // rule can be null, if it didn't exist before
                    rule.setLoadOrder( loadOrder.getLoadOrder() );
                }
            }
        }
        return fileCount;
    }

    private boolean isFileInKBase(InternalKieModule kieModule, KieBaseModel kieBase, String fileName) {
        if (kieModule.isFileInKBase(kieBase, fileName)) {
            return true;
        }
        for (String include : ctx.kProject.getTransitiveIncludes(kieBase)) {
            InternalKieModule includeModule = ctx.kProject.getKieModuleForKBase(include);
            if (includeModule != null && includeModule.isFileInKBase(ctx.kProject.getKieBaseModel(include), fileName)) {
                return true;
            }
        }
        return false;
    }

    protected void updateAllResources(InternalKnowledgeBuilder kbuilder, CompositeKnowledgeBuilder ckbuilder) {
        for (String resourceName : ctx.currentKM.getFileNames()) {
            if ( !resourceName.endsWith( ".properties" ) && isFileInKBase(ctx.currentKM, ctx.newKieBaseModel, resourceName) ) {
                Resource resource = ctx.currentKM.getResource(resourceName);
                kbuilder.removeObjectsGeneratedFromResource(resource);
            }
        }
        for (String resourceName : ctx.newKM.getFileNames()) {
            if ( !resourceName.endsWith( ".properties" ) && isFileInKBase(ctx.newKM, ctx.newKieBaseModel, resourceName) ) {
                ctx.newKM.addResourceToCompiler(ckbuilder, ctx.newKieBaseModel, resourceName);
            }
        }
    }

    protected void clearInstancesOfModifiedClass( Class<?> cls ) {
        // remove all ObjectTypeNodes for the modified classes
        ClassObjectType objectType = new ClassObjectType(cls );
        for ( EntryPointNode epn : ctx.kBase.getRete().getEntryPointNodes().values() ) {
            epn.removeObjectType( objectType );
        }

        // remove all instance of the old class from the object stores
        for (InternalWorkingMemory wm : ctx.kBase.getWorkingMemories()) {
            for (EntryPoint ep : wm.getEntryPoints()) {
                InternalWorkingMemoryEntryPoint wmEp = (InternalWorkingMemoryEntryPoint) wm.getEntryPoint(ep.getEntryPointId() );
                if ( wmEp.getObjectStore().clearClassStore( cls ) ) {
                    log.warn( "Class " + cls.getName() + " has been modified and therfore its old instances will no longer match" );
                }
            }
        }
    }

    protected static boolean isPackageInKieBase( KieBaseModel kieBaseModel, String pkgName ) {
        return pkgName != null && ( kieBaseModel.getPackages().isEmpty() || KieBuilderImpl.isPackageInKieBase( kieBaseModel, pkgName ) );
    }
}

