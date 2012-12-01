/*
 * Copyright 2012 JBoss Inc
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
package org.drools.kproject.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.drools.RuleBase;
import org.drools.agent.impl.BinaryResourceDiffProducerImpl;
import org.drools.agent.impl.CompositeClassLoader;
import org.drools.agent.impl.KnowledgeAgentEventListener;
import org.drools.agent.impl.KnowledgeAgentImpl;
import org.drools.agent.impl.KnowledgeBase;
import org.drools.agent.impl.KnowledgeBuilder;
import org.drools.agent.impl.KnowledgeDefinition;
import org.drools.agent.impl.KnowledgePackage;
import org.drools.agent.impl.KnowledgeSessionConfiguration;
import org.drools.agent.impl.ResourceDiffProducer;
import org.drools.agent.impl.ResourceDiffResult;
import org.drools.agent.impl.ResourcedObject;
import org.drools.agent.impl.StatefulKnowledgeSession;
import org.drools.agent.impl.StatelessKnowledgeSession;
import org.drools.agent.impl.SystemEventListener;
import org.drools.agent.impl.KnowledgeAgentImpl.ChangeSetNotificationDetector;
import org.drools.agent.impl.KnowledgeAgentImpl.ChangeSetState;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalRuleBase;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatelessKnowledgeSessionImpl;
import org.drools.io.impl.ChangeSetImpl;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.ReaderResource;
import org.drools.io.internal.InternalResource;
import org.drools.rule.Function;
import org.drools.rule.JavaDialectRuntimeData;
import org.drools.rule.Package;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.kie.ChangeSet;
import org.kie.builder.KieJar;
import org.kie.io.Resource;
import org.kie.io.ResourceFactory;

/**
 *
 */
public class ChangeSetBuilder {
    
    private static final String URL_PREFIX = "jar:file://!/";

    public static ChangeSet build( KieJar original, KieJar currentJar ) {
        ChangeSetImpl result = new ChangeSetImpl();
        
        List<String> originalFiles = original.getFiles();
        List<String> currentFiles = currentJar.getFiles();
        
        ArrayList<String> removedFiles = new ArrayList<String>( originalFiles );
        removedFiles.removeAll( currentFiles );
        if( ! removedFiles.isEmpty() ) {
            List<Resource> removed = new ArrayList<Resource>( removedFiles.size() );
            result.setResourcesRemoved( removed );
            for( String file : removedFiles ) {
                // there should be a way to get the JAR name/url to produce a proper URL for the file in it
                removed.add( ResourceFactory.newUrlResource( URL_PREFIX+file ) );
            }
        }

        List<Resource> modified = new ArrayList<Resource>();
        List<Resource> added = new ArrayList<Resource>();
        for( String file : currentFiles ) {
            if( originalFiles.contains( file ) ) {
                // check for modification
                byte[] ob = original.getBytes( file );
                byte[] cb = currentJar.getBytes( file );
                if( ! Arrays.equals( ob, cb ) ) {
                    // parse the file to figure out the difference
                    modified.add( ResourceFactory.newUrlResource( URL_PREFIX+file ) );
                }
            } else {
                // file was added
                added.add( ResourceFactory.newUrlResource( URL_PREFIX+file ) );
            }
        }
        if( !modified.isEmpty() ) {
            result.setResourcesModified( modified );
        }
        
        if( !added.isEmpty() ) {
            result.setResourcesAdded( added );
        }
        
        return result;
    }
    
