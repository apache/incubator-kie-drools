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

package org.drools.reliability.test;

import org.drools.reliability.core.ReliableGlobalResolverFactory;
import org.drools.reliability.core.SimpleReliableObjectStoreFactory;
import org.drools.reliability.core.StorageManagerFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ExtendWith(BeforeAllMethodExtension.class)
class FactoryServiceTest {

    @Test
    void getStorageManagerFactoryWithDifferentReliabilityPersistanceLayer_shouldThrowException() {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
            StorageManagerFactory.get("infinispan");
            StorageManagerFactory.get("h2mvstore");
        });
    }

    @Test
    void getReliableGlobalResolverFactoryWithDifferentReliabilityPersistanceLayer_shouldThrowException() {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
            ReliableGlobalResolverFactory.get("infinispan");
            ReliableGlobalResolverFactory.get("core");
        });
    }

    @Test
    void getSimpleReliableObjectStoreFactoryWithDifferentReliabilityPersistanceLayer_shouldThrowException() {
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> {
            SimpleReliableObjectStoreFactory.get("infinispan");
            SimpleReliableObjectStoreFactory.get("core");
        });
    }
}
