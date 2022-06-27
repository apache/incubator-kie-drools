/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import org.drools.core.test.model.Cheese;
import org.drools.core.reteoo.ReteooFactHandleFactory;

public class EqualityKeyTest {
    @Test
    public void test1() {
        ReteooFactHandleFactory factory = new ReteooFactHandleFactory();
        
        InternalFactHandle ch1 = factory.newFactHandle( new Cheese ("c", 10), null, null, null );
        EqualityKey key = new EqualityKey( ch1 );

        assertThat(key.getFactHandle()).isSameAs(ch1);
        assertThat(key.size()).isEqualTo(1);
        
        InternalFactHandle ch2 = factory.newFactHandle( new Cheese ("c", 10), null, null, null );
        key.addFactHandle( ch2 );

        assertThat(key.size()).isEqualTo(2);
        assertThat(key.get(1)).isEqualTo(ch2);
        
        key.removeFactHandle( ch1 );
        assertThat(key.getFactHandle()).isSameAs(ch2);
        assertThat(key.size()).isEqualTo(1);
        
        key.removeFactHandle( ch2 );
        assertThat(key.getFactHandle()).isNull();
        assertThat(key.size()).isEqualTo(0);
        
        key = new EqualityKey( ch2 );
        key.addFactHandle( ch1 );
        assertThat(key.getFactHandle()).isSameAs(ch2);
        assertThat(key.size()).isEqualTo(2);
        assertThat(key.get(1)).isEqualTo(ch1);
        
        key.removeFactHandle( ch1 );
        assertThat(key.getFactHandle()).isSameAs(ch2);
        assertThat(key.size()).isEqualTo(1);
        
        key.removeFactHandle( ch2 );
        assertThat(key.getFactHandle()).isNull();
        assertThat(key.size()).isEqualTo(0);
    }
}
