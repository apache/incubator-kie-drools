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
package org.drools.core.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScalablePoolTest {

    public static class Monitor {
        private int newCounter;
        private int resetCounter;
        private int disposeCounter;
    }

    public static class PooledResource {
        private final Monitor monitor;

        public PooledResource( Monitor monitor ) {
            this.monitor = monitor;
            monitor.newCounter++;
        }

        public void reset() {
            monitor.resetCounter++;
        }

        public void dispose() {
            monitor.disposeCounter++;
        }
    }

    @Test
    public void testPoolOf1() {
        Monitor monitor = new Monitor();
        check( monitor, 0, 0, 0 );

        ScalablePool<PooledResource> pool = new ScalablePool<>( 1, () -> new PooledResource( monitor ), PooledResource::reset, PooledResource::dispose );

        // one resource is eagerly created
        check( monitor, 1, 0, 0 );

        PooledResource resource = pool.get();
        check( monitor, 1, 0, 0 );

        pool.release( resource );
        check( monitor, 1, 1, 0 );

        PooledResource resource2 = pool.get();
        assertThat(resource).isSameAs(resource2);
        check( monitor, 1, 1, 0 );

        pool.shutdown();
        check( monitor, 1, 1, 1 );
    }

    @Test
    public void testPool() {
        Monitor monitor = new Monitor();
        check( monitor, 0, 0, 0 );

        ScalablePool<PooledResource> pool = new ScalablePool<>( 3, () -> new PooledResource( monitor ), PooledResource::reset, PooledResource::dispose );

        // resource is eagerly created
        check( monitor, 3, 0, 0 );

        PooledResource resource1 = pool.get();
        PooledResource resource2 = pool.get();
        PooledResource resource3 = pool.get();

        check( monitor, 3, 0, 0 );

        pool.release( resource3 );
        check( monitor, 3, 1, 0 );

        resource3 = pool.get();
        check( monitor, 3, 1, 0 );

        PooledResource resource4 = pool.get();
        check( monitor, 4, 1, 0 );

        PooledResource resource5 = pool.get();
        check( monitor, 5, 1, 0 );

        pool.release( resource4 );
        check( monitor, 5, 2, 0 );

        resource4 = pool.get();
        check( monitor, 5, 2, 0 );

        pool.shutdown();
        check( monitor, 5, 2, 5 );
    }

    private void check( Monitor monitor, int expectedNew, int expectedReset, int expectedDispose ) {
        assertThat(monitor.newCounter).isEqualTo(expectedNew);
        assertThat(monitor.resetCounter).isEqualTo(expectedReset);
        assertThat(monitor.disposeCounter).isEqualTo(expectedDispose);
    }
}
