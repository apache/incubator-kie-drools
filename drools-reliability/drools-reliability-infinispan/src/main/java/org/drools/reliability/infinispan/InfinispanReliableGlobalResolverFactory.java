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
package org.drools.reliability.infinispan;

import org.drools.core.common.Storage;
import org.drools.reliability.core.ReliableGlobalResolver;
import org.drools.reliability.core.ReliableGlobalResolverFactory;
import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.infinispan.proto.ProtoStreamReliableGlobalResolver;

public class InfinispanReliableGlobalResolverFactory implements ReliableGlobalResolverFactory {

    @Override
    public ReliableGlobalResolver createReliableGlobalResolver(Storage<String, Object> storage) {
        if (((InfinispanStorageManager) StorageManagerFactory.get().getStorageManager()).isProtoStream()) {
            return new ProtoStreamReliableGlobalResolver(storage);
        } else {
            return new ReliableGlobalResolver(storage);
        }
    }

    @Override
    public int servicePriority() {
        return 0;
    }

    @Override
    public String serviceTag() {
        return "infinispan";
    }
}
