package org.kie.api.cluster;

import java.util.Collection;
import java.util.List;

import org.kie.api.internal.utils.KieService;

/**
 * Interface that allows to rise some awareness about the cluster environment
 *
 */
public interface ClusterAwareService extends KieService {

    String CLUSTER_NODES_KEY = "nodes";
    String CLUSTER_JOBS_KEY = "jobs";

    ClusterNode getThisNode();
    /**
     * Computes whether the jbpm engine instance is the coordinator therefore the one to 
     * provide singleton cluster active features.
     * @return whether this instance of jbpm engine is the one coordinating or not.
     */

    boolean isCoordinator();

    /**
     * Get all active members of the cluster
     */
    Collection<ClusterNode> getActiveClusterNodes();

    <T> void removeData(String key, String partition, T value);

    <T> void addData(String key, String partition, T value);

    <T> List<T> getData(String key);

    <T> List<T> getDataFromPartition(String key, String partition);

    /**
     * Add cluster listener for join and left members
     * @param listener
     */
    void addClusterListener(ClusterListener listener);

}
