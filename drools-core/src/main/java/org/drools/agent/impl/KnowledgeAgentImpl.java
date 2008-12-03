package org.drools.agent.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentEventListener;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.definition.process.Process;
import org.drools.event.io.ResourceChangeListener;
import org.drools.event.io.ResourceModifiedEvent;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.rule.Function;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;

public class KnowledgeAgentImpl
    implements
    KnowledgeAgent,
    ResourceChangeListener {
    private String name;
    private Map<Resource, ResourceMapping> resources;
    private KnowledgeBase                  kbase;
    private ResourceChangeNotifierImpl     notifier;
    private boolean                        newInstance;
    private KnowledgeAgentEventListener    listener;

    public KnowledgeAgentImpl(String name,
                              KnowledgeBase kbase,
                              KnowledgeAgentConfiguration configuration,
                              KnowledgeAgentEventListener listener) {
        this.kbase = kbase;
        this.resources = new HashMap<Resource, ResourceMapping>();
        this.listener = listener;
        if ( configuration != null ) {
            this.newInstance = ((KnowledgeAgentConfigurationImpl) configuration).isNewInstance();
            this.notifier = (ResourceChangeNotifierImpl) ResourceFactory.getResourceChangeNotifierService();
            if ( ((KnowledgeAgentConfigurationImpl) configuration).isScanResources() ) {
                this.notifier.addResourceChangeMonitor( ResourceFactory.getResourceChangeScannerService() );
            }
        }
        buildResourceMapping( kbase );
    }

    public void buildResourceMapping(KnowledgeBase kbase) {
        RuleBase rbase = ((KnowledgeBaseImpl) kbase).ruleBase;

        synchronized ( this.resources ) {

            for ( Package pkg : rbase.getPackages() ) {
                for ( Rule rule : pkg.getRules() ) {
                    Resource resource = rule.getResource();
                    if ( resource == null || !resource.hasURL() ) {
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
                    mapping.getObjects().add( rule );
                    System.out.println( "agent : " + resource );
                }

                for ( Process process : pkg.getRuleFlows().values() ) {
                    Resource resource = ((org.drools.process.core.Process) process).getResource();
                    if ( resource == null || !resource.hasURL() ) {
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
                    mapping.getObjects().add( process );
                    System.out.println( "agent : " + resource );
                }

                for ( TypeDeclaration typeDeclaration : pkg.getTypeDeclarations().values() ) {
                    Resource resource = typeDeclaration.getResource();
                    if ( resource == null || !resource.hasURL() ) {
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
                    mapping.getObjects().add( typeDeclaration );
                    System.out.println( "agent : " + resource );
                }

                for ( Function function : pkg.getFunctions().values() ) {
                    Resource resource = function.getResource();
                    if ( resource == null || !resource.hasURL() ) {
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
                    mapping.getObjects().add( function );
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

    public void resourceModified(ResourceModifiedEvent event) {
        ResourceMapping mapping = this.resources.get( event.getResource() );
        System.out.println( "modified : " + event.getResource() );
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        synchronized ( this.resources ) {
            for ( Resource resource : this.resources.keySet() ) {
                System.out.println( "building : " + resource );
                kbuilder.add( resource,
                              KnowledgeType.DRL );
            }

            this.kbase = KnowledgeBaseFactory.newKnowledgeBase();
            this.kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        }
    }

    public void resourceAdded(ResourceModifiedEvent event) {
        // TODO Auto-generated method stub

    }

    public void resourceRemoved(ResourceModifiedEvent event) {
        // TODO Auto-generated method stub

    }

    public static class ResourceMapping {
        private Resource resource;
        private Set      objects;

        public ResourceMapping(Resource resource) {
            this.objects = new HashSet<Object>();
        }

        public Resource getResource() {
            return resource;
        }

        public Set getObjects() {
            return objects;
        }

    }

    public String getName() {
        return this.name;
    }
}
