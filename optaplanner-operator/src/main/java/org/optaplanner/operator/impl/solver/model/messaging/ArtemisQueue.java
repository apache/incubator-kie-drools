/*
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

package org.optaplanner.operator.impl.solver.model.messaging;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.Singular;
import io.fabric8.kubernetes.model.annotation.Version;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Group(ArtemisQueue.GROUP)
@Plural(ArtemisQueue.PLURAL)
@Singular(ArtemisQueue.SINGULAR)
@Version(ArtemisQueue.API_VERSION)
@Kind(ArtemisQueue.KIND)
public final class ArtemisQueue extends CustomResource<ArtemisQueueSpec, ArtemisQueue.ArtemisQueueStatus>
        implements Namespaced {

    public static final String GROUP = "broker.amq.io";
    public static final String PLURAL = "activemqartemisaddresses";
    public static final String SINGULAR = "activemqartemisaddress";
    public static final String API_VERSION = "v1beta1";
    public static final String KIND = "ActiveMQArtemisAddress";

    static class ArtemisQueueStatus {
        // Not interested in the status.
    }
}
