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