    /**
     * Iterates over the pkg's definitions and maps it to resource.
     * If autoDiscoverResource is set to true, the resource used in the mapping
     * will be taken from each definition. In this case, the parameter <code>
     * resource</code> is not used and should be null. This is useful for packages
     * that contains definitions from more than one resource.
     * If <code>autoDiscoverResource</code> is false and <code>resource</code>
     * is null, this method does nothig.
     * @param pkg the definitions present in this package will be iterated and
     * mapped to <code>resource</code>
     * @param resource The resouce where the pkg's definition will be mapped. If
     * <code>autoDiscoverResource</code> is true, this parameter should be null;
     * it will not be used.
     * @param autoDiscoverResource if set to true, the resource to do the mapping
     * will be taken from each definition. If that is the case, the parameter
     * <code>resource</code> is not used.
     */
    private void buildResourceMapping(Package pkg,
                                      Resource resource,
                                      boolean autoDiscoverResource) {

        synchronized ( this.registeredResources ) {
            if ( !autoDiscoverResource && resource == null ) {
                this.listener.warning( "KnowledgeAgent: Impossible to map to a null resource! Use autoDiscoverResource = true " );
                return;
            }

            if ( autoDiscoverResource && resource != null ) {
                this.listener.warning( "KnowledgeAgent: building resource map with resource set and autoDiscoverResource=true. Resource value wil be overwritten!" );
            }

            for ( Rule rule : pkg.getRules() ) {
                if ( autoDiscoverResource ) {
                    resource = rule.getResource();
                }

                if ( resource == null ) {
                    this.listener.debug( "KnowledgeAgent no resource mapped for rule="
                                         + rule );
                }


                if ( isNewDefinition( resource, rule ) ) {
                    this.addDefinitionMapping( resource,
                                               rule,
                                               true );
                }
            }

            for ( Process process : pkg.getRuleFlows().values() ) {
                if ( resource == null ) {
                    this.listener.debug( "KnowledgeAgent no resource mapped for process="
                                         + process );
                }
                if ( autoDiscoverResource ) {
                    resource = ((ResourcedObject) process).getResource();
                }

                if ( isNewDefinition( resource, process ) ) {
                    this.addDefinitionMapping( resource,
                                               process,
                                               true );
                }
            }

            for ( TypeDeclaration typeDeclaration : pkg.getTypeDeclarations().values() ) {
                if ( resource == null ) {
                    this.listener.debug( "KnowledgeAgent no resource mapped for type="
                                         + typeDeclaration );
                }
                if ( autoDiscoverResource ) {
                    resource = typeDeclaration.getResource();
                }

                if ( isNewDefinition( resource, typeDeclaration ) ) {
                    this.addDefinitionMapping( resource,
                                               typeDeclaration,
                                               true );
                }
            }

            for ( Function function : pkg.getFunctions().values() ) {
                if ( resource != null && !((InternalResource) resource).hasURL() ) {
                    this.listener.debug( "KnowledgeAgent no resource mapped for function="
                                         + function );
                }
                if ( autoDiscoverResource ) {
                    resource = function.getResource();
                }
                if ( isNewDefinition( resource, function ) ) {
                    this.addDefinitionMapping( resource,
                                               function,
                                               true );
                }
            }
        }
    }

    private boolean isNewDefinition(Resource resource,
                                    KnowledgeDefinition def) {
        return !this.registeredResources.isResourceMapped(resource) || !this.registeredResources.getDefinitions(resource).contains(def);
    }

    /**
     * This indexes the rules, flows, type declarations, etc against their
     * respective URLs if they have any, to allow more fine grained removal and
     * not just removing of an entire package
     */
    public void autoBuildResourceMapping() {
        this.listener.debug( "KnowledgeAgent building resource map" );
        synchronized ( this.registeredResources ) {
            RuleBase rbase = ((KnowledgeBaseImpl) this.kbase).ruleBase;

            for ( Package pkg : rbase.getPackages() ) {
                this.buildResourceMapping( pkg,
                                           null,
                                           true );
            }
        }
    }

    public KnowledgeBase getKnowledgeBase() {
        synchronized ( this.registeredResources ) {
            return this.kbase;
        }
    }

    public StatelessKnowledgeSession newStatelessKnowledgeSession() {
        return new StatelessKnowledgeSessionImpl( null,
                                                  this,
                                                  null );
    }

    public StatelessKnowledgeSession newStatelessKnowledgeSession(
                                                                  KnowledgeSessionConfiguration conf) {
        return new StatelessKnowledgeSessionImpl( null,
                                                  this,
                                                  conf );
    }

    public void resourcesChanged(ChangeSet changeSet) {
        try {
            this.listener.debug( "KnowledgeAgent received ChangeSet changed notification" );
            this.queue.put( changeSet );
        } catch ( InterruptedException e ) {
            this.listener.exception( new RuntimeException(
                                                           "KnowledgeAgent error while adding ChangeSet notification to queue",
                                                           e ) );
        }
    }

