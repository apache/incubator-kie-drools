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
package org.kie.hacep.core.infra.election;

import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.client.KubernetesClient;

/**
 * from org.apache.camel.component.kubernetes.cluster.lock
 * Configuration for Kubernetes Lock.
 */
public class KubernetesLockConfiguration implements Cloneable {

    public static final String DEFAULT_CONFIGMAP_NAME = "leaders";

    public static final double DEFAULT_JITTER_FACTOR = 1.2;
    public static final long DEFAULT_LEASE_DURATION_MILLIS = 30000;
    public static final long DEFAULT_RENEW_DEADLINE_MILLIS = 20000;
    public static final long DEFAULT_RETRY_PERIOD_MILLIS = 5000;

    /**
     * Kubernetes namespace containing the pods and the ConfigMap used for locking.
     */
    private String kubernetesResourcesNamespace;

    /**
     * Name of the ConfigMap used for locking.
     */
    private String configMapName ;

    /**
     * Name of the lock group (or namespace according to the Camel cluster convention) within the chosen ConfigMap.
     */
    private String groupName;

    /**
     * Name of the current pod (defaults to host name).
     */
    private String podName;

    /**
     * Labels used to identify the members of the cluster.
     */
    private Map<String, String> clusterLabels = new HashMap<>();

    /**
     * A jitter factor to apply in order to prevent all pods to call Kubernetes APIs in the same instant.
     */
    private double jitterFactor = DEFAULT_JITTER_FACTOR;

    /**
     * The default duration of the lease for the current leader.
     */
    private long leaseDurationMillis = DEFAULT_LEASE_DURATION_MILLIS;

    /**
     * The deadline after which the leader must stop its services because it may have lost the leadership.
     */
    private long renewDeadlineMillis = DEFAULT_RENEW_DEADLINE_MILLIS;

    /**
     * The time between two subsequent attempts to check and acquire the leadership.
     * It is randomized using the jitter factor.
     */
    private long retryPeriodMillis = DEFAULT_RETRY_PERIOD_MILLIS;

    public KubernetesLockConfiguration(String namespace) {
        configMapName = namespace + "-"+ DEFAULT_CONFIGMAP_NAME;
    }

    public String getKubernetesResourcesNamespaceOrDefault(KubernetesClient kubernetesClient) {
        if (kubernetesResourcesNamespace != null) {
            return kubernetesResourcesNamespace;
        }
        return kubernetesClient.getNamespace();
    }

    public String getKubernetesResourcesNamespace() {
        return kubernetesResourcesNamespace;
    }

    public void setKubernetesResourcesNamespace(String kubernetesResourcesNamespace) {
        this.kubernetesResourcesNamespace = kubernetesResourcesNamespace;
    }

    public String getConfigMapName() {
        return configMapName;
    }

    public void setConfigMapName(String configMapName) {
        this.configMapName = configMapName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getPodName() {
        return podName;
    }

    public void setPodName(String podName) {
        this.podName = podName;
    }

    public Map<String, String> getClusterLabels() {
        return clusterLabels;
    }

    public void setClusterLabels(Map<String, String> clusterLabels) {
        this.clusterLabels = clusterLabels;
    }

    public void addToClusterLabels(String key,
                                   String value) {
        this.clusterLabels.put(key,
                               value);
    }

    public double getJitterFactor() {
        return jitterFactor;
    }

    public void setJitterFactor(double jitterFactor) {
        this.jitterFactor = jitterFactor;
    }

    public long getLeaseDurationMillis() {
        return leaseDurationMillis;
    }

    public void setLeaseDurationMillis(long leaseDurationMillis) {
        this.leaseDurationMillis = leaseDurationMillis;
    }

    public long getRenewDeadlineMillis() {
        return renewDeadlineMillis;
    }

    public void setRenewDeadlineMillis(long renewDeadlineMillis) {
        this.renewDeadlineMillis = renewDeadlineMillis;
    }

    public long getRetryPeriodMillis() {
        return retryPeriodMillis;
    }

    public void setRetryPeriodMillis(long retryPeriodMillis) {
        this.retryPeriodMillis = retryPeriodMillis;
    }

    public KubernetesLockConfiguration copy() {
        try {
            KubernetesLockConfiguration copy = (KubernetesLockConfiguration) this.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Cannot clone",
                                            e);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("KubernetesLockConfiguration{");
        sb.append("kubernetesResourcesNamespace='").append(kubernetesResourcesNamespace).append('\'');
        sb.append(", configMapName='").append(configMapName).append('\'');
        sb.append(", groupName='").append(groupName).append('\'');
        sb.append(", podName='").append(podName).append('\'');
        sb.append(", clusterLabels=").append(clusterLabels);
        sb.append(", jitterFactor=").append(jitterFactor);
        sb.append(", leaseDurationMillis=").append(leaseDurationMillis);
        sb.append(", renewDeadlineMillis=").append(renewDeadlineMillis);
        sb.append(", retryPeriodMillis=").append(retryPeriodMillis);
        sb.append('}');
        return sb.toString();
    }
}

