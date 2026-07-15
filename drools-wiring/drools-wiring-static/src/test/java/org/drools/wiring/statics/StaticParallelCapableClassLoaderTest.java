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
package org.drools.wiring.statics;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See ParallelCapableClassLoaderTest in drools-wiring-dynamic: parallel capability is per-class,
 * so StaticProjectClassLoader needs its own registration despite the base class' one
 * (issue #6758). registerAsParallelCapable() in the probe returns {@code false} when any
 * ancestor class is not itself registered.
 */
class StaticParallelCapableClassLoaderTest {

    @Test
    void staticProjectClassLoaderChainIsParallelCapable() {
        assertThat(StaticProbe.PARALLEL_CAPABLE)
                .as("registerAsParallelCapable() in a subclass only succeeds when the whole "
                        + "StaticProjectClassLoader ancestry is registered")
                .isTrue();
        StaticProbe probe = new StaticProbe(getClass().getClassLoader());
        Object lockA = probe.lockFor("com.example.A");
        assertThat(lockA).as("a parallel-capable loader must not lock on itself").isNotSameAs(probe);
        assertThat(probe.lockFor("com.example.B")).isNotSameAs(lockA);
    }

    private static class StaticProbe extends StaticProjectClassLoader {
        private static final boolean PARALLEL_CAPABLE = registerAsParallelCapable();

        private StaticProbe(ClassLoader parent) {
            super(parent, null);
        }

        private Object lockFor(String className) {
            return getClassLoadingLock(className);
        }
    }
}
