package org.drools.agent.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.drools.ChangeSet;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.SystemEventListener;
import org.drools.SystemEventListenerFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.ResourceDiffProducer;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalRuleBase;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.definition.KnowledgeDefinition;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.event.KnowledgeAgentEventSupport;
import org.drools.event.io.ResourceChangeListener;
import org.drools.event.knowledgeagent.KnowledgeAgentEventListener;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatelessKnowledgeSessionImpl;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.io.internal.InternalResource;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.rule.Function;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.xml.ChangeSetSemanticModule;
import org.drools.xml.SemanticModules;
import org.drools.xml.XmlChangeSetReader;

/**
 * Drools Implementation of the KnowledgeAgent interface. Implements itself as a
 * ResourceChangeListener as well so it can act as an agent service to provide
 * incremental of the KnowledgeBase which connects to this or entirely new
 * rebuilds for new KnowledgeBases.
 *
 * @author Mark Proctor, Sam Romano
 */
public class KnowledgeAgentImpl implements KnowledgeAgent,
        ResourceChangeListener {

    private String name;
    private Set<Resource> resourceDirectories;
    private KnowledgeBase kbase;
    private ResourceChangeNotifierImpl notifier;
    private boolean newInstance;
    private SystemEventListener listener;
    private boolean scanDirectories;
    private boolean useKBaseClassLoaderForCompiling;
    private LinkedBlockingQueue<ChangeSet> queue;
    private Thread thread;
    private ChangeSetNotificationDetector changeSetNotificationDetector;
    private SemanticModules semanticModules;
    private final RegisteredResourceMap registeredResources = new RegisteredResourceMap();
    private Map<Resource, String> dslResources = new HashMap<Resource, String>();
    private KnowledgeAgentEventSupport eventSupport = new KnowledgeAgentEventSupport();
    private KnowledgeBuilderConfiguration builderConfiguration;

    /**
     * Default constructor for KnowledgeAgentImpl
     *
     * @param name
     * @param kbase
     * @param configuration
     */
    public KnowledgeAgentImpl(String name, KnowledgeBase kbase,
            KnowledgeAgentConfiguration configuration, KnowledgeBuilderConfiguration builderConfiguration) {
        this.name = name;
        this.kbase = kbase;
        this.builderConfiguration = builderConfiguration;
        this.resourceDirectories = new HashSet<Resource>();
        // this.listener = listener;
        this.listener = SystemEventListenerFactory.getSystemEventListener();
        this.queue = new LinkedBlockingQueue<ChangeSet>();
        boolean scanResources = false;
        boolean monitor = false;
        if (configuration != null) {
            // New Instance describes if we do incremental builds or not
            this.newInstance = ((KnowledgeAgentConfigurationImpl) configuration).isNewInstance();
            this.useKBaseClassLoaderForCompiling = ((KnowledgeAgentConfigurationImpl) configuration).isUseKBaseClassLoaderForCompiling();
            this.notifier = (ResourceChangeNotifierImpl) ResourceFactory.getResourceChangeNotifierService();
            if (((KnowledgeAgentConfigurationImpl) configuration).isMonitorChangeSetEvents()) {
                monitor = true;
            }

            if (((KnowledgeAgentConfigurationImpl) configuration).isScanDirectories()) {
                this.scanDirectories = true;
            }

            scanResources = ((KnowledgeAgentConfigurationImpl) configuration).isScanResources();
            if (scanResources) {
                this.notifier.addResourceChangeMonitor(ResourceFactory.getResourceChangeScannerService());
                monitor = true; // if scanning, monitor must be true;
            }
        }

        monitorResourceChangeEvents(monitor);


        autoBuildResourceMapping();

        this.listener.info("KnowledgeAgent created, with configuration:\nmonitorChangeSetEvents="
                + monitor
                + " scanResources="
                + scanResources
                + " scanDirectories="
                + this.scanDirectories
                + " newInstance=" + this.newInstance);
    }

    public void setSystemEventListener(SystemEventListener listener) {
        this.listener = listener;
    }
    
    public Set<Resource> getResourceDirectories() {
    	return this.resourceDirectories;
    }

    public boolean isNewInstance() {
        return this.newInstance;
    }
    
    public void applyChangeSet(Resource resource) {
        applyChangeSet(getChangeSet(resource));
    }

    public void applyChangeSet(ChangeSet changeSet) {
        synchronized (this.registeredResources) {
            this.eventSupport.fireBeforeChangeSetApplied(changeSet);

            this.listener.info("KnowledgeAgent applying ChangeSet");

            ChangeSetState changeSetState = new ChangeSetState();
            changeSetState.scanDirectories = this.scanDirectories;
            // incremental build is inverse of newInstance
            changeSetState.incrementalBuild = !(this.newInstance);

            // Process the new ChangeSet
            processChangeSet(changeSet, changeSetState);
            // Rebuild or do an update to the KnowledgeBase
            buildKnowledgeBase(changeSetState);
            // Rebuild the resource mapping
            //buildResourceMapping();

            this.eventSupport.fireAfterChangeSetApplied(changeSet);
        }
    }

    public void processChangeSet(Resource resource,
            ChangeSetState changeSetState) {
        processChangeSet(getChangeSet(resource), changeSetState);
    }

    /**
     * Processes a changeSet.
     * If {@link ChangeSetState#incrementalBuild} is set to true, this method
     * fill the lists and Maps of <code>changeSetState</code>.
     * 
     * @param changeSet
     * @param changeSetState
     */
    public void processChangeSet(ChangeSet changeSet,
            ChangeSetState changeSetState) {
        synchronized (this.registeredResources) {
            this.eventSupport.fireBeforeChangeSetProcessed(changeSet);

            /*
             * Process the added resources from a ChangeSet by subscribing to
             * the notifier and inserting a new ResourceMapping.
             */
            for (Resource resource : changeSet.getResourcesAdded()) {
                this.eventSupport.fireBeforeResourceProcessed(changeSet, resource, ((InternalResource) resource).getResourceType(), ResourceStatus.RESOURCE_ADDED);
                if (((InternalResource) resource).getResourceType() == ResourceType.DSL) {
                    this.notifier.subscribeResourceChangeListener(this,
                            resource);
                    try {
                        this.retrieveDSLResource(resource);
                    } catch (IOException ex) {
                        this.listener.exception("KnowledgeAgent Fails trying to read DSL Resource: "
                                + resource, ex);
                    }
                } else if (((InternalResource) resource).getResourceType() == ResourceType.CHANGE_SET) {
                    // @TODO We should not ignore an added change set
                    this.listener.debug("KnowledgeAgent processing sub ChangeSet="
                            + resource);
                    processChangeSet(resource, changeSetState);
                } else if (((InternalResource) resource).isDirectory()) {
                    this.resourceDirectories.add(resource);
                    this.listener.debug("KnowledgeAgent subscribing to directory="
                            + resource);
                    this.notifier.subscribeResourceChangeListener(this,
                            resource);
                    // if it's a dir, subscribe it's children first
                    for (Resource child : ((InternalResource) resource).listResources()) {

                        // ignore sub directories
                        if (((InternalResource) child).isDirectory()) {
                            continue;
                        }

                        ((InternalResource) child).setResourceType(((InternalResource) resource).getResourceType());

                        this.addDefinitionMapping(child, null, true);
                        if (this.addResourceMapping(child, true)
                                && changeSetState.incrementalBuild) {
                            changeSetState.addedResources.add(child);
                        }
                    }
                } else {
                    if (this.addResourceMapping(resource, true)
                            && changeSetState.incrementalBuild) {
                        changeSetState.addedResources.add(resource);

                    }
                }
                this.eventSupport.fireAfterResourceProcessed(changeSet, resource, ((InternalResource) resource).getResourceType(), ResourceStatus.RESOURCE_ADDED);
            }

            /*
             * For those marked as removed by the ChangeSet, remove their
             * mappings, index them if we are doing incremental builds so the
             * incremental building process knows what to remove.
             */
            for (Resource resource : changeSet.getResourcesRemoved()) {
                this.eventSupport.fireBeforeResourceProcessed(changeSet, resource, ((InternalResource) resource).getResourceType(), ResourceStatus.RESOURCE_MODIFIED);
                if (((InternalResource) resource).getResourceType() == ResourceType.DSL) {
                    this.notifier.unsubscribeResourceChangeListener(this,
                            resource);
                    this.dslResources.remove(resource);
                } else if (((InternalResource) resource).getResourceType() == ResourceType.CHANGE_SET) {
                    // @TODO Is this true? Shouldn't we just ignore it in
                    // removed?
                    processChangeSet(resource, changeSetState);
                } else if (changeSetState.scanDirectories
                        && ((InternalResource) resource).isDirectory()) {
                    this.listener.debug("KnowledgeAgent unsubscribing from directory resource="
                            + resource);
                    this.resourceDirectories.remove(resource);
                    this.notifier.unsubscribeResourceChangeListener(this,
                            resource);
                } else {

                    Set<KnowledgeDefinition> definitions = this.removeResourceMapping(resource, true);

                    if (definitions != null && changeSetState.incrementalBuild) {
                        changeSetState.removedResourceMappings.put(resource, definitions);
                    }
                }
                this.eventSupport.fireAfterResourceProcessed(changeSet, resource, ((InternalResource) resource).getResourceType(), ResourceStatus.RESOURCE_MODIFIED);
            }

            /*
             * For those marked as modified, remove their ResourceMapping,
             * attach it to the ChangeSetState, and add a new one - it will be
             * repopulated with the KnowledgeDefinitions later after rebuilding.
             * Process any modified ChangeSets - treat them as if they were new.
             */
            for (Resource resource : changeSet.getResourcesModified()) {
                this.eventSupport.fireBeforeResourceProcessed(changeSet, resource, ((InternalResource) resource).getResourceType(), ResourceStatus.RESOURCE_REMOVED);
                if (((InternalResource) resource).getResourceType() == ResourceType.DSL) {
                    try {
                        this.retrieveDSLResource(resource);
                    } catch (IOException ex) {
                        this.listener.exception("KnowledgeAgent Fails trying to read DSL Resource: "
                                + resource, ex);
                    }
                } else if (((InternalResource) resource).getResourceType() == ResourceType.CHANGE_SET) {
                    // processChangeSet(resource, changeSetState);
                    continue;
                } else if (((InternalResource) resource).isDirectory()) {
                    if (this.resourceDirectories.add(resource)) {
                        this.listener.warning("KnowledgeAgent is subscribing to a modified directory="
                                + resource
                                + " when it should have already been subscribed");
                        this.notifier.subscribeResourceChangeListener(this,
                                resource);
                    }
                    // if it's a dir, subscribe it's children first
                    for (Resource child : ((InternalResource) resource).listResources()) {

                        // ignore sub directories
                        if (((InternalResource) child).isDirectory()) {
                            continue;
                        }

                        if (this.addResourceMapping(child, true)) {
                            ((InternalResource) child).setResourceType(((InternalResource) resource).getResourceType());
                            if (changeSetState.incrementalBuild) {
                                changeSetState.addedResources.add(child);
                            }
                        }
                    }
                } else {

                    boolean isResourceMapped = this.registeredResources.isResourceMapped(resource);

                    if (!isResourceMapped) {
                        this.listener.warning("KnowledgeAgent subscribing to new resource="
                                + resource
                                + ", though it was marked as modified.");
                        this.addResourceMapping(resource, true);
                        if (changeSetState.incrementalBuild) {
                            changeSetState.addedResources.add(resource);
                        }
                    } else {
                        if (changeSetState.incrementalBuild) {

                            Set<KnowledgeDefinition> definitions = this.removeResourceMapping(resource, true);

                            changeSetState.modifiedResourceMappings.put(resource, definitions);

                            //adds a new empty mapping that will be filled in buildKnowledgeBase()
                            this.addResourceMapping(resource, false);
                        }
                    }
                }
                this.eventSupport.fireAfterResourceProcessed(changeSet, resource, ((InternalResource) resource).getResourceType(), ResourceStatus.RESOURCE_REMOVED);
            }

            this.eventSupport.fireAfterChangeSetProcessed(changeSet, changeSetState.addedResources, changeSetState.modifiedResourceMappings, changeSetState.removedResourceMappings);
        }
    }

    /**
     * Returns a ChangeSet based on a resource with a resource type of
     * ChangeSet.
     *
     * @param resource
     *            A resource with the type set to ChangeSet
     * @return A ChangeSet that can be processed by this Agent.
     */
    public ChangeSet getChangeSet(Resource resource) {
        if (this.semanticModules == null) {
            this.semanticModules = new SemanticModules();
            this.semanticModules.addSemanticModule(new ChangeSetSemanticModule());
        }

        XmlChangeSetReader reader = new XmlChangeSetReader(this.semanticModules);
        if (resource instanceof ClassPathResource) {
            reader.setClassLoader(((ClassPathResource) resource).getClassLoader(), null);
        } else {
            reader.setClassLoader(((AbstractRuleBase) (((KnowledgeBaseImpl) this.kbase).ruleBase)).getConfiguration().getClassLoader(), null);
        }

        ChangeSet changeSet = null;
        try {
            changeSet = reader.read(resource.getReader());
        } catch (Exception e) {
            this.listener.exception(new RuntimeException(
                    "Unable to parse ChangeSet", e));
        }
        if (changeSet == null) {
            this.listener.exception(new RuntimeException(
                    "Unable to parse ChangeSet"));
        }
        return changeSet;
    }

    /**
     * Keeps state information during the 'state' of a ChangeSet alteration so
     * past information can be kept along the way.
     *
     * @author Mark Proctor
     */
    public static class ChangeSetState {

        List<Resource> addedResources = new ArrayList<Resource>();
        /**
         * Map of removed definitions. The Set of kdefinitions is the original
         * definitions of the resource (before the deletion).
         */
        Map<Resource, Set<KnowledgeDefinition>> removedResourceMappings = new HashMap<Resource, Set<KnowledgeDefinition>>();
        /**
         * Map of modified definitions. The Set of kdefinitions is the original
         * definitions of the resource (before the modification).
         */
        Map<Resource, Set<KnowledgeDefinition>> modifiedResourceMappings = new HashMap<Resource, Set<KnowledgeDefinition>>();
        /**
         *Map of created Packages. The agent will create this packages when
         * processing added and modified resources
         */
        Map<Resource, KnowledgePackage> createdPackages = new LinkedHashMap<Resource, KnowledgePackage>();
        boolean scanDirectories;
        boolean incrementalBuild;
    }

    /**
     * Same as {@link #buildResourceMapping(org.drools.rule.Package, org.drools.io.Resource, boolean)
     *  buildResourceMapping(org.drools.rule.Package, org.drools.io.Resource, false)}.
     * If <code>resource</code> is null, this method does nothing.
     * @param pkg
     * @param resource
     */
    private void buildResourceMapping(Package pkg, Resource resource) {
        if (resource == null) {
            this.listener.warning("KnowledgeAgent: trying to build a resource map for a null resource!");
            return;
        }
        this.buildResourceMapping(pkg, resource, false);
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
    private void buildResourceMapping(Package pkg, Resource resource, boolean autoDiscoverResource) {

        synchronized (this.registeredResources) {
            if (!autoDiscoverResource && resource == null) {
                this.listener.warning("KnowledgeAgent: Impossible to map to a null resource! Use autoDiscoverResource = true ");
                return;
            }

            if (autoDiscoverResource && resource != null) {
                this.listener.warning("KnowledgeAgent: building resource map with resource set and autoDiscoverResource=true. Resource value wil be overwritten!");
            }

            for (Rule rule : pkg.getRules()) {
                if (resource != null && !((InternalResource) resource).hasURL()) {
                    this.listener.debug("KnowledgeAgent no resource mapped for rule="
                            + rule);
                }
                if (autoDiscoverResource) {
                    resource = rule.getResource();
                }

                this.addDefinitionMapping(resource, rule, true);
            }

            for (Process process : pkg.getRuleFlows().values()) {
                if (resource != null && !((InternalResource) resource).hasURL()) {
                    this.listener.debug("KnowledgeAgent no resource mapped for rule="
                            + process);
                }
                if (autoDiscoverResource) {
                    resource = ((org.drools.process.core.Process) process).getResource();
                }

                this.addDefinitionMapping(resource, process, true);
            }

            for (TypeDeclaration typeDeclaration : pkg.getTypeDeclarations().values()) {
                if (resource != null && !((InternalResource) resource).hasURL()) {
                    this.listener.debug("KnowledgeAgent no resource mapped for rule="
                            + typeDeclaration);
                }
                if (autoDiscoverResource) {
                    resource = typeDeclaration.getResource();
                }

                this.addDefinitionMapping(resource, typeDeclaration, true);
            }

            for (Function function : pkg.getFunctions().values()) {
                if (resource != null && !((InternalResource) resource).hasURL()) {
                    this.listener.debug("KnowledgeAgent no resource mapped for rule="
                            + function);
                }
                if (autoDiscoverResource) {
                    resource = function.getResource();
                }
                this.addDefinitionMapping(resource, function, true);
            }
        }
    }

    /**
     * This indexes the rules, flows, type declarations, etc against their
     * respective URLs if they have any, to allow more fine grained removal and
     * not just removing of an entire package
     */
    public void autoBuildResourceMapping() {
        this.listener.debug("KnowledgeAgent building resource map");
        synchronized (this.registeredResources) {
            RuleBase rbase = ((KnowledgeBaseImpl) this.kbase).ruleBase;

            for (Package pkg : rbase.getPackages()) {
                this.buildResourceMapping(pkg, null, true);
            }
        }
    }

    public KnowledgeBase getKnowledgeBase() {
        synchronized (this.registeredResources) {
            return this.kbase;
        }
    }

    public StatelessKnowledgeSession newStatelessKnowledgeSession() {
        return new StatelessKnowledgeSessionImpl(null, this, null);
    }

    public StatelessKnowledgeSession newStatelessKnowledgeSession(
            KnowledgeSessionConfiguration conf) {
        return new StatelessKnowledgeSessionImpl(null, this, conf);
    }

    public void resourcesChanged(ChangeSet changeSet) {
        try {
            this.listener.debug("KnowledgeAgent received ChangeSet changed notification");
            this.queue.put(changeSet);
        } catch (InterruptedException e) {
            this.listener.exception(new RuntimeException(
                    "KnowledgeAgent error while adding ChangeSet notification to queue",
                    e));
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
        this.listener.debug("KnowledgeAgent rebuilding KnowledgeBase using ChangeSet");
        synchronized (this.registeredResources) {

            /*
             * Do the following only if we are building a new instance,
             * otherwise, do an incremental build/update
             */
            if (this.newInstance) {
                rebuildResources(changeSetState);
            } else {
                incrementalBuildResources(changeSetState);
            }

            /*
             * If the ruleBase is sequential, after rebuilding or incremental
             * update, do an ordering of the ReteooBuilder
             */
            InternalRuleBase ruleBase = (InternalRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase;
            synchronized (ruleBase.getPackagesMap()) {
                if (ruleBase.getConfiguration().isSequential()) {
                    ruleBase.getReteooBuilder().order();
                }
            }
        }
        this.eventSupport.fireKnowledgeBaseUpdated(this.kbase);
        this.listener.debug("KnowledgeAgent finished rebuilding KnowledgeBase using ChangeSet");
    }

    /**
     * Same as {@link #createPackageFromResource(org.drools.io.Resource, org.drools.builder.KnowledgeBuilder)
     * createPackageFromResource(org.drools.io.Resource, null)}
     *
     * @param resource
     * @return
     * @see #createPackageFromResource(org.drools.io.Resource, org.drools.builder.KnowledgeBuilder) 
     */
    private KnowledgePackageImp createPackageFromResource(Resource resource) {
        return this.createPackageFromResource(resource, null);
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
    private KnowledgePackageImp createPackageFromResource(Resource resource, KnowledgeBuilder kbuilder) {

        if (kbuilder == null) {
            kbuilder = this.createKBuilder();
        }

        if (((InternalResource) resource).getResourceType() != ResourceType.PKG) {
            kbuilder.add(resource, ((InternalResource) resource).getResourceType());
            if (kbuilder.hasErrors()) {
                this.eventSupport.fireResourceCompilationFailed(kbuilder, resource, ((InternalResource) resource).getResourceType());
                this.listener.warning(
                        "KnowledgeAgent has KnowledgeBuilder errors ", kbuilder.getErrors());
            }
            if (kbuilder.getKnowledgePackages().iterator().hasNext()) {
                return (KnowledgePackageImp) kbuilder.getKnowledgePackages().iterator().next();
            }
            return null;
        } else {
            // .pks are handled as a special case.
            InputStream is = null;
            KnowledgePackageImp kpkg = null;
            try {
                is = resource.getInputStream();
                Object object = DroolsStreamUtils.streamIn(is);
                if (object instanceof KnowledgePackageImp) {
                    kpkg = ((KnowledgePackageImp) object);
                } else {
                    kpkg = new KnowledgePackageImp((Package) object);
                }
                for (Rule rule : kpkg.pkg.getRules()) {
                    rule.setResource(resource);
                }

            } catch (Exception ex) {
                this.listener.exception(new RuntimeException("KnowledgeAgent exception while trying to deserialize KnowledgeDefinitionsPackage  ", ex));
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    this.listener.exception(new RuntimeException("KnowledgeAgent exception while trying to close KnowledgeDefinitionsPackage  ", e));
                }
            }
            return kpkg;
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
        if (!this.newInstance) {
            listener.warning("KnowledgeAgent rebuilding KnowledgeBase when newInstance is false");
        }

        /*
         * Rebuild a new knowledge base. Try to use the old configuration if
         * possible
         */
        if (this.kbase != null) {
            this.kbase = KnowledgeBaseFactory.newKnowledgeBase(((InternalRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase).getConfiguration());
        } else {
            this.kbase = KnowledgeBaseFactory.newKnowledgeBase();
        }

        //puts all the resources as added in the changeSet.
        changeSetState.addedResources.clear();
        changeSetState.addedResources.addAll(this.registeredResources.getAllResources());
        addResourcesToKnowledgeBase(changeSetState);

        this.listener.info("KnowledgeAgent new KnowledgeBase now built and in use");
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
        if (this.newInstance) {
            this.listener.warning("KnowledgeAgent incremental build of KnowledgeBase when newInstance is true");
        }
        // Incrementally rebuild the resources
        synchronized (this.registeredResources) {
            this.listener.info("KnowledgeAgent performing an incremental build of the ChangeSet");

            // Create the knowledge base if one does not exist
            if (this.kbase == null) {
                this.kbase = KnowledgeBaseFactory.newKnowledgeBase();
            }

            // Remove all rules from the resources removed and also those
            // modified
            for (Map.Entry<Resource, Set<KnowledgeDefinition>> entry : changeSetState.removedResourceMappings.entrySet()) {
                for (KnowledgeDefinition kd : entry.getValue()) {
                    removeKnowledgeDefinitionFromBase(kd);
                }
            }

            for (Map.Entry<Resource, Set<KnowledgeDefinition>> entry : changeSetState.modifiedResourceMappings.entrySet()) {

                KnowledgePackageImp kpkg = createPackageFromResource(entry.getKey());

                if (kpkg == null) {
                    this.listener.warning("KnowledgeAgent: The resource didn't create any package: " + entry.getKey());
                    continue;
                }


                this.listener.debug("KnowledgeAgent: Diffing: " + entry.getKey());


                ResourceDiffProducer rdp = new BinaryResourceDiffProducerImpl();

                //we suppose that the package definition didn't change in the resource.
                //That's why we are serching the current package as
                //this.kbase.getKnowledgePackage(kpkg.getName())
                ResourceDiffResult diff = rdp.diff(entry.getValue(), kpkg, (KnowledgePackageImp) this.kbase.getKnowledgePackage(kpkg.getName()));

                for (KnowledgeDefinition kd : diff.getRemovedDefinitions()) {
                    this.listener.debug("KnowledgeAgent: Removing: " + kd);
                    removeKnowledgeDefinitionFromBase(kd);
                }

                //because all the mappings for "resource" were removed, we
                //need to map again the definitions that didn't change.
                //Those modified or added will be mapped in addResourcesToKnowledgeBase()
                for (KnowledgeDefinition knowledgeDefinition : diff.getUnmodifiedDefinitions()) {
                    this.addDefinitionMapping(entry.getKey(), knowledgeDefinition, false);
                }

                changeSetState.createdPackages.put(entry.getKey(), diff.getPkg());

            }

            /*
             * Compile the newly added resources
             */
            for (Resource resource : changeSetState.addedResources) {
                ///compile the new resource
                KnowledgePackageImp kpkg = createPackageFromResource(resource);
                if (kpkg == null) {
                    this.listener.warning("KnowledgeAgent: The resource didn't create any package: " + resource);
                    continue;
                }
                changeSetState.createdPackages.put(resource, kpkg);
            }

            //the added and modified resources were already processed and 
            //converted to createdPackages. We must clear the lists.
            changeSetState.addedResources.clear();
            changeSetState.modifiedResourceMappings.clear();

            addResourcesToKnowledgeBase(changeSetState);

        }
        this.listener.info("KnowledgeAgent incremental build of KnowledgeBase finished and in use");
    }

    /**
     * Removes a definition from {@link #kbase}.
     * @param kd the definition to be removed.
     */
    private void removeKnowledgeDefinitionFromBase(KnowledgeDefinition kd) {
        try {
            if (kd instanceof Rule) {
                Rule rule = (Rule) kd;
                this.listener.debug("KnowledgeAgent removing Rule=" + rule
                        + " from package=" + rule.getPackageName());
                this.kbase.removeRule(rule.getPackageName(), rule.getName());
            } else if (kd instanceof Process) {
                Process process = (Process) kd;
                this.listener.debug("KnowledgeAgent removing Process=" + process);
                this.kbase.removeProcess(process.getId());
            } else if (kd instanceof TypeDeclaration) {
                // @TODO Handle Type Declarations... is there a way to remove this?
            } else if (kd instanceof Function) {
                Function function = (Function) kd;
                this.kbase.removeFunction(function.getNamespace(), function.getName());
            }
        } catch (IllegalArgumentException e) {
            //it could be possible that a definition does not longer exists
            //in the kbase.
            this.listener.warning(e.getMessage());
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
     * created pacages list.
     */
    private void addResourcesToKnowledgeBase(ChangeSetState changeSetState) {

        KnowledgeBuilder kbuilder = this.createKBuilder();
        List<Package> packages = new ArrayList<Package>();


        for (Resource resource : changeSetState.addedResources) {
            KnowledgePackageImp createdPackage = this.createPackageFromResource(resource, kbuilder);
            changeSetState.createdPackages.put(resource, createdPackage);
        }

        //createPackageFromResource already log this
//        if (kbuilder.hasErrors()) {
//            this.listener.warning(
//                    "KnowledgeAgent has KnowledgeBuilder errors ", kbuilder.getErrors());
//        }

        for (Map.Entry<Resource, KnowledgePackage> entry : changeSetState.createdPackages.entrySet()) {
            // For PKG (.pks) just add them
            Resource resource = entry.getKey();
            this.listener.debug("KnowledgeAgent obtaining pkg resource="
                    + resource);

            try {
                Package pkg = ((KnowledgePackageImp) entry.getValue()).pkg;
                for (Rule rule : pkg.getRules()) {
                    rule.setResource(resource);
                }
                packages.add(pkg);

                this.buildResourceMapping(pkg, resource);
            } catch (Exception e) {
                this.listener.exception(new RuntimeException(
                        "KnowledgeAgent exception while trying to deserialize KnowledgeDefinitionsPackage  ",
                        e));
            }
        }

//        if (kbuilder
//                != null) {
//            // Log any errors we come across
//            if (kbuilder.hasErrors()) {
//                this.listener.warning(
//                        "KnowledgeAgent has KnowledgeBuilder errors ", kbuilder.getErrors());
//            }
//            this.listener.debug("KnowledgeAgent adding KnowledgePackages from KnowledgeBuilder");
//            this.kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
//        }
        /*
         * Add all the packages we found, but did not build, from the resources
         * now
         */
        for (Package pkg : packages) {
            this.listener.debug("KnowledgeAgent adding KnowledgeDefinitionsPackage "
                    + pkg.getName());
            ((KnowledgeBaseImpl) this.kbase).ruleBase.addPackage(pkg);
        }
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
        allResources.addAll(this.resourceDirectories);
        allResources.addAll(this.registeredResources.getAllResources());
        allResources.addAll(this.dslResources.keySet());


        //subscribe/unsubscribe from resources
        for (Resource resource : allResources) {
            if (monitor){
                this.listener.debug("KnowledgeAgent subscribing from resource="
                        + resource);
                this.notifier.subscribeResourceChangeListener(this, resource);
            }else{
                this.listener.debug("KnowledgeAgent unsubscribing from resource="
                        + resource);
                this.notifier.unsubscribeResourceChangeListener(this, resource);
            }
        }

        if (!monitor && this.changeSetNotificationDetector != null) {
            // we are running, but it wants to stop
            // this will stop the thread
            this.changeSetNotificationDetector.stop();
            this.thread.interrupt();
            this.changeSetNotificationDetector = null;
        } else if (monitor && this.changeSetNotificationDetector == null) {
            this.changeSetNotificationDetector = new ChangeSetNotificationDetector(
                    this, this.queue, this.listener);
            this.thread = new Thread(this.changeSetNotificationDetector);
            this.thread.start();
        }
    }

    /**
     *
     * @param resource
     * @param notify
     * @return
     */
    public boolean addResourceMapping(Resource resource, boolean notify) {
        boolean newMapping = this.registeredResources.createNewResourceEntry(resource);

        if (notify && newMapping) {
            this.listener.debug("KnowledgeAgent notifier subscribing to resource="
                    + resource);
            this.notifier.subscribeResourceChangeListener(this,
                    resource);
            return true;
        }
        return false;
    }

    /**
     * Add an resource/definition entry to registeredResources. Optionaly it
     * adds a listener to the resource added.
     * @param resource
     * @param definition
     * @param notify
     * @return if the resource/definition didn't exist in registeredResources.
     */
    public boolean addDefinitionMapping(Resource resource, KnowledgeDefinition definition, boolean notify) {

        if (resource == null) {
            listener.warning("KnowledgeAgent: impossible to add a map for a null resource! skiping.");
            return false;
        }

        this.listener.debug("KnowledgeAgent mapping resource="
                + resource + " to KnowledgeDefinition=" + definition);

        boolean isNewResource = this.registeredResources.isResourceMapped(resource);

        boolean isNewDefinition = true;

        if (definition != null) {
            isNewDefinition = this.registeredResources.putDefinition(resource, definition);
        }

        if (notify && isNewResource) {
            this.listener.debug("KnowledgeAgent notifier subscribing to resource="
                    + resource);

            this.notifier.subscribeResourceChangeListener(this,
                    resource);
        }

        return isNewDefinition;
    }

    public Set<KnowledgeDefinition> removeResourceMapping(Resource resource,
            boolean unsubscribe) {
        this.listener.debug("KnowledgeAgent removing mappings for resource="
                + resource + " with unsubscribe=" + unsubscribe);
        Set<KnowledgeDefinition> definitions = this.registeredResources.removeDefinitions(resource);




        if (definitions != null) {
            if (unsubscribe) {
                this.listener.debug("KnowledgeAgent notifier unsubscribing to resource="
                        + resource);

                this.notifier.unsubscribeResourceChangeListener(
                        this, resource);
            }
        }
        return definitions;

    }

    /**
     * A class to monitor and handle ChangeSets fired by the
     * ResourceChangeNotifier on a separate service (or process).
     *
     * @author Mark Proctor
     */
    public static class ChangeSetNotificationDetector implements Runnable {

        private LinkedBlockingQueue<ChangeSet> queue;
        private volatile boolean monitor;
        private KnowledgeAgentImpl kagent;
        private SystemEventListener listener;

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
            if (this.monitor) {
                this.listener.info("KnowledegAgent has started listening for ChangeSet notifications");
            }
            while (this.monitor) {
                Exception exception = null;
                try {
                    kagent.applyChangeSet(this.queue.take());
                } catch (InterruptedException e) {
                    exception = e;
                }
                Thread.yield();
                if (this.monitor && exception != null) {
                    this.listener.exception(new RuntimeException(
                            "KnowledgeAgent ChangeSet notification thread has been interrupted, but shutdown was not scheduled",
                            exception));
                }
            }

            this.listener.info("KnowledegAgent has stopped listening for ChangeSet notifications");
        }
    }

    private static class RegisteredResourceMap {

        private Map<Resource, Set<KnowledgeDefinition>> map = new HashMap<Resource, Set<KnowledgeDefinition>>();

        /**
         * Creates a new entry for resource with an empty Set<KnowledgeDefinition>.
         * If the map already contains an entry for the resource, then nothing
         * is changed.
         * @param resource
         * @return true if the resource was not previously mapped.
         */
        public boolean createNewResourceEntry(Resource resource) {
            if (!this.isResourceMapped(resource)) {
                this.map.put(resource, new HashSet<KnowledgeDefinition>());
                return true;
            }
            return false;
        }

        public boolean putDefinition(Resource resource, KnowledgeDefinition definition) {
            Set<KnowledgeDefinition> defList = map.get(resource);
            if (defList == null) {
                defList = new HashSet<KnowledgeDefinition>();
                this.map.put(resource, defList);
            }

            //support for lazy loading
            if (definition != null) {
                boolean isNew = defList.add(definition);
                return isNew;
            }

            return false;
        }

        public Set<KnowledgeDefinition> removeDefinitions(Resource resource) {
            return this.map.remove(resource);
        }

        public Set<KnowledgeDefinition> getDefinitions(Resource resource) {
            return this.getDefinitions(resource, false);
        }

        public Set<KnowledgeDefinition> getDefinitions(Resource resource, boolean returnEmptyIfNull) {
            Set<KnowledgeDefinition> definitions = this.map.get(resource);
            if (returnEmptyIfNull && definitions == null) {
                definitions = new HashSet<KnowledgeDefinition>();
            }
            return definitions;
        }

        public boolean isResourceMapped(Resource resource) {
            return this.map.containsKey(resource);
        }

        public Set<Resource> getAllResources() {
            return this.map.keySet();
        }
    }

    private KnowledgeBuilder createKBuilder() {
        KnowledgeBuilder kbuilder = null;
        if (this.builderConfiguration != null) {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(this.builderConfiguration);
        } else if (this.useKBaseClassLoaderForCompiling) {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, ((ReteooRuleBase) ((KnowledgeBaseImpl) this.getKnowledgeBase()).getRuleBase()).getRootClassLoader()));
        } else {
            kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        }

        if (this.dslResources != null) {
            for (Map.Entry<Resource, String> entry : this.dslResources.entrySet()) {
                kbuilder.add(ResourceFactory.newByteArrayResource(entry.getValue().getBytes()), ResourceType.DSL);
            }
        }

        return kbuilder;
    }

    private void retrieveDSLResource(Resource resource) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(resource.getReader());
        String line = null;
        StringBuilder content = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            content.append(line);
            content.append("\n");
        }

        this.dslResources.put(resource, content.toString());
    }

    public void addEventListener(KnowledgeAgentEventListener listener) {
        this.eventSupport.addEventListener(listener);
    }

    public void dispose() {
        synchronized (this.registeredResources) {
            //all kbase's ksessions must be disposed
            if (this.kbase != null) {
                Collection<StatefulKnowledgeSession> statefulSessions = this.kbase.getStatefulKnowledgeSessions();
                if (statefulSessions != null && statefulSessions.size() > 0){
                    String message = "The kbase still contains "+statefulSessions.size()+" stateful sessions. You must dispose them first.";
                    this.listener.warning(message);
                    throw new IllegalStateException(message);
                }
            }

            //stop changeSet Notification Detector
            this.monitorResourceChangeEvents(false);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        // users should turn off monitoring, but just in case when this class is
        // GC'd we turn off the thread
        if (this.changeSetNotificationDetector != null) {
            this.changeSetNotificationDetector.monitor = false;



        }
    }
}
