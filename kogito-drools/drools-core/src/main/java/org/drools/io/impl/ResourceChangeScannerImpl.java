package org.drools.io.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.drools.ChangeSet;
import org.drools.SystemEventListener;
import org.drools.SystemEventListenerFactory;
import org.drools.io.Resource;
import org.drools.io.ResourceChangeNotifier;
import org.drools.io.ResourceChangeScanner;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.internal.InternalResource;

public class ResourceChangeScannerImpl implements ResourceChangeScanner {

	private Map<Resource, Set<ResourceChangeNotifier>> resources;
	private Set<Resource> directories;
	private SystemEventListener listener;
	private int interval;

	public ResourceChangeScannerImpl() {
		this.listener = SystemEventListenerFactory.getSystemEventListener();
		this.resources = new HashMap<Resource, Set<ResourceChangeNotifier>>();
		this.directories = new HashSet<Resource>();
		this.interval = 60;
        this.listener.info( "ResourceChangeScanner created with default interval=60" );
	}

	public void setSystemEventListener(SystemEventListener listener) {
		this.listener = listener;
	}

	public void configure(ResourceChangeScannerConfiguration configuration) {
        this.interval = ((ResourceChangeScannerConfigurationImpl) configuration).getInterval();
        this.listener.info( "ResourceChangeScanner reconfigured with interval=" + getInterval() );

		// restart it if it's already running.
		if (this.scannerScheduler != null && this.scannerScheduler.isRunning()) {
			stop();
			start();
		}
	}

	public ResourceChangeScannerConfiguration newResourceChangeScannerConfiguration() {
		return new ResourceChangeScannerConfigurationImpl();
	}

	public ResourceChangeScannerConfiguration newResourceChangeScannerConfiguration(
			Properties properties) {
		return new ResourceChangeScannerConfigurationImpl(properties);
	}

	public void subscribeNotifier(ResourceChangeNotifier notifier,
			Resource resource) {
		synchronized (this.resources) {
			if (((InternalResource) resource).isDirectory()) {
				this.directories.add(resource);
			}
			Set<ResourceChangeNotifier> notifiers = this.resources
					.get(resource);
			if (notifiers == null) {
				notifiers = new HashSet<ResourceChangeNotifier>();
				this.resources.put(resource, notifiers);
			}
			this.listener.debug("ResourceChangeScanner subcribing notifier="
					+ notifier + " to resource=" + resource);
			notifiers.add(notifier);
		}
	}

	public void unsubscribeNotifier(ResourceChangeNotifier notifier,
			Resource resource) {
		synchronized (this.resources) {
			Set<ResourceChangeNotifier> notifiers = this.resources
					.get(resource);
			if (notifiers == null) {
				return;
			}
			this.listener.debug("ResourceChangeScanner unsubcribing notifier="
					+ notifier + " to resource=" + resource);
			notifiers.remove(notifier);
			if (notifiers.isEmpty()) {
				this.listener.debug("ResourceChangeScanner resource="
						+ resource + " now has no subscribers");
				this.resources.remove(resource);
				this.directories.remove(resource); // don't bother with
				// isDirectory check, as
				// doing a remove is
				// harmless if it doesn't
				// exist
			}
		}
	}

