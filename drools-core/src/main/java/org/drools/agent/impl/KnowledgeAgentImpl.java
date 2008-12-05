package org.drools.agent.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.drools.KnowledgeBase;
import org.drools.ChangeSet;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentEventListener;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgeDefinition;
import org.drools.definition.process.Process;
import org.drools.event.io.ResourceChangeListener;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.Resource;
import org.drools.io.InternalResource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.rule.Function;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;

public class KnowledgeAgentImpl
    implements
    KnowledgeAgent,
    ResourceChangeListener, Runnable {
    private String                         name;
    private Map<Resource, ResourceMapping> resources;
    private Set<Resource>                  resourceDirectories;
    private KnowledgeBase                  kbase;
    private ResourceChangeNotifierImpl     notifier;
    private boolean                        newInstance;
    private KnowledgeAgentEventListener    listener;
    private LinkedBlockingQueue<ChangeSet> queue;
    private Thread                         thread;
    private volatile boolean               monitor;

    public KnowledgeAgentImpl(String name,
                              KnowledgeBase kbase,
                              KnowledgeAgentConfiguration configuration,
                              KnowledgeAgentEventListener listener) {
        this.kbase = kbase;
        this.resources = new HashMap<Resource, ResourceMapping>();
        this.listener = listener;
        this.newInstance = true; // we hard code this for now as incremental kbase changes don't work.
        if ( configuration != null ) {
            //this.newInstance = ((KnowledgeAgentConfigurationImpl) configuration).isNewInstance();
            this.notifier = (ResourceChangeNotifierImpl) ResourceFactory.getResourceChangeNotifierService();
            if ( ((KnowledgeAgentConfigurationImpl) configuration).isMonitorChangeSetEvents() ) {
                this.monitor = true;
            }
            if ( ((KnowledgeAgentConfigurationImpl) configuration).isScanResources() ) {
                this.notifier.addResourceChangeMonitor( ResourceFactory.getResourceChangeScannerService() );
                this.monitor = true; // if scanning, monitor must be true;
            }
        }
        
        if ( this.monitor ) {
            this.queue = new LinkedBlockingQueue<ChangeSet>();
            thread = new Thread( this );
            thread.start();
        }
        
        buildResourceMapping( kbase );
    }

    public void buildResourceMapping(KnowledgeBase kbase) {
        RuleBase rbase = ((KnowledgeBaseImpl) kbase).ruleBase;

        synchronized ( this.resources ) {

            for ( Package pkg : rbase.getPackages() ) {
                
                for ( Resource resource : pkg.getResourceDirectories() ) {
                    this.notifier.subscribeResourceChangeListener( this,
                                                                   resource );
                }
                
                for ( Rule rule : pkg.getRules() ) {
                    Resource resource = rule.getResource();
                    if ( resource == null || !((InternalResource)resource).hasURL() ) {
                        continue;
                    }
                    ResourceMapping mapping = this.resources.get( resource );
                    if ( mapping == null ) {
                        this.notifier.subscribeResourceChangeListener( this,
                                                                       resource );
                        mapping = new ResourceMapping( resource );
                        this.resources.put( resource,
                                            mapping );
                    }
                    mapping.getKnowledgeDefinitions().add( rule );
                    System.out.println( "agent : " + resource );
                }

                for ( Process process : pkg.getRuleFlows().values() ) {
                    Resource resource = ((org.drools.process.core.Process) process).getResource();
                    if ( resource == null || !((InternalResource)resource).hasURL() ) {
                        continue;
                    }
                    ResourceMapping mapping = this.resources.get( resource );
                    if ( mapping == null ) {
                        this.notifier.subscribeResourceChangeListener( this,
                                                                       resource );
                        mapping = new ResourceMapping( resource );
                        this.resources.put( resource,
                                            mapping );
                    }
                    mapping.getKnowledgeDefinitions().add( process );
                    System.out.println( "agent : " + resource );
                }

                for ( TypeDeclaration typeDeclaration : pkg.getTypeDeclarations().values() ) {
                    Resource resource = typeDeclaration.getResource();
                    if ( resource == null || !((InternalResource)resource).hasURL() ) {
                        continue;
                    }
                    ResourceMapping mapping = this.resources.get( resource );
                    if ( mapping == null ) {
                        this.notifier.subscribeResourceChangeListener( this,
                                                                       resource );
                        mapping = new ResourceMapping( resource );
                        this.resources.put( resource,
                                            mapping );
                    }
                    mapping.getKnowledgeDefinitions().add( typeDeclaration );
                    System.out.println( "agent : " + resource );
                }

                for ( Function function : pkg.getFunctions().values() ) {
                    Resource resource = function.getResource();
                    if ( resource == null || !((InternalResource)resource).hasURL() ) {
                        continue;
                    }
                    ResourceMapping mapping = this.resources.get( resource );
                    if ( mapping == null ) {
                        this.notifier.subscribeResourceChangeListener( this,
                                                                       resource );
                        mapping = new ResourceMapping( resource );
                        this.resources.put( resource,
                                            mapping );
                    }
                    mapping.getKnowledgeDefinitions().add( function );
                    System.out.println( "agent : " + resource );
                }
            }
        }
    }

    public KnowledgeBase getKnowledgeBase() {
        synchronized ( this.resources ) {
            return this.kbase;
        }
    }

    //    public void resourceModified(ResourceModifiedEvent event) {
    //        ResourceMapping mapping = this.resources.get( event.getResource() );
    //        System.out.println( "modified : " + event.getResource() );
    //        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    //        synchronized ( this.resources ) {
    //            for ( Resource resource : this.resources.keySet() ) {
    //                System.out.println( "building : " + resource );
    //                kbuilder.add( resource,
    //                              KnowledgeType.DRL );
    //            }
    //
    //            this.kbase = KnowledgeBaseFactory.newKnowledgeBase();
    //            this.kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
    //        }
    //    }

    public void resourcesChanged(ChangeSet changeSet) {
        try {
            System.out.println( "agent resource changed" );
            this.queue.put( changeSet );
        } catch ( InterruptedException e ) {
            // @TODO add proper error message
            e.printStackTrace();
        }
    }

    public static class ResourceMapping {
        private Resource                 resource;
        private Set<KnowledgeDefinition> knowledgeDefinitions;

        public ResourceMapping(Resource resource) {
            this.knowledgeDefinitions = new HashSet<KnowledgeDefinition>();
        }

        public Resource getResource() {
            return resource;
        }

        public Set<KnowledgeDefinition> getKnowledgeDefinitions() {
            return knowledgeDefinitions;
        }

    }
    
    private void processChangeSet(ChangeSet changeSet) {
        // for now we assume newIntance only, so just blow away the mappings and knowledgedefinition sets.
        synchronized ( this.resources ) {
            // first remove the unneeded resources        
            for ( Resource resource : changeSet.getResourcesRemoved() ) {
                this.resources.remove( resource );
            }

            // now add the new ones
            for ( Resource resource : changeSet.getResourcesAdded() ) {
                this.resources.put( resource,
                                    null );
            }

            // modified we already know is in the map, so no need to process those

            // now make a copy of the resource keys, as we are about to reset it, but need the keys to rebuild the kbase
            Resource[] resourcesClone = this.resources.keySet().toArray( new Resource[this.resources.size()] );

            // reset the resources map, so it can now be rebuilt
            this.resources.clear();

            // rebuild the kbase
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

            for ( Resource resource : resourcesClone ) {
                System.out.println( "building : " + resource );
                kbuilder.add( resource,
                              ((InternalResource)resource).getResourceType() );
            }

            this.kbase = KnowledgeBaseFactory.newKnowledgeBase();
            this.kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        }

        // code commented out to try and do incremental kbase changes
        // @TODO get this working for incremental changes
        //        synchronized ( this.resources ) {
        //            // first deal with removals
        //            for ( Resource resource : changeSet.getResourcesRemoved() ) {
        //                ResourceMapping mapping = this.resources.remove(resource );
        //                if ( !this.newInstance  ) {
        //                    // we are keeping the current instance, so we need remove the individual knowledge definitions           
        //                    for ( KnowledgeDefinition kd : mapping.getKnowledgeDefinitions() ) {
        //                        if ( kd instanceof Rule ) {
        //                            Rule rule = ( Rule ) kd;
        //                            this.kbase.removeRule( rule.getPackageName(), rule.getName() );
        //                        } else if ( kd instanceof Process ) {
        //                            Process process = ( Process ) kd;
        //                            this.kbase.removeProcess( process.getId() );
        //                        }
        //                        // @TODO functions and type declarations
        //                    }
        //                }
        //            }
        //            
        //            // now deal with additions
        //            for ( Resource resource : changeSet.getResourcesAdded() ) {
        //                
        //            }
        //            
        //            // final deal with modifies
        //        }        
    }

    public String getName() {
        return this.name;
    }
    
    public void monitorResourceChangeEvents(boolean monitor) {
        if ( !this.monitor && monitor ) {
            // If the thread is not running and we are trying to start it, we must create a new Thread
            this.monitor = monitor;
            this.thread = new Thread( this );
            this.thread.start();
        }
        this.monitor = monitor;
    }
    
    public void run() { 
        while ( this.monitor ) {           
            try {                
                processChangeSet( this.queue.take() );
            } catch ( InterruptedException e ) {
                // @TODO print proper error message
                e.printStackTrace();
            }
            Thread.yield();
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        // users should turn off monitoring, but just in case when this class is GC'd we turn off the thread
        this.monitor = false;
    }
}
