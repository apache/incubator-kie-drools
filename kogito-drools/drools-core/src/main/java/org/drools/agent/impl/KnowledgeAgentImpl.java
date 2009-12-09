package org.drools.agent.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalRuleBase;
import org.drools.definition.KnowledgeDefinition;
import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.event.io.ResourceChangeListener;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.impl.StatelessKnowledgeSessionImpl;
import org.drools.io.InternalResource;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ClassPathResource;
import org.drools.io.impl.ResourceChangeNotifierImpl;
import org.drools.rule.Function;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.util.DroolsStreamUtils;
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
	private ResourceMap resourcesMap;
	private Set<Resource> resourceDirectories;
	private KnowledgeBase kbase;
	private ResourceChangeNotifierImpl notifier;
	private boolean newInstance;
	private SystemEventListener listener;
	private boolean scanDirectories;
	private LinkedBlockingQueue<ChangeSet> queue;
	private Thread thread;
	private ChangeSetNotificationDetector changeSetNotificationDetector;
	private SemanticModules semanticModules;

	/**
	 * Default constructor for KnowledgeAgentImpl
	 *
	 * @param name
	 * @param kbase
	 * @param configuration
	 */
	public KnowledgeAgentImpl(String name, KnowledgeBase kbase,
			KnowledgeAgentConfiguration configuration) {
		this.name = name;
		this.kbase = kbase;
		this.resourcesMap = new ResourceMap(this);
		this.resourceDirectories = new HashSet<Resource>();
		// this.listener = listener;
		this.listener = SystemEventListenerFactory.getSystemEventListener();
		this.queue = new LinkedBlockingQueue<ChangeSet>();
		boolean scanResources = false;
		boolean monitor = false;
		if (configuration != null) {
			// New Instance describes if we do incremental builds or not
			this.newInstance = ((KnowledgeAgentConfigurationImpl) configuration)
					.isNewInstance();
			this.notifier = (ResourceChangeNotifierImpl) ResourceFactory
					.getResourceChangeNotifierService();
			if (((KnowledgeAgentConfigurationImpl) configuration)
					.isMonitorChangeSetEvents()) {
				monitor = true;
			}

			if (((KnowledgeAgentConfigurationImpl) configuration)
					.isScanDirectories()) {
				this.scanDirectories = true;
			}

			scanResources = ((KnowledgeAgentConfigurationImpl) configuration)
					.isScanResources();
			if (scanResources) {
				this.notifier.addResourceChangeMonitor(ResourceFactory
						.getResourceChangeScannerService());
				monitor = true; // if scanning, monitor must be true;
			}
		}

		monitorResourceChangeEvents(monitor);

		buildResourceMapping();

		this.listener
				.info("KnowledgeAgent created, with configuration:\nmonitorChangeSetEvents="
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

	public void applyChangeSet(Resource resource) {
		applyChangeSet(getChangeSet(resource));
	}

	public void applyChangeSet(ChangeSet changeSet) {
		synchronized (this.resourcesMap) {
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
			buildResourceMapping();
		}
	}

	public void processChangeSet(Resource resource,
			ChangeSetState changeSetState) {
		processChangeSet(getChangeSet(resource), changeSetState);
	}

	public void processChangeSet(ChangeSet changeSet,
			ChangeSetState changeSetState) {
		synchronized (this.resourcesMap) {
			/*
			 * Process the added resources from a ChangeSet by subscribing to
			 * the notifier and inserting a new ResourceMapping.
			 */
			for (Resource resource : changeSet.getResourcesAdded()) {
				if (((InternalResource) resource).getResourceType() == ResourceType.CHANGE_SET) {
					// @TODO We should not ignore an added change set
					this.listener
							.debug("KnowledgeAgent processing sub ChangeSet="
									+ resource);
					processChangeSet(resource, changeSetState);
				} else if (((InternalResource) resource).isDirectory()) {
					this.resourceDirectories.add(resource);
					this.listener
							.debug("KnowledgeAgent subscribing to directory="
									+ resource);
					this.notifier.subscribeResourceChangeListener(this,
							resource);
					// if it's a dir, subscribe it's children first
					for (Resource child : ((InternalResource) resource)
							.listResources()) {

						// ignore sub directories
						if (((InternalResource) child).isDirectory()) {
							continue;
						}

						((InternalResource) child)
								.setResourceType(((InternalResource) resource)
										.getResourceType());
						if (this.resourcesMap.addResourceMapping(child, true)
								&& changeSetState.incrementalBuild) {
							changeSetState.addedResources.add(child);
						}
					}
				} else {
					if (this.resourcesMap.addResourceMapping(resource, true)
							&& changeSetState.incrementalBuild) {
						changeSetState.addedResources.add(resource);
					}
				}
			}

			/*
			 * For those marked as removed by the ChangeSet, remove their
			 * mappings, index them if we are doing incremental builds so the
			 * incremental building process knows what to remove.
			 */
			for (Resource resource : changeSet.getResourcesRemoved()) {
				if (((InternalResource) resource).getResourceType() == ResourceType.CHANGE_SET) {
					// @TODO Is this true? Shouldn't we just ignore it in
					// removed?
					processChangeSet(resource, changeSetState);
				} else if (changeSetState.scanDirectories
						&& ((InternalResource) resource).isDirectory()) {
					this.listener
							.debug("KnowledgeAgent unsubscribing from directory resource="
									+ resource);
					this.resourceDirectories.remove(resource);
					this.notifier.unsubscribeResourceChangeListener(this,
							resource);
				} else {
					ResourceMapEntry removedEntry = this.resourcesMap
							.removeResourceMapping(resource, true);

					if (removedEntry != null && changeSetState.incrementalBuild) {
						changeSetState.removedResourceMappings
								.add(removedEntry);
					}
				}
			}

			/*
			 * For those marked as modified, remove their ResourceMapping,
			 * attach it to the ChangeSetState, and add a new one - it will be
			 * repopulated with the KnowledgeDefinitions later after rebuilding.
			 * Process any modified ChangeSets - treat them as if they were new.
			 */
			for (Resource resource : changeSet.getResourcesModified()) {
				if (((InternalResource) resource).getResourceType() == ResourceType.CHANGE_SET) {
					// processChangeSet(resource, changeSetState);
					continue;
				} else if (((InternalResource) resource).isDirectory()) {
					if (this.resourceDirectories.add(resource)) {
						this.listener
								.warning("KnowledgeAgent is subscribing to a modified directory="
										+ resource
										+ " when it should have already been subscribed");
						this.notifier.subscribeResourceChangeListener(this,
								resource);
					}
					// if it's a dir, subscribe it's children first
					for (Resource child : ((InternalResource) resource)
							.listResources()) {

						// ignore sub directories
						if (((InternalResource) child).isDirectory()) {
							continue;
						}

						if (this.resourcesMap.addResourceMapping(child, true)) {
							((InternalResource) child)
									.setResourceType(((InternalResource) resource)
											.getResourceType());
							if (changeSetState.incrementalBuild) {
								changeSetState.addedResources.add(child);
							}
						}
					}
				} else {
					ResourceMapEntry modifiedMapping = this.resourcesMap
							.removeResourceMapping(resource, false);
					if (modifiedMapping == null) {
						this.listener
								.warning("KnowledgeAgent subscribing to new resource="
										+ resource
										+ ", though it was marked as modified.");
						this.resourcesMap.addResourceMapping(resource, true);
						if (changeSetState.incrementalBuild) {
							changeSetState.addedResources.add(resource);
						}
					} else {
						/*
						 * Put a new one, but no need to subscribe or update
						 * since this will be done in the buildResourceMapping
						 * later
						 */
						this.resourcesMap.addResourceMapping(resource, false);
						if (changeSetState.incrementalBuild) {
							changeSetState.modifiedResourceMappings
									.add(modifiedMapping);
						}
					}
				}
			}
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
			this.semanticModules
					.addSemanticModule(new ChangeSetSemanticModule());
		}

		XmlChangeSetReader reader = new XmlChangeSetReader(this.semanticModules);
		if (resource instanceof ClassPathResource) {
			reader.setClassLoader(((ClassPathResource) resource)
					.getClassLoader());
		} else {
			reader
					.setClassLoader(((AbstractRuleBase) (((KnowledgeBaseImpl) this.kbase).ruleBase))
							.getConfiguration().getClassLoader());
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
		List<ResourceMapEntry> removedResourceMappings = new ArrayList<ResourceMapEntry>();
		List<ResourceMapEntry> modifiedResourceMappings = new ArrayList<ResourceMapEntry>();
		boolean scanDirectories;
		boolean incrementalBuild;
	}

	/**
	 * This indexes the rules, flows, type declarations, etc against their
	 * respective URLs if they have any, to allow more fine grained removal and
	 * not just removing of an entire package
	 */
	public void buildResourceMapping() {
		this.listener.debug("KnowledgeAgent building resource map");
		synchronized (this.resourcesMap) {
			RuleBase rbase = ((KnowledgeBaseImpl) this.kbase).ruleBase;
			/*
			 * Iterate each package for the different types of
			 * KnowledgeDefinitions we want to track
			 */
			for (Package pkg : rbase.getPackages()) {
				for (Rule rule : pkg.getRules()) {
					Resource resource = rule.getResource();
					if (resource == null
							|| !((InternalResource) resource).hasURL()) {
						this.listener
								.debug("KnowledgeAgent no resource mapped for rule="
										+ rule);
						continue;
					}
					this.resourcesMap.putResourceMappingEntry(resource, rule);
				}

				for (Process process : pkg.getRuleFlows().values()) {
					Resource resource = ((org.drools.process.core.Process) process)
							.getResource();
					if (resource == null
							|| !((InternalResource) resource).hasURL()) {
						this.listener
								.debug("KnowledgeAgent no resource mapped for process="
										+ process);
						continue;
					}
					this.resourcesMap
							.putResourceMappingEntry(resource, process);
				}

				for (TypeDeclaration typeDeclaration : pkg
						.getTypeDeclarations().values()) {
					Resource resource = typeDeclaration.getResource();
					if (resource == null
							|| !((InternalResource) resource).hasURL()) {
						this.listener
								.debug("KnowledgeAgent no resource mapped for typeDeclaration="
										+ typeDeclaration);
						continue;
					}
					this.resourcesMap.putResourceMappingEntry(resource,
							typeDeclaration);
				}

				for (Function function : pkg.getFunctions().values()) {
					Resource resource = function.getResource();
					if (resource == null
							|| !((InternalResource) resource).hasURL()) {
						this.listener
								.debug("KnowledgeAgent no resource mapped for function="
										+ function);
						continue;
					}
					this.resourcesMap.putResourceMappingEntry(resource,
							function);
				}
			}
		}
	}

	public KnowledgeBase getKnowledgeBase() {
		synchronized (this.resourcesMap) {
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
			this.listener
					.debug("KnowledgeAgent received ChangeSet changed notification");
			this.queue.put(changeSet);
		} catch (InterruptedException e) {
			this.listener
					.exception(new RuntimeException(
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
		this.listener
				.debug("KnowledgeAgent rebuilding KnowledgeBase using ChangeSet");
		synchronized (this.resourcesMap) {

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
		this.listener
				.debug("KnowledgeAgent finished rebuilding KnowledgeBase using ChangeSet");
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
			listener
					.warning("KnowledgeAgent rebuilding KnowledgeBase when newInstance is false");
		}

		/*
		 * Rebuild a new knowledge base. Try to use the old configuration if
		 * possible
		 */
		if (this.kbase != null) {
			this.kbase = KnowledgeBaseFactory
					.newKnowledgeBase(((InternalRuleBase) ((KnowledgeBaseImpl) this.kbase).ruleBase)
							.getConfiguration());
		} else {
			this.kbase = KnowledgeBaseFactory.newKnowledgeBase();
		}

		addResourcesToKnowledgeBase(resourcesMap.getAllResources());

		this.listener
				.info("KnowledgeAgent new KnowledgeBase now built and in use");
	}

	/**
	 * This method is meant to incrementally build or update the current
	 * KnowledgeBase.
	 *
	 * @param changeSet
	 * @param changeSetState
	 */
	private void incrementalBuildResources(ChangeSetState changeSetState) {
		if (this.newInstance) {
			this.listener
					.warning("KnowledgeAgent incremental build of KnowledgeBase when newInstance is true");
		}
		// Incrementally rebuild the resources
		synchronized (this.resourcesMap) {
			this.listener
					.info("KnowledgeAgent performing an incremental build of the ChangeSet");

			// Create the knowledge base if one does not exist
			if (this.kbase == null) {
				this.kbase = KnowledgeBaseFactory.newKnowledgeBase();
			}

			// Remove all rules from the resources removed and also those
			// modified
			for (ResourceMapEntry resourceMapEntry : changeSetState.removedResourceMappings) {
				for (KnowledgeDefinition kd : resourceMapEntry
						.getKnowledgeDefinitions()) {
					removeKnowledgeDefinitionFromBase(kd);
				}
			}

			for (ResourceMapEntry resourceMapEntry : changeSetState.modifiedResourceMappings) {
				for (KnowledgeDefinition kd : resourceMapEntry
						.getKnowledgeDefinitions()) {
					removeKnowledgeDefinitionFromBase(kd);
				}
				changeSetState.addedResources.add(resourceMapEntry
						.getResource());
			}

			/*
			 * Adds both the newly added resources and the modified resources
			 */
			addResourcesToKnowledgeBase(changeSetState.addedResources);
		}
		this.listener
				.info("KnowledgeAgent incremental build of KnowledgeBase finished and in use");
	}

	/**
	 *
	 * @param kd
	 */
	private void removeKnowledgeDefinitionFromBase(KnowledgeDefinition kd) {
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
			// @TODO functions and type declarations
		}
	}

	/**
	 * Adds the resources to the current KnowledgeBase on this KnowledgeAgent.
	 * Only uses the KnowledgeBuilder when necessary.
	 *
	 * @param resources
	 */
	private void addResourcesToKnowledgeBase(Collection<Resource> resources) {

		KnowledgeBuilder kbuilder = null;
		List<Package> packages = new ArrayList<Package>();
		for (Resource resource : resources) {
			/*
			 * If it's not a PKG, clearly we need the knowledge builder, so
			 * build it
			 */
			if (((InternalResource) resource).getResourceType() != ResourceType.PKG) {
				this.listener.debug("KnowledgeAgent building resource="
						+ resource);
				if (kbuilder == null) {
					kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
				}
				kbuilder.add(resource, ((InternalResource) resource)
						.getResourceType());
			} else {
				// For PKG (.pks) just add them
				this.listener.debug("KnowledgeAgent obtaining pkg resource="
						+ resource);

				InputStream is = null;
				try {
					// .pks are handled as a special case.
					is = resource.getInputStream();
					Object object = DroolsStreamUtils.streamIn(is);
					Package pkg = null;
					if (object instanceof KnowledgePackage) {
						pkg = ((KnowledgePackageImp) object).pkg;

					} else {
						pkg = (Package) object;
					}
					for (Rule rule : pkg.getRules()) {
						rule.setResource(resource);
					}

					packages.add(pkg);
				} catch (Exception e) {
					this.listener
							.exception(new RuntimeException(
									"KnowledgeAgent exception while trying to deserialize KnowledgeDefinitionsPackage  ",
									e));
				} finally {
					try {
						if (is != null) {
							is.close();
						}
					} catch (IOException e) {
						this.listener
								.exception(new RuntimeException(
										"KnowledgeAgent exception while trying to close KnowledgeDefinitionsPackage  ",
										e));
					}
				}
			}
		}

		if (kbuilder != null) {
			// Log any errors we come across
			if (kbuilder.hasErrors()) {
				this.listener.warning(
						"KnowledgeAgent has KnowledgeBuilder errors ", kbuilder
								.getErrors());
			}
			this.listener
					.debug("KnowledgeAgent adding KnowledgePackages from KnowledgeBuilder");
			this.kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		}
		/*
		 * Add all the packages we found, but did not build, from the resources
		 * now
		 */
		for (Package pkg : packages) {
			this.listener
					.debug("KnowledgeAgent adding KnowledgeDefinitionsPackage "
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
				this.listener
						.info("KnowledegAgent has started listening for ChangeSet notifications");
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
					this.listener
							.exception(new RuntimeException(
									"KnowledgeAgent ChangeSet notification thread has been interrupted, but shutdown was not scheduled",
									exception));
				}
			}

			this.listener
					.info("KnowledegAgent has stopped listening for ChangeSet notifications");
		}
	}

	/**
	 * Maps a set of KnowledgeDefinitions to the resource that created them so
	 * we can perform incremental building of a KnowledgeBase.
	 *
	 * @author Mark Proctor
	 */
	public static class ResourceMapEntry {
		private final Resource resource;
		private Set<KnowledgeDefinition> knowledgeDefinitions;

		public ResourceMapEntry(Resource resource) {
			this.resource = resource;
			this.knowledgeDefinitions = new HashSet<KnowledgeDefinition>();
		}

		public Resource getResource() {
			return resource;
		}

		public Set<KnowledgeDefinition> getKnowledgeDefinitions() {
			return knowledgeDefinitions;
		}
	}

	/**
	 * A Bidirectional map of Resources to KnowledgeDefinitions.This allows you
	 * to go both ways due to a KnowledgeDefinition being able to be overwritten
	 * by multiple resources, and we only want to track the Resource responsible
	 * as tagged by the KnowledgeBase for creating that resource.
	 *
	 * @author Sam Romano
	 */
	public static class ResourceMap {
		private final KnowledgeAgentImpl agent;
		private Map<Resource, ResourceMapEntry> resourceMappings;
		private Map<KnowledgeDefinition, Resource> knowledgeDefinitionMappings;

		public ResourceMap(KnowledgeAgentImpl agent) {
			this.agent = agent;
			this.resourceMappings = new HashMap<Resource, ResourceMapEntry>();
			this.knowledgeDefinitionMappings = new HashMap<KnowledgeDefinition, Resource>();
		}

		/**
		 * @param resource
		 *            The resource to add to the Map
		 * @param notify
		 *            True if you want to notify the listener, false otherwise.
		 * @return True if it was added, false otherwise.
		 */
		public boolean addResourceMapping(Resource resource, boolean notify) {
			ResourceMapEntry rsrcMapping = this.resourceMappings.get(resource);
			if (rsrcMapping == null) {
				rsrcMapping = new ResourceMapEntry(resource);
				this.resourceMappings.put(resource, rsrcMapping);
				if (notify) {
					this.agent.listener
							.debug("KnowledgeAgent notifier subscribing to resource="
									+ resource);

					this.agent.notifier.subscribeResourceChangeListener(agent,
							resource);
				}
				return true;
			}
			return false;
		}

		/**
		 * Returns the old ResourceMapping mapped to the Resource. If it finds a
		 * resource mapping, it will unsubscribe from the
		 * ResourceChangeListener.
		 *
		 * @param resource
		 * @param unsubscribe
		 *            True if you want to unsubscribe on a successful removal,
		 *            false otherwise... normally false on attempting to remove
		 *            entries for Modifications
		 * @return The old resourceMapping with the KnowledgeDefinitions that it
		 *         use to have
		 */
		public ResourceMapEntry removeResourceMapping(Resource resource,
				boolean unsubscribe) {
			this.agent.listener
					.debug("KnowledgeAgent removing mappings for resource="
							+ resource + " with unsubscribe=" + unsubscribe);
			ResourceMapEntry rsrcMapping = this.resourceMappings
					.remove(resource);
			if (rsrcMapping != null) {
				if (unsubscribe) {
					this.agent.listener
							.debug("KnowledgeAgent notifier unsubscribing to resource="
									+ resource);
					this.agent.notifier.unsubscribeResourceChangeListener(
							agent, resource);
				}

				for (KnowledgeDefinition kd : rsrcMapping.knowledgeDefinitions) {
					this.knowledgeDefinitionMappings.remove(kd);
				}
			}
			return rsrcMapping;
		}

		public Set<Resource> getAllResources() {
			return this.resourceMappings.keySet();
		}

		/**
		 * Maps a resource to the knowledge Definition, and vice versa for
		 * bidirectional mapping and integrity. If the resource is not mapped at
		 * all, this will subscribe the agent specified to this ResourceMap to
		 * listen for those changes.
		 *
		 * @param resource
		 * @param kd
		 * @return The old resource the KnowledgeDefinition use to be mapped to
		 *         if it was
		 */
		public Resource putResourceMappingEntry(Resource resource,
				KnowledgeDefinition kd) {
			ResourceMapEntry rsrcMapping = this.resourceMappings.get(resource);
			if (rsrcMapping == null) {
				addResourceMapping(resource, true);
				rsrcMapping = this.resourceMappings.get(resource);
			}
			/*
			 * If adding this returns true, then we need to map it the other
			 * way, otherwise don't bother with the bidirectional logic and
			 * waste time - essentially we know we mapped it before.
			 */
			if (rsrcMapping.knowledgeDefinitions.add(kd)) {
				this.agent.listener.debug("KnowledgeAgent mapping resource="
						+ resource + " to KnowledgeDefinition=" + kd);

				Resource oldRsrc = this.knowledgeDefinitionMappings.put(kd,
						resource);
				/*
				 * If an oldRsrc exists, make sure we remove the kd from that
				 * mapping - but dont unsubscribe from it as the resource is
				 * still being compiled in the KnowledgeBase, we need to know of
				 * its updates
				 */
				ResourceMapEntry oldRsrcMapping = this.resourceMappings
						.get(oldRsrc);
				if (oldRsrcMapping != null) {
					this.agent.listener
							.debug("KnowledgeAgent removing reference from resource="
									+ oldRsrc + " to KnowledgeDefinition=" + kd);
					oldRsrcMapping.getKnowledgeDefinitions().remove(kd);
				}

				return oldRsrc;
			}
			return null;
		}

		public boolean removeResourceMappingEntry(Resource resource,
				KnowledgeDefinition kd) {
			ResourceMapEntry rsrcMapping = this.resourceMappings.get(resource);
			if (rsrcMapping != null) {
				/*
				 * If the above didn't remove the kd, then we don't do the
				 * bidirectional removal
				 */
				if (rsrcMapping.getKnowledgeDefinitions().remove(kd)) {
					this.knowledgeDefinitionMappings.remove(kd);
					return true;
				}
			}
			return false;
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