/*
 * Copyright 2019 Red Hat
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
package org.kie.hacep.consumer;

import org.junit.Test;

import static org.junit.Assert.*;

public class BidirectionalMapTest {

    @Test
    public void bidirectionalMapTest(){
        BidirectionalMap map = new BidirectionalMap();
        map.put("one", 1);
        Object res = map.getKey(1);
        assertNotNull(res);
        assertTrue(res.equals("one"));
        assertNotNull(map.removeValue(1));
        map.put("one", 2);
        assertNotNull(map.remove("one"));
    }

    @Test
    public void bidirectionalMapPutTest(){
        BidirectionalMap map = new BidirectionalMap();
        Object previousValue = map.put("one", 1);
        assertNull(previousValue);
    }

    @Test
    public void bidirectionalMapKeyNotPresentTest(){
        BidirectionalMap map = new BidirectionalMap();
        Object res = map.getKey(1);
        assertNull(res);
    }

    @Test
    public void bidirectionalMapRemoveValueTest(){
        BidirectionalMap map = new BidirectionalMap();
        map.put("one", 1);
        Object res = map.getKey(1);
        assertNotNull(res);
        Object result = map.removeValue(1);
        assertNotNull(result);
        Object resultSecond = map.removeValue(2);
        assertNull(resultSecond);
    }

    @Test
    public void bidirectionalMapRemoveTest(){
        BidirectionalMap map = new BidirectionalMap();
        map.put("one", 1);
        Object res = map.getKey(1);
        assertNotNull(res);
        Object result = map.remove("one");
        assertNotNull(result);
        Object resultSecond = map.remove("two");
        assertNull(resultSecond);
    }
}
