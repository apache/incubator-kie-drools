/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.cloud.kubernetes.client.operations;

import java.util.HashMap;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

public class MapWalkerTest {

    public MapWalkerTest() {

    }

    @Test
    public void whenMapIsEmptyAndIsSafe() {
        final MapWalker walker = new MapWalker(new HashMap<>(), true);
        assertThat(walker.mapToListMap("test").listToMap(0).asMap(), notNullValue());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void whenMapIsEmptyAndIsNotSafe() {
        final MapWalker walker = new MapWalker(new HashMap<>());
       walker.mapToListMap("test").listToMap(0).asMap();
       fail("Should explode an exception while is not safe walking through an empty map");
    }

}
