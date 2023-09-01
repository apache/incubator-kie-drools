/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
