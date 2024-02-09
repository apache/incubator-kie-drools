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
package org.kie.api.internal.utils;

public interface KieService extends Comparable<KieService> {

    String UNDEFINED = "undefined";

    default int servicePriority() {
        return 0;
    }

    // Override when you want to load a service based on a tag
    default String serviceTag() {
        return UNDEFINED;
    }

    @Override
    default int compareTo(KieService other) {
        if (servicePriority() == other.servicePriority()) {
            throw new IllegalStateException("Found 2 services with same priority (" + servicePriority() + "): " + this.getClass().getCanonicalName() + " and " + other.getClass().getCanonicalName());
        }
        return servicePriority() - other.servicePriority();
    }

    static <T extends KieService> T load(Class<T> serviceClass) {
        return KieServiceLoader.INSTANCE.lookup(serviceClass);
    }

    static <T extends KieService> T loadWithTag(Class<T> serviceClass, String tag) {
        return KieServiceLoader.INSTANCE.lookup(serviceClass, tag);
    }
}
