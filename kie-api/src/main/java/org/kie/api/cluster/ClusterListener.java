package org.kie.api.cluster;

public interface ClusterListener {
    
    void nodeJoined(ClusterNode node);
    
    void nodeLeft(ClusterNode node);
    
}