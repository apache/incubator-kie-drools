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

package org.drools.core.management;

import org.drools.core.reteoo.ObjectTypeNode;
import org.kie.api.management.ObjectTypeNodeMonitorMBean;

/**
 * The monitor MBean for ObjectTypeNodes
 */
public class ObjectTypeNodeMonitor implements ObjectTypeNodeMonitorMBean  {
    
    private ObjectTypeNode node;

    public ObjectTypeNodeMonitor(ObjectTypeNode node) {
        this.node = node;
    }

    /* (non-Javadoc)
     * @see org.drools.core.management.ObjectTypeNodeMonitorMbean#getId()
     */
    public int getId() {
        return node.getId();
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.ObjectTypeNodeMonitorMbean#getEntryPoint()
     */
    public String getEntryPoint() {
        return node.getEntryPoint().toString();
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.ObjectTypeNodeMonitorMbean#getObjectType()
     */
    public String getObjectType() {
        return node.getObjectType().toString();
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.ObjectTypeNodeMonitorMbean#getPartitionId()
     */
    public String getPartitionId() {
        return node.getPartitionId().toString();
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.ObjectTypeNodeMonitorMbean#isEvent()
     */
    public boolean isEvent() {
        return node.getObjectType().isEvent();
    }
    
    /* (non-Javadoc)
     * @see org.drools.core.management.ObjectTypeNodeMonitorMbean#getExpirationOffset()
     */
    public long getExpirationOffset() {
        return node.getExpirationOffset();
    }
    
    public String getNameSufix() {
        char[] name = node.getEntryPoint().getEntryPointId().toCharArray();
        for( int i = 0; i < name.length; i++ ) {
            if( ! Character.isLetter( name[i] ) && name[i] != ' ' ) {
                name[i] = '_';
            }
        }
        return new String( name );
    }

}