	public void scan() {
		this.listener.debug("ResourceChangeScanner attempt to scan "
				+ this.resources.size() + " resources");

		synchronized (this.resources) {
			Map<ResourceChangeNotifier, ChangeSet> notifications = new HashMap<ResourceChangeNotifier, ChangeSet>();

			List<Resource> removed = new ArrayList<Resource>();

			// detect modified and added
			for (Resource resource : this.directories) {
				this.listener.debug("ResourceChangeScanner scanning directory="
						+ resource);
				for (Resource child : ((InternalResource) resource)
						.listResources()) {
					if (((InternalResource) child).isDirectory()) {
						continue; // ignore sub directories
					}
					if (!this.resources.containsKey(child)) {

						this.listener
								.debug("ResourceChangeScanner new resource="
										+ child);
						// child is new
						((InternalResource) child)
								.setResourceType(((InternalResource) resource)
										.getResourceType());
						Set<ResourceChangeNotifier> notifiers = this.resources
								.get(resource); // get notifiers for this
						// directory
						for (ResourceChangeNotifier notifier : notifiers) {
							ChangeSetImpl changeSet = (ChangeSetImpl) notifications
									.get(notifier);
							if (changeSet == null) {
								// lazy initialise changeSet
								changeSet = new ChangeSetImpl();
								notifications.put(notifier, changeSet);
							}
							if (changeSet.getResourcesAdded().isEmpty()) {
								changeSet
										.setResourcesAdded(new ArrayList<Resource>());
							}
							changeSet.getResourcesAdded().add(child);
							notifier.subscribeChildResource(resource, child);
						}
					}
				}
			}

			for (Entry<Resource, Set<ResourceChangeNotifier>> entry : this.resources
					.entrySet()) {
				Resource resource = entry.getKey();
				Set<ResourceChangeNotifier> notifiers = entry.getValue();

				if (!((InternalResource) resource).isDirectory()) {
					// detect if Resource has been removed
					long lastModified = ((InternalResource) resource)
							.getLastModified();
					long lastRead = ((InternalResource) resource).getLastRead();
					if (lastModified == 0) {
						this.listener
								.debug("ResourceChangeScanner removed resource="
										+ resource);
						removed.add(resource);
						// resource is no longer present
						// iterate notifiers for this resource and add to each
						// removed
						for (ResourceChangeNotifier notifier : notifiers) {
							ChangeSetImpl changeSet = (ChangeSetImpl) notifications
									.get(notifier);
							if (changeSet == null) {
								// lazy initialise changeSet
								changeSet = new ChangeSetImpl();
								notifications.put(notifier, changeSet);
							}
							if (changeSet.getResourcesRemoved().isEmpty()) {
								changeSet
										.setResourcesRemoved(new ArrayList<Resource>());
							}
							changeSet.getResourcesRemoved().add(resource);
						}
					} else if (lastRead < lastModified && lastRead >= 0) {
						this.listener
								.debug("ResourceChangeScanner modified resource="
										+ resource
										+ " : "
										+ lastRead
										+ " : "
										+ lastModified);
						// it's modified
						// iterate notifiers for this resource and add to each
						// modified
						for (ResourceChangeNotifier notifier : notifiers) {
							ChangeSetImpl changeSet = (ChangeSetImpl) notifications
									.get(notifier);
							if (changeSet == null) {
								// lazy initialise changeSet
								changeSet = new ChangeSetImpl();
								notifications.put(notifier, changeSet);
							}
							if (changeSet.getResourcesModified().isEmpty()) {
								changeSet
										.setResourcesModified(new ArrayList<Resource>());
							}
							changeSet.getResourcesModified().add(resource);
						}
					}
				}
			}

			// now iterate and removed the removed resources, we do this so as
			// not to mutate the foreach loop while iterating
			for (Resource resource : removed) {
				this.resources.remove(resource);
			}

			for (Entry<ResourceChangeNotifier, ChangeSet> entry : notifications
					.entrySet()) {
				ResourceChangeNotifier notifier = entry.getKey();
				ChangeSet changeSet = entry.getValue();
				notifier.publishChangeSet(changeSet);
			}
		}
	}

	public void setInterval(int interval) {
		this.interval = interval;
		this.listener.info("ResourceChangeScanner reconfigured with interval="
				+ getInterval());

		if (this.scannerScheduler != null && this.scannerScheduler.isRunning()) {
			stop();
			start();
		}
	}

	public int getInterval() {
		return this.interval;
	}

	public void start() {
		this.scannerScheduler = new ProcessChangeSet(this.resources, this,
				this.listener, this.interval);
		thread = new Thread(this.scannerScheduler);
		thread.start();
	}

	public void stop() {
		if (this.scannerScheduler != null && this.scannerScheduler.isRunning()) {
			this.scannerScheduler.stop();
			this.thread.interrupt();
			this.scannerScheduler = null;
		}
	}

	public void reset() {
		this.resources.clear();
		this.directories.clear();
	}

	private Thread thread;
	private ProcessChangeSet scannerScheduler;

	public static class ProcessChangeSet implements Runnable {
		private volatile boolean scan;
		private ResourceChangeScannerImpl scanner;
		private long interval;
		private Map<Resource, Set<ResourceChangeNotifier>> resources;
		private SystemEventListener listener;

		ProcessChangeSet(Map<Resource, Set<ResourceChangeNotifier>> resources,
				ResourceChangeScannerImpl scanner,
				SystemEventListener listener, int interval) {
			this.resources = resources;
			this.scanner = scanner;
			this.listener = listener;
			this.interval = interval;
			this.scan = true;
		}

		public int getInterval() {
			return (int) this.interval;
		}

		public void stop() {
			this.scan = false;
		}

		public boolean isRunning() {
			return this.scan;
		}

		public void run() {
			synchronized (this) {
				if (this.scan) {
					this.listener
							.info("ResourceChangeNotification scanner has started");
				}
				while (this.scan) {
					Exception exception = null;
					// System.out.println( "BEFORE : sync this.resources" );
					synchronized (this.resources) {
						// System.out.println( "DURING : sync this.resources" );
						// lock the resources, as we don't want this modified
						// while processing
						this.scanner.scan();
					}
					// System.out.println( "AFTER : SCAN" );
					try {
						this.listener
								.debug("ResourceChangeScanner thread is waiting for "
										+ this.interval + " seconds.");
						wait(this.interval * 1000);
					} catch (InterruptedException e) {
						exception = e;
					}

					if (this.scan && exception != null) {
						this.listener
								.exception(new RuntimeException(
										"ResourceChangeNotification ChangeSet scanning thread was interrupted, but shutdown was not requested",
										exception));
					}
				}
				this.listener
						.info("ResourceChangeNotification scanner has stopped");
			}
		}
	}
}