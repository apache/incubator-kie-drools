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
package org.drools.core.reteoo;

import org.drools.core.common.DefaultFactHandle;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultFactHandleFactoryTest {

    /*
     * Class under test for FactHandle newFactHandle()
     */
    @Test
    public void testNewFactHandle() {
        final ReteooFactHandleFactory factory = new ReteooFactHandleFactory();
        DefaultFactHandle handle = (DefaultFactHandle) factory.newFactHandle( "cheese", null, null, null );
        assertThat(handle.getId()).isEqualTo(1);
        assertThat(handle.getRecency()).isEqualTo(1);

        // issue  new handle
        handle = (DefaultFactHandle) factory.newFactHandle( "cheese", null, null, null );
        assertThat(handle.getId()).isEqualTo(2);
        assertThat(handle.getRecency()).isEqualTo(2);

        // issue  new handle, under a different reference so we  can destroy later        
        final DefaultFactHandle handle2 = (DefaultFactHandle) factory.newFactHandle( "cheese", null, null, null );
        assertThat(handle2.getId()).isEqualTo(3);
        assertThat(handle2.getRecency()).isEqualTo(3);

        // Check recency increasion works
        factory.increaseFactHandleRecency( handle );
        assertThat(handle.getRecency()).isEqualTo(4);

        // issue new handle and make sure  recency is still inline
        handle = (DefaultFactHandle) factory.newFactHandle( "cheese", null, null, null );
        assertThat(handle.getId()).isEqualTo(4);
        assertThat(handle.getRecency()).isEqualTo(5);

        // destroy handle
        factory.destroyFactHandle( handle2 );

        //@FIXME recycling is currently disabled
//        // issue  new  fact handle and  make sure it  recycled the  id=2
//        handle = (DefaultFactHandle) factory.newFactHandle( "cheese", false, null );
//        assertEquals( 2,
//                      handle.getId() );
//        assertEquals( 5,
//                      handle.getRecency() );
//
//        // issue new  handle  making  sure it correctly resumes  ids  and recency
//        handle = (DefaultFactHandle) factory.newFactHandle( "cheese", false, null );
//        assertEquals( 4,
//                      handle.getId() );
//        assertEquals( 6,
//                      handle.getRecency() );

    }

}
