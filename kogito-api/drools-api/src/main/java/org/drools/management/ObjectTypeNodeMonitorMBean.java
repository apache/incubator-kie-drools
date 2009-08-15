package org.drools.management;

public interface ObjectTypeNodeMonitorMBean {

    public int getId();

    public String getEntryPoint();

    public String getObjectType();

    public String getPartitionId();

    public boolean isEvent();

    public long getExpirationOffset();

}