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

package org.drools.management;

/**
 * An interface for OTN MBean
 * 
 * @author etirelli
 */
public interface ObjectTypeNodeMonitorMBean {

    /**
     * The ID of the node
     * 
     * @return
     */
    public int getId();

    /**
     * The entry point for the node
     * 
     * @return
     */
    public String getEntryPoint();

    /**
     * The Object Type of the node
     * 
     * @return
     */
    public String getObjectType();

    /**
     * The partition the node belongs to
     * 
     * @return
     */
    public String getPartitionId();

    /**
     * True if this node corresponds to an event type
     * 
     * @return
     */
    public boolean isEvent();

    /**
     * The calculated expiration offset for this node
     * in case it is an event. -1 means it does not
     * expires.
     * 
     * @return
     */
    public long getExpirationOffset();

}
