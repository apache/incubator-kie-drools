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
package org.drools.model.operators;

import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MatchesOperatorTest {


    @Test
    public void testMatchesOperatorCache() {
        MatchesOperator instance = MatchesOperator.INSTANCE;
        instance.forceCacheSize(100);

        // input maybe null
        assertThat(instance.eval(null,"anything")).isFalse();
        assertThat(instance.mapSize()).isEqualTo(0); // not added to cache with null input
        // cache enabled
        assertThat(instance.eval("a","a")).isTrue();
        assertThat(instance.mapSize()).isEqualTo(1);
        assertThat(instance.eval("a","b")).isFalse();
        assertThat(instance.mapSize()).isEqualTo(2);
        assertThat(instance.eval("a","a")).isTrue();  // regular expression "a" in map.
        assertThat(instance.eval("b","b")).isTrue();  // regular expression "b" in map.
        assertThat(instance.eval("c","a")).isFalse(); // regular expression "a" in map.
        assertThat(instance.eval("c","b")).isFalse(); // regular expression "b" in map.
        assertThat(instance.mapSize()).isEqualTo(2);
    }

    @Test
    public void testMatchesOperatorNoCache() {
        MatchesOperator instance = MatchesOperator.INSTANCE;
        instance.forceCacheSize(0);
        // input maybe null
        assertThat(instance.eval(null,"anything")).isFalse();
        assertThat(instance.eval("a","a")).isTrue();
        assertThat(instance.eval("a","b")).isFalse();
        assertThat(instance.eval("b","a")).isFalse();
        assertThat(instance.eval("b","b")).isTrue();
        assertThat(instance.mapSize()).isEqualTo(0);
    }

    @After
    public void resetCache() {
        MatchesOperator instance = MatchesOperator.INSTANCE;
        instance.reInitialize();
    }

}
