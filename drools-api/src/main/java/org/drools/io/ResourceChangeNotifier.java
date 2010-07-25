/**
 * Copyright 2010 JBoss Inc
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

package org.drools.io;

import java.util.Collection;

import org.drools.ChangeSet;
import org.drools.SystemEventListener;
import org.drools.event.io.ResourceChangeListener;

/**
 * <p>
 * ResourceChangeListeners can subscribe to the notifier to receive ChangeSet info when results they are subscribed to change. The ResourceChangeNotifier
 * itself is not monitoring resources for changes, instead it delegates to added ResourceChangeMonitor implementations. When a ResourceChangeListener
 * subscribes to a Resource that subscription is delegated to the added monitors.
 * </p>
 * 
 * <p>
 * This interface, as well as ChangeSet, ResourceChangeMonitor, esourceChangeListener and ResourceChangeScanner are still considered subject to change.
 * Use the XML format change-set, as
 * part of the ResourceType api when adding to KnowledgeBuilder, which is considered stable. KnowledgeBuilder currently ignored Added/Modified xml elements,
 * the KnowledgeAgent will use them, when rebuilding the KnowledgeBase.
 * </p>
 * @BETA
 * 
 */
public interface ResourceChangeNotifier {
    void subscribeResourceChangeListener(ResourceChangeListener listener,
                                         Resource resource);

    void unsubscribeResourceChangeListener(ResourceChangeListener listener,
                                           Resource resource);

    /**
     * When a ResourceChangeMonitor is asked to monitor a directory, it needs a way to tell the ResourceChangeNotifier of any newly added Resources.
     * 
     * @param directory
     *     The parent directory the discovered child is in.
     * @param child
     *     The discovered child resource
     */
    void subscribeChildResource(Resource directory,
                                Resource child);

    /** 
     * Add a ResourceChangeMonitor, which will receive all Resource subscriptions.
     * 
     * @param monitor
     */
    void addResourceChangeMonitor(ResourceChangeMonitor monitor);

    /**
     * Remove a ResourceChangeMonitor.
     * 
     * @param monitor
     */
    void removeResourceChangeMonitor(ResourceChangeMonitor monitor);

    /**
     * Return a collection of the added ResourceChangeMonitors
     * @return
     */
    Collection<ResourceChangeMonitor> getResourceChangeMonitors();

    /**
     * Called by the added ResourceChangeMonitors to inform this ResourceChangeNotifier of resource changes.
     * The ResourceChangeMontior must have a reference to the ResourceChangeNotifiers they are added to,
     * 
     * @param changeSet
     */
    void publishChangeSet(ChangeSet changeSet);

    public void setSystemEventListener(SystemEventListener listener);

    /**
     * Start the service, this will create a new Thread.
     */
    void start();

    /**
     * Stop the service.
     */
    void stop();
}
