/*
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

import org.drools.SystemEventListener;

/**
 * <p>
 * Subscribes and unsubscribes the given notifier to the requested resource. The Monitor will inform the notifier when when results it susbcribes to are changed
 * via the ChangeSet interface.
 * </p>
 * 
 * <p>
 * This interface, as well as ChangeSet, ResourceChangeNotifier, ResourceChangeListener and ResourceChangeScanner are still considered subject to change. 
 * Use the XML format change-set, as
 * part of the ResourceType api when adding to KnowledgeBuilder, which is considered stable. KnowledgeBuilder currently ignored Added/Modified xml elements,
 * the KnowledgeAgent will use them, when rebuilding the KnowledgeBase.
 * </p>
 */
public interface ResourceChangeMonitor {
    /**
     * Subscribe the notifier to receive change information on the given resource.
     * 
     * @param notifier
     * @param resource
     */
    void subscribeNotifier(ResourceChangeNotifier notifier,
                           Resource resource);

    /**
     * Unsubscribe the notifier for the given resource.
     * 
     * @param notifier
     * @param resource
     */
    void unsubscribeNotifier(ResourceChangeNotifier notifier,
                             Resource resource);

    public void setSystemEventListener(SystemEventListener listener);
}
