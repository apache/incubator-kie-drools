/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.hacep.core;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.kie.hacep.core.infra.election.KubernetesLockConfiguration;
import org.kie.hacep.core.infra.election.LeaderElection;
import org.kie.hacep.core.infra.election.LeaderElectionImpl;
import org.kie.hacep.core.infra.election.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreKube {

  private static final Logger logger = LoggerFactory.getLogger(CoreKube.class);
  private KubernetesClient kubernetesClient;
  private KubernetesLockConfiguration configuration;
  private LeaderElection leadership;

  public CoreKube(String namespace,
                  State initialState) {
    kubernetesClient = new DefaultKubernetesClient();
    configuration = createKubeConfiguration(namespace);
    leadership = new LeaderElectionImpl(kubernetesClient,
                                        configuration,
                                        initialState);
  }

  private KubernetesLockConfiguration createKubeConfiguration(String namespace) {
    String podName = System.getenv("POD_NAME");
    if (podName == null) {
      podName = System.getenv("HOSTNAME");
    }
    if (logger.isInfoEnabled()) {
      logger.info("PodName: {}",
                  podName);
    }
    KubernetesLockConfiguration newConfiguration = new KubernetesLockConfiguration(namespace);
    newConfiguration.setPodName(podName);
    return newConfiguration;
  }

  public LeaderElection getLeaderElection() {
    return leadership;
  }
}

