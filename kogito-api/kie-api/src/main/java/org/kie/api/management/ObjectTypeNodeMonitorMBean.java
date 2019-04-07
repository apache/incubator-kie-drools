/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.kie.api.management;

/**
 * An interface for OTN MBean
 */
public interface ObjectTypeNodeMonitorMBean {

    /**
     * @return ID of the node
     */
    int getId();

    /**
     * @return entry point for the node
     */
    String getEntryPoint();

    /**
     * @return Object Type of the node
     */
    String getObjectType();

    /**
     * @return partition the node belongs to
     */
    String getPartitionId();

    /**
     * @return true if this node corresponds to an event type
     */
    boolean isEvent();

    /**
     * @return calculated expiration offset for this node in case it is an event.
     * -1 means it does not expire.
     */
    long getExpirationOffset();

}