    /**
     * Rebuilds and creates a new KnowledgeBase for this KnowledgeAgent when
     * called based on the ChangeSet that comes in and if newInstance is set to
     * true. If incremental building or KnowledgeBase updates is on, then this
     * will attempt to update the KnowledgeBase instead.
     *
     * @param changeSetState
     *            The state that the ChangeSet performed
     */
    public void buildKnowledgeBase(ChangeSetState changeSetState) {
        this.listener.debug( "KnowledgeAgent rebuilding KnowledgeBase using ChangeSet" );
        synchronized ( this.registeredResources ) {

            /*
             * Do the following only if we are building a new instance,
             * otherwise, do an incremental build/update
             */
            if ( this.newInstance ) {
                rebuildResources( changeSetState );
            } else {
                incrementalBuildResources( changeSetState );
            }

            /*
             * If the ruleBase is sequential, after rebuilding or incremental
             * update, do an ordering of the ReteooBuilder
             */
            // FIXME: this same code exists in ReteooRuleBase#newStatelessSession()
            InternalRuleBase ruleBase = (InternalRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase;
            ruleBase.lock(); // XXX: readlock might be enough, no idea what order() does.
            try {
                if ( ruleBase.getConfiguration().isSequential() ) {
                    ruleBase.getReteooBuilder().order();
                }
            } finally {
                ruleBase.unlock();
            }
        }
        this.eventSupport.fireKnowledgeBaseUpdated( this.kbase );
        this.listener.debug( "KnowledgeAgent finished rebuilding KnowledgeBase using ChangeSet" );
    }

    /**
     * Same as {@link #createPackageFromResource(org.drools.io.Resource, org.drools.builder.KnowledgeBuilder)
     * createPackageFromResource(org.drools.io.Resource, null)}
     *
     * @param resource
     * @return
     * @see #createPackageFromResource(org.drools.io.Resource, org.drools.builder.KnowledgeBuilder)
     */
    private Collection<KnowledgePackage> createPackageFromResource(Resource resource) {
        return this.createPackageFromResource( resource,
                                               null );
    }

    /**
     * Compiles the resource and returns the created package using the passed
     * kbuilder. If kbuilder is null, a new instance of a Builder is used.
     * Kbuilder is not used for resources that already are packages.
     * @param resource the resource to compile.
     * @param kbuilder the builder used to compile the resource. If the resource
     * is already a package, this builder is not used.
     * @return the package resulting of the compilation of resource.
     */
    @SuppressWarnings("unchecked")
    private Collection<KnowledgePackage> createPackageFromResource(Resource resource,
                                                                   KnowledgeBuilder kbuilder) {

        if ( ((InternalResource) resource).getResourceType() != ResourceType.PKG ) {

            if ( kbuilder == null ) {
                kbuilder = this.createKBuilder();
            }

            kbuilder.add( resource,
                          ((InternalResource) resource).getResourceType() );
            if ( kbuilder.hasErrors() ) {
                this.eventSupport.fireResourceCompilationFailed( kbuilder,
                                                                 resource,
                                                                 ((InternalResource) resource).getResourceType() );
                this.listener.warning(
                                       "KnowledgeAgent has KnowledgeBuilder errors ",
                                       kbuilder.getErrors() );
            }
            if ( kbuilder.getKnowledgePackages().iterator().hasNext() ) {
                return kbuilder.getKnowledgePackages();
            }
            return Collections.emptyList();
        } else {
            // .pks are handled as a special case.
            InputStream is = null;
            Collection<KnowledgePackage> kpkgs = null;
            try {
                is = resource.getInputStream();
                ClassLoader classLoader = null;
                if ( this.isUseKBaseClassLoaderForCompiling() ) {
                    classLoader = ((InternalRuleBase)((KnowledgeBaseImpl)this.kbase).ruleBase).getRootClassLoader();
                } else if ( this.builderConfiguration != null ) {
                    this.listener.warning("Even if a custom KnowledgeBuilderConfiguration was provided, "
                            + " the Knowledge Agent will not use any specific classloader while deserializing packages."
                            + " Expect ClassNotFoundExceptions.");
                }

                Object object = DroolsStreamUtils.streamIn( is, classLoader );
                if ( object instanceof Collection ) {
                    kpkgs = (Collection<KnowledgePackage>) object;
                } else if ( object instanceof KnowledgePackageImp ) {
                    kpkgs = Collections.singletonList( (KnowledgePackage) object );
                } else if( object instanceof Package ) {
                    kpkgs = Collections.singletonList( (KnowledgePackage) new KnowledgePackageImp( (Package) object ) );
                } else if( object instanceof Package[] ) {
                    kpkgs = new ArrayList<KnowledgePackage>();
                    for( Package pkg : (Package[]) object ) {
                        kpkgs.add( new KnowledgePackageImp( pkg ) );
                    }
                } else {
                    throw new RuntimeException("Unknown binary format trying to load resource "+resource.toString());
                }
                for( KnowledgePackage kpkg : kpkgs ) {
                    for ( Rule rule : ((KnowledgePackageImp)kpkg).pkg.getRules() ) {
                        rule.setResource( resource );
                    }
                    for ( Process process : ((KnowledgePackageImp)kpkg).pkg.getRuleFlows().values() ) {
                        ((ResourcedObject) process).setResource( resource );
                    }
                    for ( TypeDeclaration type : ((KnowledgePackageImp)kpkg).pkg.getTypeDeclarations().values() ) {
                        type.setResource( resource );
                    }
                }
            } catch ( Exception ex ) {
                this.listener.exception( new RuntimeException( "KnowledgeAgent exception while trying to deserialize KnowledgeDefinitionsPackage  ",
                                                               ex ) );
            } finally {
                try {
                    if ( is != null ) {
                        is.close();
                    }
                } catch ( IOException e ) {
                    this.listener.exception( new RuntimeException( "KnowledgeAgent exception while trying to close KnowledgeDefinitionsPackage  ",
                                                                   e ) );
                }
            }
            return kpkgs;
        }
    }

    /**
     * This method is meant to rebuild the entire KnowledgeBase. Cached
     * references outside of this Agent will no longer be valid to the current
     * KnowledgeBase
     *
     * @param changeSetState
     *            The ChangeSetState
     */
    private void rebuildResources(ChangeSetState changeSetState) {
        if ( !this.newInstance ) {
            listener.warning( "KnowledgeAgent rebuilding KnowledgeBase when newInstance is false" );
        }

        /*
         * Rebuild a new knowledge base. Try to use the old configuration if
         * possible
         */
        if ( this.kbase != null ) {
            this.kbase = KnowledgeBaseFactory.newKnowledgeBase( ((InternalRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase).getConfiguration() );
        } else {
            this.kbase = KnowledgeBaseFactory.newKnowledgeBase();
        }

        //puts all the resources as added in the changeSet.
        changeSetState.addedResources.clear();
        for ( Resource resource : this.registeredResources.getAllResources() ) {
            // Ignore the HACKs created by org.drools.agent.impl.KnowledgeAgentImpl.autoBuildResourceMapping()
            if ( !(resource instanceof ReaderResource)
                 || ((ReaderResource) resource).getReader() != null ) {
                changeSetState.addedResources.add( resource );
            }
        }
        addResourcesToKnowledgeBase( changeSetState );

        this.listener.info( "KnowledgeAgent new KnowledgeBase now built and in use" );
    }

    /**
     * Processes {@link ChangeSetState#removedResourceMappings},
     * {@link ChangeSetState#addedResources} and
     * {@link ChangeSetState#modifiedResourceMappings} of <code>changeSetState</code>
     * and apply them to {@link #kbase}.
     * The way the lists are processed is:
     * <ol>
     * <li>
     * Each element of {@link ChangeSetState#removedResourceMappings} is removed
     * from {@link #kbase} using {@link #removeKnowledgeDefinitionFromBase(org.drools.definition.KnowledgeDefinition) }.
     * </li>
     * <li>
     * Each element of {@link ChangeSetState#modifiedResourceMappings} is compiled
     * using {@link #createPackageFromResource(org.drools.io.Resource) } and
     * diffed against the previous version of the resource. The diff dictates
     * wich definitions should be removed and what should be updated. The
     * ones that should be removed are deleted using
     * {@link #removeKnowledgeDefinitionFromBase(org.drools.definition.KnowledgeDefinition) },
     * the ones that should be update/added are put into
     * {@link ChangeSetState#createdPackages} of <code>changeSetState</code>.
     * </li>
     * <li>
     * Each element of {@link ChangeSetState#addedResources} is compiled
     * using {@link #createPackageFromResource(org.drools.io.Resource) }
     * and added to {@link ChangeSetState#createdPackages} of
     * <code>changeSetState</code>.
     * </li>
     * </ol>
     * Because the elements of {@link ChangeSetState#addedResources} and
     * {@link ChangeSetState#modifiedResourceMappings} were already processed and
     * added as elements of {@link ChangeSetState#createdPackages}, these two lists
     * are emtpied.
     * The <code>changeSetState</code> is then passed to
     * {@link #addResourcesToKnowledgeBase(org.drools.agent.impl.KnowledgeAgentImpl.ChangeSetState) }
     * in order to process {@link ChangeSetState#createdPackages}.
     * @param changeSetState the ChangeSetState
     */
    private void incrementalBuildResources(ChangeSetState changeSetState) {
        if ( this.newInstance ) {
            this.listener.warning( "KnowledgeAgent incremental build of KnowledgeBase when newInstance is true" );
        }

        KnowledgeBuilder kBuilder = createKBuilder();

        // Incrementally rebuild the resources
        synchronized ( this.registeredResources ) {
            this.listener.info( "KnowledgeAgent performing an incremental build of the ChangeSet" );

            // Create the knowledge base if one does not exist
            if ( this.kbase == null ) {
                this.kbase = KnowledgeBaseFactory.newKnowledgeBase();
            }

            // Remove all rules from the resources removed and also those
            // modified
            for ( Map.Entry<Resource, Set<KnowledgeDefinition>> entry : changeSetState.removedResourceMappings.entrySet() ) {
                for ( KnowledgeDefinition kd : entry.getValue() ) {
                    removeKnowledgeDefinitionFromBase( kd );
                }
            }

            for ( Map.Entry<Resource, Set<KnowledgeDefinition>> entry : changeSetState.modifiedResourceMappings.entrySet() ) {

                Collection<KnowledgePackage> newPackages = createPackageFromResource( entry.getKey() );

                removeDeletedPackages( entry, newPackages );
                
                if( newPackages == null ) {
                    continue;
                }

                for ( KnowledgePackage pkage : newPackages ) {

                    KnowledgePackageImp newPackage = (KnowledgePackageImp) pkage;
                    
                    Set<KnowledgeDefinition> kdefs = getKnowledgeDefinitions( entry, newPackage );

                    KnowledgePackageImp oldPackage = (KnowledgePackageImp) this.kbase.getKnowledgePackage( newPackage.getName() );
                    AbstractRuleBase abstractRuleBase = (AbstractRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase;
                    CompositeClassLoader rootClassLoader = abstractRuleBase.getRootClassLoader();

                    JavaDialectRuntimeData.TypeDeclarationClassLoader tdClassLoader = (JavaDialectRuntimeData.TypeDeclarationClassLoader)
                            ((AbstractRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase).getTypeDeclarationClassLoader();

                    JavaDialectRuntimeData jdata = (JavaDialectRuntimeData) newPackage.pkg.getDialectRuntimeRegistry().getDialectData( "java" );
                    Map<String,byte[]> definedClasses = jdata.getClassDefinitions();
                    for ( String className : definedClasses.keySet() ) {
                        if ( tdClassLoader.getStore().getClassDefinition( className ) != null ) {
                            jdata.removeClassDefinition( className );
                            jdata.getStore().remove( className );
                        }
                    }

                    newPackage.pkg.getDialectRuntimeRegistry().onAdd( rootClassLoader );
                    newPackage.pkg.getDialectRuntimeRegistry().onBeforeExecute();
                    newPackage.pkg.getClassFieldAccessorStore().setClassFieldAccessorCache( abstractRuleBase.getClassFieldAccessorCache() );
                    newPackage.pkg.getClassFieldAccessorStore().wire();

                    this.listener.debug( "KnowledgeAgent: Diffing: " + entry.getKey() );

                    ResourceDiffProducer rdp = new BinaryResourceDiffProducerImpl();

                    //we suppose that the package definition didn't change in the resource.
                    //That's why we are serching the current package as
                    //this.kbase.getKnowledgePackage(kpkg.getName())
                    ResourceDiffResult diff = rdp.diff( kdefs,
                                                        newPackage,
                                                        oldPackage );

                    for ( KnowledgeDefinition kd : diff.getRemovedDefinitions() ) {
                        this.listener.debug( "KnowledgeAgent: Removing: " + kd );
                        removeKnowledgeDefinitionFromBase( kd );
                    }

                    //because all the mappings for "resource" were removed, we
                    //need to map again the definitions that didn't change.
                    //Those modified or added will be mapped in addResourcesToKnowledgeBase()
                    for ( KnowledgeDefinition knowledgeDefinition : diff.getUnmodifiedDefinitions() ) {
                        this.addDefinitionMapping( entry.getKey(),
                                                   knowledgeDefinition,
                                                   false );
                    }

                    Set<KnowledgePackage> set = changeSetState.createdPackages.get(entry.getKey() );
                    if( set == null ) {
                        set = new HashSet<KnowledgePackage>(); 
                        changeSetState.createdPackages.put( entry.getKey(),
                                                            set );
                    }
                    set.add( diff.getPkg() );
                }
            }

            /*
             * Compile the newly added resources
             */
            for ( Resource resource : changeSetState.addedResources ) {
                ///compile the new resource
                Collection<KnowledgePackage> kpkgs = createPackageFromResource( resource, kBuilder );
                if ( kpkgs == null || kpkgs.isEmpty()) {
                    this.listener.warning( "KnowledgeAgent: The resource didn't create any package: " + resource );
                    continue;
                }

                changeSetState.createdPackages.put( resource,
                                                    new HashSet<KnowledgePackage>( kpkgs ) );

            }

            //the added and modified resources were already processed and
            //converted to createdPackages. We must clear the lists.
            changeSetState.addedResources.clear();
            changeSetState.modifiedResourceMappings.clear();

            addResourcesToKnowledgeBase( changeSetState );

        }
        this.listener.info( "KnowledgeAgent incremental build of KnowledgeBase finished and in use" );
    }

    private void removeDeletedPackages(Map.Entry<Resource, Set<KnowledgeDefinition>> entry,
                                       Collection<KnowledgePackage> newPackages) {
        Set<String> newPkgNames = new HashSet<String>();

        if ( newPackages == null || newPackages.isEmpty()) {
            this.listener.warning( "KnowledgeAgent: The resource didn't create any package: " + entry.getKey() + ". Removing any existing knowledge definition of " + entry.getKey() );
        } else {
            for( KnowledgePackage kp : newPackages ) {
                newPkgNames.add( kp.getName() );
            }
        }
        for( KnowledgeDefinition kd : entry.getValue() ) {
            if( ! newPkgNames.contains( kd.getNamespace() ) ) {
                // the package was removed, so remove the kd
                this.listener.debug( "KnowledgeAgent: Removing: " + kd );
                removeKnowledgeDefinitionFromBase( kd );
            }
        }
    }

    private Set<KnowledgeDefinition> getKnowledgeDefinitions(Map.Entry<Resource, Set<KnowledgeDefinition>> entry,
                                                             KnowledgePackageImp newPackage) {
        Set<KnowledgeDefinition> kdefs = new HashSet<KnowledgeDefinition>();
        for( KnowledgeDefinition kdef : entry.getValue() ) {
            if( kdef.getNamespace().equals( newPackage.getName() ) ) {
                kdefs.add( kdef );
            }
        }
        return kdefs;
    }

    /**
     * Removes a definition from {@link #kbase}.
     * @param kd the definition to be removed.
     */
    private void removeKnowledgeDefinitionFromBase(KnowledgeDefinition kd) {
        try {
            if ( kd instanceof Query ) {
                Query query = (Query) kd;
                this.listener.debug( "KnowledgeAgent removing Query=" + query
                                     + " from package=" + query.getPackageName() );
                this.kbase.removeQuery( query.getPackageName(),
                                        query.getName() );
            } else if ( kd instanceof Rule ) {
                Rule rule = (Rule) kd;
                this.listener.debug( "KnowledgeAgent removing Rule=" + rule
                                     + " from package=" + rule.getPackageName() );
                this.kbase.removeRule( rule.getPackageName(),
                                       rule.getName() );
            } else if ( kd instanceof Process ) {
                Process process = (Process) kd;
                this.listener.debug( "KnowledgeAgent removing Process=" + process );
                this.kbase.removeProcess( process.getId() );
            } else if ( kd instanceof TypeDeclaration ) {
                // @TODO Handle Type Declarations... is there a way to remove this?
            } else if ( kd instanceof Function ) {
                Function function = (Function) kd;
                this.kbase.removeFunction( function.getNamespace(),
                                           function.getName() );
            }
        } catch ( IllegalArgumentException e ) {
            //it could be possible that a definition does not longer exists
            //in the kbase.
            this.listener.warning( e.getMessage() );
        }
    }

    /**
     * Adds the resources to the current KnowledgeBase on this KnowledgeAgent.
     * This method processes {@link ChangeSetState#addedResources} and
     * {@link ChangeSetState#createdPackages} lists in two different ways:
     * <ul>
     * <li>
     * The elments of {@link ChangeSetState#addedResources} are compiled using
     * {@link #createPackageFromResource(org.drools.io.Resource, org.drools.builder.KnowledgeBuilder)}
     * and added to {@link ChangeSetState#createdPackages}. The same kbuilder
     * is used for all the elements.
     * </li>
     * <li>
     * The elments of {@link ChangeSetState#createdPackages} are added to
     * {@link #kbase}. Each package is mapped to the original resource
     * using {@link #buildResourceMapping(org.drools.rule.Package, org.drools.io.Resource)}.
     * </li>
     * </ul>
     *
     *
     * @param changeSetState the object containing the added resources list and
     * created packages list.
     */
    private void addResourcesToKnowledgeBase(ChangeSetState changeSetState) {

        if ( !changeSetState.addedResources.isEmpty() ) {
            KnowledgeBuilder builder = createKBuilder();
            for ( Resource resource : changeSetState.addedResources ) {
                Collection<KnowledgePackage> createdPackages = this.createPackageFromResource( resource, builder );
                Set<KnowledgePackage> packs = changeSetState.createdPackages.get( resource );
                if ( packs == null ) {
                    packs = new HashSet<KnowledgePackage>();
                    changeSetState.createdPackages.put( resource, packs );
                }
                packs.addAll( createdPackages );
            }
        }

        Set<KnowledgePackage> createdDistinctPackages = new LinkedHashSet<KnowledgePackage>();
        for ( Resource resource : changeSetState.createdPackages.keySet() ) {
            createdDistinctPackages.addAll( changeSetState.createdPackages.get( resource ) );
        }
        this.kbase.addKnowledgePackages( createdDistinctPackages );

        autoBuildResourceMapping();

    }

    /*
     * (non-Javadoc)
     *
     * @see org.drools.agent.KnowledgeAgent#getName()
     */
    public String getName() {
        return this.name;

    }

    /**
     * Kicks off the monitoring service for handling ResourceChangeEvents on a
     * separate process.
     *
     * @boolean monitor True if monitoring should take place, false otherwise
     */
    public void monitorResourceChangeEvents(boolean monitor) {

        Set<Resource> allResources = new HashSet<Resource>();
        allResources.addAll( this.resourceDirectories );
        allResources.addAll( this.registeredResources.getAllResources() );
        allResources.addAll( this.dslResources.keySet() );

        //subscribe/unsubscribe from resources
        for ( Resource resource : allResources ) {
            if ( monitor ) {
                this.listener.debug( "KnowledgeAgent subscribing from resource="
                                     + resource );
                this.notifier.subscribeResourceChangeListener( this,
                                                               resource );
            } else {
                this.listener.debug( "KnowledgeAgent unsubscribing from resource="
                                     + resource );
                this.notifier.unsubscribeResourceChangeListener( this,
                                                                 resource );
            }
        }

        if ( !monitor && this.changeSetNotificationDetector != null ) {
            // we are running, but it wants to stop
            // this will stop the thread
            this.changeSetNotificationDetector.stop();
            this.notificationDetectorExecutor.cancel( true );
            this.changeSetNotificationDetector = null;
        } else if ( monitor && this.changeSetNotificationDetector == null ) {
            this.changeSetNotificationDetector = new ChangeSetNotificationDetector( this,
                                                                                    this.queue,
                                                                                    this.listener );
            this.notificationDetectorExecutor =
                    ExecutorProviderFactory.getExecutorProvider().<Boolean> getCompletionService()
                            .submit( this.changeSetNotificationDetector, true );
        }
    }

    /**
     *
     * @param resource
     * @param notify
     * @return
     */
    public boolean addResourceMapping(Resource resource,
                                      boolean notify) {
        boolean newMapping = this.registeredResources.createNewResourceEntry( resource );

        if ( notify && newMapping ) {
            this.listener.debug( "KnowledgeAgent notifier subscribing to resource="
                                 + resource );
            this.notifier.subscribeResourceChangeListener( this,
                                                           resource );
            return true;
        }
        return false;
    }

    /**
     * Returns the Resource -> KnowledgeItem mapping
     * @return
     */
    public Map<Resource, Set<KnowledgeDefinition>> getRegisteredResources() {
        return registeredResources.map;
    }

    /**
     * Add an resource/definition entry to registeredResources. Optionaly it
     * adds a listener to the resource added.
     * @param resource
     * @param definition
     * @param notify
     * @return if the resource/definition didn't exist in registeredResources.
     */
    public boolean addDefinitionMapping(Resource resource,
                                        KnowledgeDefinition definition,
                                        boolean notify) {

        if ( resource == null ) {
            listener.warning( "KnowledgeAgent: impossible to add a map for a null resource! skiping." );
            return false;
        }

        this.listener.debug( "KnowledgeAgent mapping resource="
                             + resource + " to KnowledgeDefinition=" + definition );

        boolean isNewResource = !this.registeredResources.isResourceMapped( resource );

        if ( resource instanceof ClassPathResource && ((ClassPathResource) resource).getClassLoader() == null ) {
            ((ClassPathResource) resource).setClassLoader( ((InternalRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()).getRootClassLoader() );
        }

        boolean isNewDefinition = true;
        if ( definition != null ) {
            isNewDefinition = this.registeredResources.putDefinition( resource,
                                                                      definition );
        }

        if ( notify && isNewResource ) {
            this.listener.debug( "KnowledgeAgent notifier subscribing to resource="
                                 + resource );

            this.notifier.subscribeResourceChangeListener( this,
                                                           resource );
        }

        return isNewDefinition;
    }

    public Set<KnowledgeDefinition> removeResourceMapping(Resource resource,
                                                          boolean unsubscribe) {
        this.listener.debug( "KnowledgeAgent removing mappings for resource="
                             + resource + " with unsubscribe=" + unsubscribe );
        Set<KnowledgeDefinition> definitions = this.registeredResources.removeDefinitions( resource );

        if ( definitions != null ) {
            if ( unsubscribe ) {
                this.listener.debug( "KnowledgeAgent notifier unsubscribing to resource="
                                     + resource );

                this.notifier.unsubscribeResourceChangeListener(
                                                                 this,
                                                                 resource );
            }
        }
        return definitions;

    }

    /**
     * A class to monitor and handle ChangeSets fired by the
     * ResourceChangeNotifier on a separate service (or process).
     */
    public static class ChangeSetNotificationDetector
            implements
            Runnable {

        private final LinkedBlockingQueue<ChangeSet> queue;
        private volatile boolean                     monitor;
        private final KnowledgeAgentImpl             kagent;
        private final SystemEventListener            listener;

        public ChangeSetNotificationDetector(KnowledgeAgentImpl kagent,
                                             LinkedBlockingQueue<ChangeSet> queue,
                                             SystemEventListener listener) {
            this.queue = queue;
            this.kagent = kagent;
            this.listener = listener;
            this.monitor = true;
        }

        public void stop() {
            this.monitor = false;
        }

        public void run() {
            if ( this.monitor ) {
                this.listener.info( "KnowledgeAgent has started listening for ChangeSet notifications" );
            }
            while ( this.monitor ) {
                Exception exception = null;
                try {
                    kagent.applyChangeSet( this.queue.take() );
                } catch ( InterruptedException e ) {
                    exception = e;
                }
                Thread.yield();
                if ( this.monitor && exception != null ) {
                    this.listener.exception( new RuntimeException(
                                                                   "KnowledgeAgent ChangeSet notification thread has been interrupted, but shutdown was not scheduled",
                                                                   exception ) );
                }
            }

            this.listener.info( "KnowledgeAgent has stopped listening for ChangeSet notifications" );
        }
    }

    private static class RegisteredResourceMap {

        private final Map<Resource, Set<KnowledgeDefinition>> map = new HashMap<Resource, Set<KnowledgeDefinition>>();

        /**
         * Creates a new entry for resource with an empty Set<KnowledgeDefinition>.
         * If the map already contains an entry for the resource, then nothing
         * is changed.
         * @param resource
         * @return true if the resource was not previously mapped.
         */
        public boolean createNewResourceEntry(Resource resource) {
            if ( !this.isResourceMapped( resource ) ) {
                this.map.put( resource,
                              new HashSet<KnowledgeDefinition>() );
                return true;
            }
            return false;
        }

        public boolean putDefinition(Resource resource,
                                     KnowledgeDefinition definition) {
            Set<KnowledgeDefinition> defList = map.get( resource );
            if ( defList == null ) {
                defList = new HashSet<KnowledgeDefinition>();
                this.map.put( resource,
                              defList );
            }

            //support for lazy loading
            return definition != null && defList.add( definition );
        }

        public Set<KnowledgeDefinition> removeDefinitions(Resource resource) {
            return this.map.remove( resource );
        }

        public Set<KnowledgeDefinition> getDefinitions(Resource resource) {
            return this.getDefinitions( resource,
                                        false );
        }

        public Set<KnowledgeDefinition> getDefinitions(Resource resource,
                                                       boolean returnEmptyIfNull) {
            Set<KnowledgeDefinition> definitions = this.map.get( resource );
            if ( returnEmptyIfNull && definitions == null ) {
                definitions = new HashSet<KnowledgeDefinition>();
            }
            return definitions;
        }

        public boolean isResourceMapped(Resource resource) {
            return this.map.containsKey( resource );
        }

        public Set<Resource> getAllResources() {
            return this.map.keySet();
        }

        public boolean onlyHasPKGResources() {
            for ( Resource resource : map.keySet() ) {
                if ( ((InternalResource) resource).getResourceType() != ResourceType.PKG ) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Creates a kbuilder if necessary. The built kbuilder will contain all the
     * DSL resources the agent is managing. It will also apply any
     * KnowledgeBuilderConfiguration present in {@link #builderConfiguration} or
     * will copy any KnowledgeBuilderConfiguration from the current kbase if
     * {@link #useKBaseClassLoaderForCompiling} is true.
     * This method will return null if the agent is only managing binary resources.
     * This avoids drools-compiler dependency.
     *
     * @return a new kbuilder or null if all the managed resources are binaries.
     */
    private KnowledgeBuilder createKBuilder() {

        if ( this.registeredResources.onlyHasPKGResources() ) {
            return null;
        }

        KnowledgeBuilder kbuilder;
        if ( this.builderConfiguration != null ) {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( this.builderConfiguration );
        } else if ( this.isUseKBaseClassLoaderForCompiling() ) {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( this.kbase );
        } else {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        }

        if ( this.dslResources != null ) {
            for ( Map.Entry<Resource, String> entry : this.dslResources.entrySet() ) {
                kbuilder.add( ResourceFactory.newByteArrayResource( entry.getValue().getBytes() ),
                              ResourceType.DSL );
            }
        }

        return kbuilder;
    }

    private void retrieveDSLResource(Resource resource) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader( resource.getReader() );
            String line;
            StringBuilder content = new StringBuilder();
            while ( (line = bufferedReader.readLine()) != null ) {
                content.append( line );
                content.append( "\n" );
            }

            this.dslResources.put( resource,
                                   content.toString() );
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }

    public void addEventListener(KnowledgeAgentEventListener listener) {
        this.eventSupport.addEventListener( listener );
    }

    public void removeEventListener(KnowledgeAgentEventListener listener) {
        this.eventSupport.removeEventListener( listener );
    }

    public void dispose() {
        synchronized ( this.registeredResources ) {
            //all kbase's ksessions must be disposed
            if ( this.kbase != null ) {
                Collection<StatefulKnowledgeSession> statefulSessions = this.kbase.getStatefulKnowledgeSessions();
                if ( statefulSessions != null && !statefulSessions.isEmpty()) {
                    String message = "The kbase still contains " + statefulSessions.size() + " stateful sessions. You must dispose them first.";
                    this.listener.warning( message );
                    throw new IllegalStateException( message );
                }
            }

            //stop changeSet Notification Detector
            this.monitorResourceChangeEvents( false );
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        // users should turn off monitoring, but just in case when this class is
        // GC'd we turn off the thread
        if ( this.changeSetNotificationDetector != null ) {
            this.changeSetNotificationDetector.monitor = false;

        }
    }
    

}
